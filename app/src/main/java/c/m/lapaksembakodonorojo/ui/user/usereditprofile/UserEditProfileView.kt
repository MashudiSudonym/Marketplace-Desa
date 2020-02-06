package c.m.lapaksembakodonorojo.ui.user.usereditprofile

import c.m.lapaksembakodonorojo.util.base.View

interface UserEditProfileView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun returnUserProfileActivity()
}