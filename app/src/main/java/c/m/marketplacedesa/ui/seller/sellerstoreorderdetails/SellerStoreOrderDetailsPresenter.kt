package c.m.marketplacedesa.ui.seller.sellerstoreorderdetails

import android.util.Log
import c.m.marketplacedesa.model.MessageNotification
import c.m.marketplacedesa.model.Notification
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.base.Presenter
import c.m.marketplacedesa.util.webservice.ApiInterface
import c.m.marketplacedesa.util.webservice.RetrofitService
import com.google.firebase.auth.FirebaseAuth
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
                "user_uid" to storeOwnerUID,
                "store_uid" to storeUID,
                "read_notification" to false,
                "uid" to notificationKey
            )

            db?.collection("notification_collections")
                ?.document(notificationKey)
                ?.set(data)
                ?.addOnSuccessListener { Log.d(Constants.DEBUG_TAG, "Success add data") }
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