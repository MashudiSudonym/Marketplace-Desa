package c.m.lapaksembakodonorojo.ui.seller.selleraddproduct

import c.m.lapaksembakodonorojo.util.base.View

interface SellerAddProductView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}