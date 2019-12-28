package c.m.marketplacedesa.ui.seller.selleraddproduct

import c.m.marketplacedesa.util.base.View

interface SellerAddProductView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}