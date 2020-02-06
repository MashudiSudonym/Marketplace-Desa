package c.m.lapaksembakodonorojo.ui.notification

import c.m.lapaksembakodonorojo.model.NotificationCollectionResponse
import c.m.lapaksembakodonorojo.util.base.View

interface NotificationView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoNotificationResult()
    fun getNotification(notificationData: List<NotificationCollectionResponse>)
}