package io.helikon.subvt.util

import java.math.BigInteger
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.max

private val DECIMAL_FORMAT = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))

fun formatDecimal(
    number: BigInteger,
    tokenDecimalCount: Int,
    formatDecimalCount: Int = 4,
): String {
    var balanceString = number.toString()
    val paddingZeroCount =
        max(
            0,
            tokenDecimalCount + 1 - balanceString.length,
        )
    balanceString = "0".repeat(paddingZeroCount) + balanceString
    var decimalsString = balanceString.takeLast(tokenDecimalCount)
    var integerString = balanceString.take(balanceString.length - decimalsString.length)
    integerString = DECIMAL_FORMAT.format(integerString.toLong())
    if (formatDecimalCount == 0) {
        return integerString
    }
    decimalsString = decimalsString.take(formatDecimalCount)
    return "$integerString.$decimalsString"
}
