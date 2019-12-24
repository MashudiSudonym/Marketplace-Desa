package c.m.marketplacedesa.model

import com.google.gson.annotations.SerializedName


data class MessageNotificationResponse(
    @SerializedName("message_id")
    val messageId: Long
)