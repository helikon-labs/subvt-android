package io.helikon.subvt.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Entity(tableName = "notification")
@Parcelize
data class Notification(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: UUID,
    @ColumnInfo(name = "is_read")
    val isRead: Boolean,
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "network_id")
    val networkId: Long,
    @ColumnInfo(name = "notification_type_code")
    val notificationTypeCode: String,
    @ColumnInfo(name = "received_at")
    val receivedAt: Date,
    @ColumnInfo(name = "validator_account_id")
    val validatorAccountId: String,
    @ColumnInfo(name = "validator_display")
    val validatorDisplay: String,
) : Parcelable
