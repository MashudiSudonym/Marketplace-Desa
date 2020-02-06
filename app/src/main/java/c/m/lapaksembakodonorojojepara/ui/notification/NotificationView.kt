package c.m.lapaksembakodonorojojepara.ui.notification

import c.m.lapaksembakodonorojojepara.model.NotificationCollectionResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface NotificationView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoNotificationResult()
    fun getNotification(notificationData: List<NotificationCollectionResponse>)
}