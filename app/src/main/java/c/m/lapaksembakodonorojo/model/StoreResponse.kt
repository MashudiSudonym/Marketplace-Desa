package c.m.lapaksembakodonorojo.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class StoreResponse {
    var address: String? = ""
    var image_profile_store: String? = ""
    var name: String? = ""
    var owner: String? = ""
    var uid: String? = ""
    var store_geopoint = GeoPoint(0.0, 0.0)
    var phone: String? = ""
}