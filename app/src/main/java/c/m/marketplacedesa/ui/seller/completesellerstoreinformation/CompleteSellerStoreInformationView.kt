package c.m.marketplacedesa.ui.seller.completesellerstoreinformation

import c.m.marketplacedesa.util.base.View

interface CompleteSellerStoreInformationView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}