package c.m.marketplacedesa.ui.user.userprofile

import android.util.Log
import c.m.marketplacedesa.model.UsersResponse
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfilePresenter : Presenter<UserProfileView> {
    private var mView: UserProfileView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: UserProfileView) {
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

    fun getProfile() {
        if (userAuthentication()) {
            val userUID = authentication?.currentUser?.uid.toString()

            db?.collection("users")?.whereEqualTo("uid", userUID)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e(Constants.ERROR_TAG, "$exception")

                    val profileList = snapshot?.toObjects(UsersResponse::class.java)

                    mView?.getProfile(profileList as List<UsersResponse>)
                }
        }
    }
}