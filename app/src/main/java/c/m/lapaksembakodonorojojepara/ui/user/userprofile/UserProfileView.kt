package c.m.lapaksembakodonorojojepara.ui.user.userprofile

import c.m.lapaksembakodonorojojepara.model.UsersResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface UserProfileView : View {
    fun getProfile(profileData: List<UsersResponse>)
}