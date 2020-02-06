package c.m.lapaksembakodonorojojepara.util.webservice

import c.m.lapaksembakodonorojojepara.model.MessageNotification
import c.m.lapaksembakodonorojojepara.model.MessageNotificationResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAAQod71-0:APA91bH8d0ya5DlmPjgfJ9jS-kDyyhGadQxUyUFAQM_o70bYlh81vomX1C3WR_8KSd0bG-3TX8rrukM2lm7b5ZLMSrpbm0Zav84XqOQXQpAD6IxTzmxQOs2MVbgrm5Q8NhdHsMKcImwz"
    )
    @POST("fcm/send")
    suspend fun postMessage(@Body messageNotification: MessageNotification): MessageNotificationResponse
}