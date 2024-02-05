package io.helikon.subvt.data.preview

import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.NetworkStatus
import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.Epoch
import io.helikon.subvt.data.model.substrate.Era
import io.helikon.subvt.data.model.substrate.InactiveNominationsSummary
import io.helikon.subvt.data.model.substrate.StakeSummary
import io.helikon.subvt.data.model.substrate.ValidatorPreferences
import io.helikon.subvt.data.model.substrate.ValidatorStakeSummary
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
                averageStake = BigInteger("${7_677_000_000_000_000L}"),
                medianStake = BigInteger("${12_876_000_000_000_000L}"),
                eraRewardPoints = 1_386_680,
            )

        val validatorSummary =
            ValidatorSummary(
                accountId = stashAccountId,
                address = stashAccountId.getAddress(prefix = networks[0].ss58Prefix.toShort()),
                controllerAccountId = controllerAccountId,
                networkId = networks[0].id,
                display = "Display",
                parentDisplay = "Parent",
                childDisplay = "Child",
                confirmed = true,
                preferences =
                    ValidatorPreferences(
                        commissionPerBillion = 0,
                        blocksNominations = true,
                    ),
                selfStake =
                    StakeSummary(
                        stashAccountId = stashAccountId.getAddress(networks[0].ss58Prefix.toShort()),
                        activeAmount = BigInteger("0"),
                    ),
                isActive = true,
                isActiveNextSession = true,
                inactiveNominations =
                    InactiveNominationsSummary(
                        nominationCount = 130,
                        totalAmount = BigInteger("0"),
                    ),
                oversubscribed = true,
                slashCount = 0,
                isEnrolledIn1KV = true,
                isParaValidator = true,
                paraId = 1000,
                returnRatePerBillion = 150000000,
                blocksAuthored = 3,
                rewardPoints = 1020,
                heartbeatReceived = true,
                validatorStake =
                    ValidatorStakeSummary(
                        selfStake = BigInteger("15937871000000"),
                        totalStake = BigInteger("5031267908000000"),
                        nominatorCount = 12,
                    ),
            )
    }
}
