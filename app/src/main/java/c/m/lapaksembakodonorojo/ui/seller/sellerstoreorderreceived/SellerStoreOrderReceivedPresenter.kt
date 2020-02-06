package c.m.lapaksembakodonorojo.ui.seller.sellerstoreorderreceived

import android.util.Log
import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.Constants
import c.m.lapaksembakodonorojo.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SellerStoreOrderReceivedPresenter : Presenter<SellerStoreOrderReceivedView> {
    private var mView: SellerStoreOrderReceivedView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: SellerStoreOrderReceivedView) {
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

    fun getUserOrder(storeUID: String) {
        if (userAuthentication()) {
            mView?.showLoading()

            // get order number
            val queryOne = db?.collection("temporary_order_item_product")
                ?.whereEqualTo("store_uid", storeUID)
                ?.whereLessThanOrEqualTo("order_status", 2)
            queryOne?.whereEqualTo("is_canceled", false)
                ?.whereEqualTo("payment_status", false)
                ?.addSnapshotListener { snapshot, firestoreException ->
                    if (firestoreException != null) Log.e(
                        Constants.ERROR_TAG,
                        "$firestoreException"
                    )

                    val temporaryOrderItemProductData =
                        snapshot?.toObjects(TemporaryOrderItemProductResponse::class.java)

                    if (!temporaryOrderItemProductData.isNullOrEmpty()) {
                        mView?.hideLoading()
                        mView?.getUserOrder(temporaryOrderItemProductData)
                    } else {
                        mView?.showNoDataResult()
                    }
                }
        }
    }
}