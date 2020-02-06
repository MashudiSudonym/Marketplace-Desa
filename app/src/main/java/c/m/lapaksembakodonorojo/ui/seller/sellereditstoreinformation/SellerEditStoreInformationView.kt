package c.m.lapaksembakodonorojo.ui.seller.sellereditstoreinformation

import c.m.lapaksembakodonorojo.util.base.View

interface SellerEditStoreInformationView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}