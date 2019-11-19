package c.m.marketplacedesa.data.remote.response

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class ProductsResponse {
    var image_product: String? = ""
    var name: String? = ""
    var price: Int? = 0
    var stock: Int? = 0
    var store: String? = ""
    var uid: String? = ""
}