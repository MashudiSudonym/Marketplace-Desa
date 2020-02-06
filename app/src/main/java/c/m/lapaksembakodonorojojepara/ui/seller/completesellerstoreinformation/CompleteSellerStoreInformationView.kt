package c.m.lapaksembakodonorojojepara.ui.seller.completesellerstoreinformation

import c.m.lapaksembakodonorojojepara.util.base.View

interface CompleteSellerStoreInformationView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun finishThisActivityToNextActivity(storeUID: String)
}