package c.m.marketplacedesa.ui.user.userprofile

import c.m.marketplacedesa.model.UsersResponse
import c.m.marketplacedesa.util.base.View

interface UserProfileView : View {
    fun getProfile(profileData: List<UsersResponse>)
}