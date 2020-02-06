package c.m.lapaksembakodonorojojepara.ui.user.userorderdetails

import android.util.Log
import c.m.lapaksembakodonorojojepara.model.StoreResponse
import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserOrderDetailsPresenter : Presenter<UserOrderDetailsView> {
    private var mView: UserOrderDetailsView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: UserOrderDetailsView) {
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

                    // get store name
                    temporaryOrderList?.forEach { response ->
                        db?.collection("store")
                            ?.whereEqualTo("uid", response.store_uid)
                            ?.addSnapshotListener { snapshot, firestoreException ->
                                if (firestoreException != null) Log.e(
                                    Constants.ERROR_TAG,
                                    "$firestoreException"
                                )

                                val storeList = snapshot?.toObjects(StoreResponse::class.java)

                                storeList?.forEach { storeResponse ->
                                    mView?.getStoreName(storeResponse.name.toString())
                                }
                            }
                    }
                }
        }
    }
}