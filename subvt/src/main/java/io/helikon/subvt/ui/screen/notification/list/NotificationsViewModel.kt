package io.helikon.subvt.ui.screen.notification.list

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.helikon.subvt.data.repository.NotificationRepository
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        notificationRepository: NotificationRepository,
    ) : ViewModel() {
        val notifications = notificationRepository.allNotifications
    }
