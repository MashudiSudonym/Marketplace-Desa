package c.m.lapaksembakodonorojojepara.ui.seller.sellereditproduct

import c.m.lapaksembakodonorojojepara.util.base.View

interface SellerEditProductView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}