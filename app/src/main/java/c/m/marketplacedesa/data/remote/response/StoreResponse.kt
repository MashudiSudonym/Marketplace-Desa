package c.m.marketplacedesa.data.remote.response

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class StoreResponse {
    var address: String? = ""
    var created_at: Date? = null
    var edited_at: Date? = null
    var image_profile_store: String? = ""
    var name: String? = ""
    var owner: String? = ""
    var uid: String? = ""
}