package c.m.marketplacedesa.ui.seller.sellereditstoreinformation

import c.m.marketplacedesa.util.base.View

interface SellerEditStoreInformationView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}