package c.m.marketplacedesa.ui.user.userordercart

import android.util.Log
import c.m.marketplacedesa.model.MessageNotification
import c.m.marketplacedesa.model.Notification
import c.m.marketplacedesa.model.NotificationCollectionResponse
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.base.Presenter
import c.m.marketplacedesa.util.webservice.ApiInterface
import c.m.marketplacedesa.util.webservice.RetrofitService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserOrderCartPresenter : Presenter<UserOrderCartView> {
    private var mView: UserOrderCartView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null
    private val apiService =
        RetrofitService.getInstance("https://fcm.googleapis.com/").create(ApiInterface::class.java)

    override fun onAttach(view: UserOrderCartView) {
        mView = view
    }

    override fun onDetach() {
        mView = null
    }

    fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        authentication = FirebaseAuth.getInstance()
    }

    private fun userAuthentication() = authentication?.currentUser != null

    fun getTemporaryOrder(orderNumber: String) {
        if (userAuthentication()) {
            db?.collection("temporary_order_item_product")
                ?.whereEqualTo("order_number", orderNumber)
                ?.get()
                ?.addOnSuccessListener { snapshot ->
                    val temporaryOrderList =
                        snapshot?.toObjects(TemporaryOrderItemProductResponse::class.java)

                    mView?.getTemporaryOrder(temporaryOrderList as List<TemporaryOrderItemProductResponse>)
                }
                ?.addOnFailureListener { Log.e(Constants.ERROR_TAG, "$it") }
        }
    }

    fun sendOrderNotification(
        storeUID: String,
        orderNumber: String,
        storeOwnerUID: String,
        orderBodyMessage: String,
        orderTitleMessage: String
    ) {
        val messageNotification = MessageNotification(
            "/topics/$storeOwnerUID",
            Notification(orderBodyMessage, orderTitleMessage)
        )

        CoroutineScope(Dispatchers.IO).launch {
            apiService.postMessage(messageNotification)
        }

        if (userAuthentication()) {
            val notificationKey =
                db?.collection("notification_collections")?.document()?.id.toString()
            val data = mapOf(
                "order_title_message" to orderTitleMessage,
                "order_body_message" to orderBodyMessage,
                "order_number" to orderNumber,
                "store_uid" to storeUID,
                "read_notification" to false,
                "uid" to notificationKey,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db?.collection("notification_collections")
                ?.document(notificationKey)
                ?.set(data)
                ?.addOnSuccessListener { Log.d(Constants.DEBUG_TAG, "Success add data") }
                ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
        }
    }

    fun updateDeliveryOption(orderNumber: String, deliveryOption: Int) {
        val updateData = mapOf("delivery_option" to deliveryOption)

        if (userAuthentication()) {
            db?.collection("temporary_order_item_product")
                ?.whereEqualTo("order_number", orderNumber)
                ?.get()
                ?.addOnSuccessListener { snapshot ->
                    val temporaryOrderList =
                        snapshot?.toObjects(TemporaryOrderItemProductResponse::class.java)

                    temporaryOrderList?.forEach { response ->
                        db?.collection("temporary_order_item_product")
                            ?.document(response.uid.toString())
                            ?.update(updateData)
                            ?.addOnSuccessListener {
                                Log.d(Constants.DEBUG_TAG, "Success update data")
                                mView?.returnToMainActivity()
                            }
                            ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
                    }
                }
                ?.addOnFailureListener { Log.e(Constants.ERROR_TAG, "$it") }
        }
    }

    fun deleteOrder(orderNumber: String) {
        if (userAuthentication()) {
            db?.collection("temporary_order_item_product")
                ?.whereEqualTo("order_number", orderNumber)
                ?.get()
                ?.addOnSuccessListener { snapshot ->
                    val temporaryOrderList =
                        snapshot?.toObjects(TemporaryOrderItemProductResponse::class.java)

                    temporaryOrderList?.forEach { response ->
                        db?.collection("temporary_order_item_product")
                            ?.document(response.uid.toString())
                            ?.delete()
                            ?.addOnSuccessListener {
                                Log.d(Constants.DEBUG_TAG, "Success delete data")
                                db?.collection("order_by_order_number")
                                    ?.document(orderNumber)
                                    ?.delete()
                                    ?.addOnSuccessListener {
                                        Log.d(Constants.DEBUG_TAG, "Success delete data")
                                        mView?.returnToMainActivity()
                                    }
                                    ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
                            }
                            ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
                    }
                }
                ?.addOnFailureListener { Log.e(Constants.ERROR_TAG, "$it") }

            db?.collection("notification_collections")
                ?.whereEqualTo("order_number", orderNumber)
                ?.get()
                ?.addOnSuccessListener { snapshot ->
                    val notificationCollectionList =
                        snapshot?.toObjects(NotificationCollectionResponse::class.java)

                    notificationCollectionList?.forEach { response ->
                        db?.collection("notification_collections")
                            ?.document(response.uid.toString())
                            ?.delete()
                            ?.addOnSuccessListener {
                                Log.d(Constants.DEBUG_TAG, "Success delete data")
                            }
                            ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
                    }
                }
                ?.addOnFailureListener { Log.e(Constants.ERROR_TAG, "$it") }
        }
    }
}