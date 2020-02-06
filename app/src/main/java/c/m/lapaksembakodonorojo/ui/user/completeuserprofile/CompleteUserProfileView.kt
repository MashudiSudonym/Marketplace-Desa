package c.m.lapaksembakodonorojo.ui.user.completeuserprofile

import c.m.lapaksembakodonorojo.util.base.View

interface CompleteUserProfileView : View {
    fun userPhoneDataFromAuthentication(phone: String)
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun returnMainActivity()
}