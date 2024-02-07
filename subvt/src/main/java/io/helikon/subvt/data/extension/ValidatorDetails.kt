package io.helikon.subvt.data.extension

import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.data.model.substrate.Nomination
import io.helikon.subvt.util.truncateAddress
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.math.BigInteger

fun ValidatorDetails.nominationTotal(): BigInteger = this.nominations.sumOf { it.stake.activeAmount }

fun ValidatorDetails.inactiveNominations(): ImmutableList<Nomination> =
    if (this.validatorStake == null) {
        this.nominations
    } else {
        this.nominations.filter { nomination ->
            this.validatorStake!!.nominators.find {
                it.account.id == nomination.stashAccount.id
            } == null
        }.toImmutableList()
    }

fun ValidatorDetails.inactiveNominationTotal(): BigInteger = this.inactiveNominations().sumOf { it.stake.activeAmount }

fun ValidatorDetails.hasParentIdentity() = this.account.parent?.identity != null

fun ValidatorDetails.parentIdentityConfirmed() = this.account.parent?.identity?.confirmed == true

fun ValidatorDetails.hasIdentity() = this.account.identity != null

fun ValidatorDetails.identityConfirmed() = this.account.identity?.confirmed == true

fun ValidatorDetails.parentDisplay(): String? =
    if (this.account.parent?.identity?.display?.isNotEmpty() == true) {
        this.account.parent?.identity?.display
    } else {
        null
    }

fun ValidatorDetails.childDisplay(): String? =
    if (this.account.childDisplay?.isNotEmpty() == true) {
        this.account.childDisplay
    } else {
        null
    }

fun ValidatorDetails.display(): String? =
    if (this.account.identity?.display?.isNotEmpty() == true) {
        this.account.identity?.display
    } else {
        null
    }

fun ValidatorDetails.identityDisplay(): String =
    if (this.parentDisplay()?.isNotEmpty() == true) {
        "${this.parentDisplay()} / ${this.childDisplay() ?: this.parentDisplay()}"
    } else if (this.display()?.isNotEmpty() == true) {
        "${this.display()}"
    } else {
        truncateAddress(this.account.address)
    }
