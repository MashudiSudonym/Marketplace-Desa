package c.m.marketplacedesa.data.remote.response

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class StoreResponse {
    var address: String? = ""
    var image_profile_store: String? = ""
    var name: String? = ""
    var owner: String? = ""
    var uid: String? = ""
}