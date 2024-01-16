package io.helikon.subvt.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "network")
data class Network(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "hash")
    val hash: String,
    @ColumnInfo(name = "display")
    val display: String,
    @ColumnInfo(name = "token_ticker")
    val tokenTicker: String,
    @ColumnInfo(name = "token_decimal_count")
    val tokenDecimalCount: Int,
    @ColumnInfo(name = "ss58_prefix")
    val ss58Prefix: Int,
    @ColumnInfo(name = "network_status_service_host")
    val networkStatusServiceHost: String?,
    @ColumnInfo(name = "network_status_service_port")
    val networkStatusServicePort: Int?,
    @ColumnInfo(name = "report_service_host")
    val reportServiceHost: String?,
    @ColumnInfo(name = "report_service_port")
    val reportServicePort: Int?,
    @ColumnInfo(name = "validator_details_service_host")
    val validatorDetailsServiceHost: String?,
    @ColumnInfo(name = "validator_details_service_port")
    val validatorDetailsServicePort: Int?,
    @ColumnInfo(name = "active_validator_list_service_host")
    val activeValidatorListServiceHost: String?,
    @ColumnInfo(name = "active_validator_list_service_port")
    val activeValidatorListServicePort: Int?,
    @ColumnInfo(name = "inactive_validator_list_service_host")
    val inactiveValidatorListServiceHost: String?,
    @ColumnInfo(name = "inactive_validator_list_service_port")
    val inactiveValidatorListServicePort: Int?,
) {
    companion object {
        fun from(network: io.helikon.subvt.data.model.app.Network) =
            Network(
                id = network.id,
                hash = network.hash,
                display = network.display,
                tokenTicker = network.tokenTicker,
                tokenDecimalCount = network.tokenDecimalCount,
                ss58Prefix = network.ss58Prefix,
                networkStatusServiceHost = network.networkStatusServiceHost,
                networkStatusServicePort = network.networkStatusServicePort,
                reportServiceHost = network.reportServiceHost,
                reportServicePort = network.reportServicePort,
                validatorDetailsServiceHost = network.validatorDetailsServiceHost,
                validatorDetailsServicePort = network.validatorDetailsServicePort,
                activeValidatorListServiceHost = network.activeValidatorListServiceHost,
                activeValidatorListServicePort = network.activeValidatorListServicePort,
                inactiveValidatorListServiceHost = network.inactiveValidatorListServiceHost,
                inactiveValidatorListServicePort = network.inactiveValidatorListServicePort,
            )
    }
}
