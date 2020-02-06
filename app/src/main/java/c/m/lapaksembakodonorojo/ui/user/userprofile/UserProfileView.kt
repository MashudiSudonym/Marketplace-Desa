package c.m.lapaksembakodonorojo.ui.user.userprofile

import c.m.lapaksembakodonorojo.model.UsersResponse
import c.m.lapaksembakodonorojo.util.base.View

interface UserProfileView : View {
    fun getProfile(profileData: List<UsersResponse>)
}