package c.m.marketplacedesa.ui.user.main

import c.m.marketplacedesa.model.NotificationCollectionResponse
import c.m.marketplacedesa.model.StoreResponse
import c.m.marketplacedesa.util.base.View

interface MainView : View {
    fun showLoading()
    fun hideLoading()
    fun hideSearchLoading()
    fun showNoDataResult()
    fun getStore(storeData: List<StoreResponse>)
    fun returnToCompleteUserProfile()
    fun returnToSignInActivity()
    fun getNotificationCount(notificationData: List<NotificationCollectionResponse>)
}