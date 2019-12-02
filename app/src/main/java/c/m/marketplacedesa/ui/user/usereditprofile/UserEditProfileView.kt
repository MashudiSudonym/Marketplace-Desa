package c.m.marketplacedesa.ui.user.usereditprofile

import c.m.marketplacedesa.util.base.View

interface UserEditProfileView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun returnUserProfileActivity()
}