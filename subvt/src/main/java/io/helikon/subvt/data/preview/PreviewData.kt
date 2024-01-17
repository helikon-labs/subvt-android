package io.helikon.subvt.data.preview

import io.helikon.subvt.data.model.Network

abstract class PreviewData {
    companion object {
        val networks =
            listOf(
                Network(
                    id = 1,
                    hash = "0x0",
                    display = "Kusama",
                    tokenTicker = "KSM",
                    tokenDecimalCount = 12,
                    ss58Prefix = 2,
                ),
                Network(
                    id = 2,
                    hash = "0x1",
                    display = "Polkadot",
                    tokenTicker = "DOT",
                    tokenDecimalCount = 10,
                    ss58Prefix = 0,
                ),
            )
    }
}
