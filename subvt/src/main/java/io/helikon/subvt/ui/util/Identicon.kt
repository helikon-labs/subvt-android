package io.helikon.subvt.ui.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import io.helikon.subvt.data.model.substrate.AccountId
import org.bouncycastle.jcajce.provider.digest.Blake2b
import kotlin.math.floor

private val SCHEMES =
    mutableListOf(
        Scheme("target", 1, arrayOf(0, 28, 0, 0, 28, 0, 0, 28, 0, 0, 28, 0, 0, 28, 0, 0, 28, 0, 1)),
        Scheme("cube", 20, arrayOf(0, 1, 3, 2, 4, 3, 0, 1, 3, 2, 4, 3, 0, 1, 3, 2, 4, 3, 5)),
        Scheme("quazar", 16, arrayOf(1, 2, 3, 1, 2, 4, 5, 5, 4, 1, 2, 3, 1, 2, 4, 5, 5, 4, 0)),
        Scheme("flower", 32, arrayOf(0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3)),
        Scheme("cyclic", 32, arrayOf(0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5, 6)),
        Scheme("vmirror", 128, arrayOf(0, 1, 2, 3, 4, 5, 3, 4, 2, 0, 1, 6, 7, 8, 9, 7, 8, 6, 10)),
        Scheme("hmirror", 128, arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 8, 6, 7, 5, 3, 4, 2, 11)),
    )

private data class Scheme(
    val name: String,
    val frequency: Int,
    val colors: Array<Int>,
)

private fun findScheme(d: Int): Scheme {
    var cum = 0
    SCHEMES.forEach {
        val n = it.frequency
        cum += n
        if (d < cum) {
            return it
        }
    }
    throw RuntimeException()
}

internal fun getIdenticonColors(accountId: AccountId): List<Color> {
    val accountIdBytes = accountId.getBytes()
    val totalFreq = SCHEMES.fold(0) { summ, schema -> summ + schema.frequency }

    val zeroHash = Blake2b.Blake2b512().digest(ByteArray(32) { 0 })
    val idHash =
        Blake2b.Blake2b512().digest(accountIdBytes)
            .mapIndexed { index, byte -> (byte + 256 - zeroHash[index]) % 256 }

    val sat = (floor(idHash[29].toDouble() * 70 / 256 + 26) % 80) + 30
    val d = floor((idHash[30].toDouble() + idHash[31].toDouble() * 256) % totalFreq)
    val scheme = findScheme(d.toInt())

    val palette =
        idHash.mapIndexed { index, byte ->
            val resultColor: Color =
                when (val b = (byte + index % 28 * 58) % 256) {
                    0 -> {
                        Color(0xFF444444)
                    }

                    255 -> {
                        Color(0x00000000)
                    }

                    else -> {
                        val h = floor(b.toDouble() % 64 * 360 / 64)
                        val array = arrayOf(53, 15, 35, 75)
                        val l = array[floor(b.toDouble() / 64).toInt()]
                        val rgb =
                            ColorUtils.HSLToColor(
                                floatArrayOf(
                                    h.toFloat(),
                                    sat.toFloat() * 0.015f,
                                    l.toFloat() * 0.01f,
                                ),
                            )
                        Color(rgb)
                    }
                }
            resultColor
        }
    val rot = (idHash[28] % 6) * 3
    return scheme.colors.mapIndexed { index, _ ->
        palette[scheme.colors[if (index < 18) (index + rot) % 18 else 18]]
    }
}
