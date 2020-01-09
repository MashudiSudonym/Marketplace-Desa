package c.m.marketplacedesa.ui.seller.sellerstoreorderhistory

import android.util.Log
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SellerStoreOrderHistoryPresenter : Presenter<SellerStoreOrderHistoryView> {
    private var mView: SellerStoreOrderHistoryView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: SellerStoreOrderHistoryView) {
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
            db?.collection("temporary_order_item_product")
                ?.whereEqualTo("store_uid", storeUID)
                ?.whereEqualTo("order_status", 3)
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