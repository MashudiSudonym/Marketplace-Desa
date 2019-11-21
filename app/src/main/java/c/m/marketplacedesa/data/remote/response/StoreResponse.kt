package c.m.marketplacedesa.data.remote.response

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class StoreResponse {
    var address: String? = ""
    var image_profile_store: String? = ""
    var name: String? = ""
    var owner: String? = ""
    var uid: String? = ""
    var store_geopoint: StoreGeoPoint? = null
}

@IgnoreExtraProperties
class StoreGeoPoint {
    var _latitude: Double? = 0.0
    var _longitude: Double? = 0.0
}