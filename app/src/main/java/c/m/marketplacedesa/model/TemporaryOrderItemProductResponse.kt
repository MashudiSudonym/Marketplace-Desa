package c.m.marketplacedesa.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class TemporaryOrderItemProductResponse {
    var uid: String? = ""
    var total_price: Int? = 0
    var payment_status: Boolean = false
    var order_status: Boolean = false
    var order_number: String? = ""
    var order_by: String? = ""
    var number_of_product_orders: Int? = 0
    var name: String? = ""
    var is_canceled: Boolean = false
    var image_product: String? = ""
    var delivery_option: Int? = 1
    var user_order_uid: String? = ""
}