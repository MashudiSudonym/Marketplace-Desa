package c.m.lapaksembakodonorojojepara.ui.seller.sellerstoreorderdetails

import android.util.Log
import c.m.lapaksembakodonorojojepara.model.MessageNotification
import c.m.lapaksembakodonorojojepara.model.Notification
import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.model.UsersResponse
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.base.Presenter
import c.m.lapaksembakodonorojojepara.util.webservice.ApiInterface
import c.m.lapaksembakodonorojojepara.util.webservice.RetrofitService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SellerStoreOrderDetailsPresenter : Presenter<SellerStoreOrderDetailsView> {
    private var mView: SellerStoreOrderDetailsView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null
    private val apiService =
        RetrofitService.getInstance("https://fcm.googleapis.com/").create(ApiInterface::class.java)

    override fun onAttach(view: SellerStoreOrderDetailsView) {
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

    fun sendOrderStatusNotification(
        storeUID: String,
        orderNumber: String,
        orderBy: String,
        userOrderUID: String,
        orderBodyMessage: String,
        orderTitleMessage: String
    ) {
        if (userAuthentication()) {
            val notificationKey =
                db?.collection("notification_collections")?.document()?.id.toString()
            val data = mapOf(
                "order_title_message" to orderTitleMessage,
                "order_body_message" to orderBodyMessage,
                "order_number" to orderNumber,
                "user_order_uid" to userOrderUID,
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

            // get user order uid
            db?.collection("users")
                ?.whereEqualTo("name", orderBy)
                ?.get()
                ?.addOnSuccessListener { snapshot ->
                    val usersData = snapshot?.toObjects(UsersResponse::class.java)

                    usersData?.forEach { usersResponse ->
                        val messageNotification = MessageNotification(
                            "/topics/${usersResponse.uid}",
                            Notification(orderBodyMessage, orderTitleMessage)
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            apiService.postMessage(messageNotification)
                        }
                    }
                }
                ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
        }
    }

    fun updateCancelOrderStatus(orderNumber: String) {
        val updateData = mapOf(
            "is_canceled" to true,
            "order_status" to 3
        )

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
                                mView?.alertUpdateSuccess()
                            }
                            ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
                    }
                }
                ?.addOnFailureListener { Log.e(Constants.ERROR_TAG, "$it") }
        }
    }

    fun updateProgressOrderStatus(orderNumber: String, orderStatus: Int) {
        val updateData = mapOf(
            "order_status" to orderStatus
        )

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
                                mView?.alertUpdateSuccess()
                            }
                            ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
                    }
                }
                ?.addOnFailureListener { Log.e(Constants.ERROR_TAG, "$it") }
        }
    }

    fun updatePaymentOrderStatus(orderNumber: String) {
        val updateData = mapOf(
            "payment_status" to true,
            "order_status" to 3
        )

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
                                mView?.alertUpdateSuccess()
                            }
                            ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
                    }
                }
                ?.addOnFailureListener {
                    Log.e(Constants.ERROR_TAG, "$it")
                }
        }
    }
}