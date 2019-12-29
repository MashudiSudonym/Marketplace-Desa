package c.m.marketplacedesa.ui.seller.sellereditproduct

import c.m.marketplacedesa.util.base.View

interface SellerEditProductView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}