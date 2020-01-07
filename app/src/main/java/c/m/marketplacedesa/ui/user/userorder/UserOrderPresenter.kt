package c.m.marketplacedesa.ui.user.userorder

import android.util.Log
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.model.UsersResponse
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserOrderPresenter : Presenter<UserOrderView> {
    private var mView: UserOrderView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: UserOrderView) {
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

    fun getUserOrder() {
        val userUID = authentication?.currentUser?.uid.toString()

        if (userAuthentication()) {
            mView?.showLoading()

            // get user name
            db?.collection("users")
                ?.whereEqualTo("uid", userUID)
                ?.addSnapshotListener { snapshot, firestoreException ->
                    if (firestoreException != null) Log.e(
                        Constants.ERROR_TAG,
                        "$firestoreException"
                    )

                    val userData = snapshot?.toObjects(UsersResponse::class.java)

                    userData?.forEach { userResponse ->
                        // get order number
                        val queryOne = db?.collection("temporary_order_item_product")
                            ?.whereEqualTo("order_by", userResponse.name)
                            ?.whereEqualTo("order_status", false)
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
    }
}