package c.m.lapaksembakodonorojo.ui.seller.sellereditproduct

import c.m.lapaksembakodonorojo.util.base.View

interface SellerEditProductView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}