package c.m.lapaksembakodonorojojepara.ui.seller.sellerstoreinformation

import android.util.Log
import c.m.lapaksembakodonorojojepara.model.ProductsResponse
import c.m.lapaksembakodonorojojepara.model.StoreResponse
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SellerStoreInformationPresenter : Presenter<SellerStoreInformationView> {
    private var mView: SellerStoreInformationView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: SellerStoreInformationView) {
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

    fun getStoreByStoreUID(storeUID: String) {
        if (userAuthentication()) {
            db?.collection("store")?.whereEqualTo("uid", storeUID)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e(Constants.ERROR_TAG, "$exception")

                    val storeData = snapshot?.toObjects(StoreResponse::class.java)

                    mView?.getStore(storeData as List<StoreResponse>)
                }
        }
    }

    fun getStoreByOwnerUID(ownerUID: String) {
        if (userAuthentication()) {
            db?.collection("store")?.whereEqualTo("owner", ownerUID)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e(Constants.ERROR_TAG, "$exception")

                    val storeData = snapshot?.toObjects(StoreResponse::class.java)

                    mView?.getStore(storeData as List<StoreResponse>)
                }
        }
    }

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
        }
    }
}