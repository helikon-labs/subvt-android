package io.helikon.subvt.data.extension

import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.util.truncateAddress
import java.math.BigInteger

enum class ValidatorSortOption {
    IDENTITY,
    STAKE_DESCENDING,
    NOMINATION_DESCENDING,
}

enum class ValidatorFilterOption {
    HAS_IDENTITY,
    IS_ONEKV,
    IS_PARA_VALIDATOR,
}

fun ValidatorSummary.hasIdentity(): Boolean =
    (this.parentDisplay?.isNotEmpty() == true) ||
        (this.display?.isNotEmpty() == true)

fun ValidatorSummary.identityDisplay(): String =
    if (this.parentDisplay?.isNotEmpty() == true) {
        "${this.parentDisplay} / ${this.childDisplay ?: this.parentDisplay}"
    } else if (this.display?.isNotEmpty() == true) {
        "${this.display}"
    } else {
        truncateAddress(this.address)
    }

fun ValidatorSummary.filter(filter: String) =
    if (filter.isEmpty()) {
        true
    } else {
        val fullText =
            (display ?: "") +
                (parentDisplay ?: "") +
                (childDisplay ?: "") +
                address
        fullText.lowercase().contains(filter.lowercase())
    }

val validatorIdentityComparator =
    Comparator<ValidatorSummary> { v1, v2 ->
        if (v1.networkId != v2.networkId) {
            v1.networkId.compareTo(v2.networkId)
        } else if (v1.hasIdentity()) {
            if (v2.hasIdentity()) {
                v1.identityDisplay().uppercase().compareTo(v2.identityDisplay().uppercase())
            } else {
                -1
            }
        } else {
            if (v2.hasIdentity()) {

                1
            } else {
                v1.address.compareTo(v2.address)
            }
        }
    }

val validatorStakeComparator =
    Comparator<ValidatorSummary> { v1, v2 ->
        val v1Balance = v1.validatorStake?.totalStake ?: BigInteger("0")
        val v2Balance = v2.validatorStake?.totalStake ?: BigInteger("0")
        v2Balance.compareTo(v1Balance)
    }

val validatorNominationComparator =
    Comparator<ValidatorSummary> { v1, v2 ->
        v2.inactiveNominations.totalAmount.compareTo(
            v1.inactiveNominations.totalAmount,
        )
    }
