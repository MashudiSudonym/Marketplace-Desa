package c.m.lapaksembakodonorojojepara.ui.user.usereditprofile

import c.m.lapaksembakodonorojojepara.util.base.View

interface UserEditProfileView : View {
    fun showProgressDialog()
    fun closeProgressDialog()
    fun progressDialogMessage(message: String)
    fun returnUserProfileActivity()
}