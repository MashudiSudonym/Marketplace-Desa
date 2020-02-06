package c.m.lapaksembakodonorojojepara.ui.seller.selleraddproduct

import c.m.lapaksembakodonorojojepara.util.base.View

interface SellerAddProductView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}