package c.m.lapaksembakodonorojo.ui.seller.completesellerstoreinformation

import c.m.lapaksembakodonorojo.util.base.View

interface CompleteSellerStoreInformationView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}