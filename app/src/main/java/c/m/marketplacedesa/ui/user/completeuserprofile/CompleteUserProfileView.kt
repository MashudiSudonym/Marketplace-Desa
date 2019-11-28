package c.m.marketplacedesa.ui.user.completeuserprofile

import c.m.marketplacedesa.util.base.View

interface CompleteUserProfileView : View {
    fun userPhoneDataFromAuthentication(phone: String)
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun returnMainActivity()
}