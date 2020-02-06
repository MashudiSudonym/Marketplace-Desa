package c.m.lapaksembakodonorojojepara.ui.user.completeuserprofile

import c.m.lapaksembakodonorojojepara.util.base.View

interface CompleteUserProfileView : View {
    fun userPhoneDataFromAuthentication(phone: String)
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun returnMainActivity()
}