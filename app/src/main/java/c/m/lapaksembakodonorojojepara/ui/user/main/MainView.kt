package c.m.lapaksembakodonorojojepara.ui.user.main

import c.m.lapaksembakodonorojojepara.model.NotificationCollectionResponse
import c.m.lapaksembakodonorojojepara.model.StoreResponse
import c.m.lapaksembakodonorojojepara.util.base.View

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