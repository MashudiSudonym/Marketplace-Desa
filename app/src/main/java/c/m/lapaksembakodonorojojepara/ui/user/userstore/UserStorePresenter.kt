package c.m.lapaksembakodonorojojepara.ui.user.userstore

import android.util.Log
import c.m.lapaksembakodonorojojepara.model.ProductsResponse
import c.m.lapaksembakodonorojojepara.model.UsersResponse
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserStorePresenter : Presenter<UserStoreView> {
    private var mView: UserStoreView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: UserStoreView) {
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

    fun getProduct(storeUID: String) {
        if (userAuthentication()) {
            mView?.showLoading()
            db?.collection("products")?.whereEqualTo("store", storeUID)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) mView?.showNoDataResult()

                    val productList = snapshot?.toObjects(ProductsResponse::class.java)

                    if (!productList.isNullOrEmpty()) {
                        mView?.hideLoading()
                        mView?.getProduct(productList as List<ProductsResponse>)
                    } else {
                        mView?.showNoDataResult()
                    }
                }
        } else {
            mView?.returnToSignInActivity()
        }
    }

    fun getUser() {
        val userUID = authentication?.currentUser?.uid.toString()
        if (userAuthentication()) {
            db?.collection("users")?.whereEqualTo("uid", userUID)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e(Constants.ERROR_TAG, "$exception")

                    val userData = snapshot?.toObjects(UsersResponse::class.java)

                    mView?.getUser(userData as List<UsersResponse>)
                }
        }
    }
}