package c.m.lapaksembakodonorojojepara.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class ProductsResponse {
    var image_product: String? = ""
    var name: String? = ""
    var price: Int? = 0
    var stock: Boolean? = false
    var store: String? = ""
    var uid: String? = ""
}