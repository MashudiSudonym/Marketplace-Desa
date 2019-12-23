package c.m.marketplacedesa.ui.user.userordercart

import android.util.Log
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserOrderCartPresenter : Presenter<UserOrderCartView> {
    private var mView: UserOrderCartView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

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
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e(Constants.ERROR_TAG, "$exception")

                    val temporaryOrderList =
                        snapshot?.toObjects(TemporaryOrderItemProductResponse::class.java)

                    mView?.getTemporaryOrder(temporaryOrderList as List<TemporaryOrderItemProductResponse>)
                }
        }
    }

    fun updateDeliveryOption(orderNumber: String, deliveryOption: Int) {
        val updateData = mapOf("delivery_option" to deliveryOption)

        if (userAuthentication()) {
            db?.collection("temporary_order_item_product")
                ?.whereEqualTo("order_number", orderNumber)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e(Constants.ERROR_TAG, "$exception")

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
        }
    }

    fun deleteOrder(orderNumber: String) {
        if (userAuthentication()) {
            db?.collection("temporary_order_item_product")
                ?.whereEqualTo("order_number", orderNumber)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e(Constants.ERROR_TAG, "$exception")

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
        }
    }
}