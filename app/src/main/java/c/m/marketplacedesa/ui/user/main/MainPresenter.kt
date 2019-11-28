package c.m.marketplacedesa.ui.user.main

import c.m.marketplacedesa.model.StoreResponse
import c.m.marketplacedesa.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainPresenter : Presenter<MainView> {
    private var mView: MainView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: MainView) {
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

    fun getStore() {
        if (userAuthentication()) {
            mView?.showLoading()
            db?.collection("store")?.addSnapshotListener { snapshot, exception ->
                if (exception != null) mView?.showNoDataResult()

                val storeList = snapshot?.toObjects(StoreResponse::class.java)

                if (!storeList.isNullOrEmpty()) {
                    mView?.hideLoading()
                    mView?.getStore(storeList as List<StoreResponse>)
                } else {
                    mView?.showNoDataResult()
                }
            }
        } else {
            mView?.returnToSignInActivity()
        }
    }
}