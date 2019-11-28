package c.m.marketplacedesa.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class UsersResponse {
    var image_profile: String? = ""
    var name: String? = ""
    var phone: String? = ""
    var seller: Boolean = false
    var uid: String? = ""
    var address: String? = ""
}