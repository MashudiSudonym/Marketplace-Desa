package c.m.lapaksembakodonorojo.ui.user.main

import c.m.lapaksembakodonorojo.model.NotificationCollectionResponse
import c.m.lapaksembakodonorojo.model.StoreResponse
import c.m.lapaksembakodonorojo.util.base.View

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