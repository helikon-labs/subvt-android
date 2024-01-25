package io.helikon.subvt.data.preview

import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.NetworkStatus
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.Epoch
import io.helikon.subvt.data.model.substrate.Era
import java.math.BigInteger

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

        private val era =
            Era(
                index = 3926,
                startTimestamp = 1657214874008,
                endTimestamp = 1657236474008,
            )

        private val epoch =
            Epoch(
                index = 3926,
                startBlockNumber = 1657214874008,
                startTimestamp = 1657214874008,
                endTimestamp = 1657236474008,
            )

        val stashAccountId =
            AccountId(
                hex = "0xDC89C6865C029C1088FB27B41C1A715B0BB611B94E1D625FA0BB8A1294187454",
            )
        val controllerAccountId =
            AccountId(
                hex = "0xC4F34F45D16DA9B285984A37A9FE72C7DB4A96E267B9A5313BE5DEC1A0843352",
            )

        val networkStatus =
            NetworkStatus(
                finalizedBlockNumber = 22_345_676,
                finalizedBlockHash = "0xABC",
                bestBlockNumber = 22_345_678,
                bestBlockHash = "0xDEF",
                activeEra = era,
                currentEpoch = epoch,
                activeValidatorCount = 1000,
                inactiveValidatorCount = 1234,
                lastEraTotalReward = BigInteger("${923_764_834_000_000L}"),
                totalStake = BigInteger("${7_677_123_000_000_000_000L}"),
                returnRatePerMillion = 144_800,
                minStake = BigInteger("${6_999_000_000_000_000L}"),
                maxStake = BigInteger("${70_195_000_000_000_000L}"),
                averageStake = BigInteger("${7_677_123_000_000_000_000L}"),
                medianStake = BigInteger("${12_876_000_000_000_000L}"),
                eraRewardPoints = 1_386_680,
            )
    }
}
