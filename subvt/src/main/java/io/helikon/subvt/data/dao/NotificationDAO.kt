package io.helikon.subvt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.helikon.subvt.data.model.Notification
import java.util.UUID

@Dao
interface NotificationDAO {
    @Query("SELECT * from notification")
    fun getAll(): LiveData<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: Notification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<Notification>)

    @Query("SELECT * FROM notification WHERE id = :id")
    fun findById(id: UUID): Notification?
}
