package io.helikon.subvt.data.repository

import androidx.lifecycle.LiveData
import io.helikon.subvt.data.dao.NotificationDAO
import io.helikon.subvt.data.model.Notification
import java.util.UUID

class NotificationRepository(private val dao: NotificationDAO) {
    val allNotifications: LiveData<List<Notification>> = dao.getAll()

    suspend fun add(notification: Notification) {
        dao.insert(notification)
    }

    suspend fun addAll(notifications: List<Notification>) {
        dao.insertAll(notifications)
    }

    suspend fun findById(id: UUID): Notification? = dao.findById(id)
}
