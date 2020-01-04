package c.m.marketplacedesa.ui.notification

import c.m.marketplacedesa.model.NotificationCollectionResponse
import c.m.marketplacedesa.util.base.View

interface NotificationView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoNotificationResult()
    fun getNotification(notificationData: List<NotificationCollectionResponse>)
}