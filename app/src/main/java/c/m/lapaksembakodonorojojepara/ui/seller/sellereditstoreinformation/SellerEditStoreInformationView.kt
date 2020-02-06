package c.m.lapaksembakodonorojojepara.ui.seller.sellereditstoreinformation

import c.m.lapaksembakodonorojojepara.util.base.View

interface SellerEditStoreInformationView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}