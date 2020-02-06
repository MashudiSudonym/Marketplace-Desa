package c.m.lapaksembakodonorojojepara.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class NotificationCollectionResponse {
    var order_title_message: String? = ""
    var order_body_message: String? = ""
    var order_number: String? = ""
    var user_uid: String? = ""
    var store_uid: String? = ""
    var read_notification: Boolean = false
    var uid: String? = ""
}