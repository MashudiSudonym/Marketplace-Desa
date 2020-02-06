package c.m.lapaksembakodonorojo.ui.user.userprofile

import android.annotation.SuppressLint
import android.util.Log
import c.m.lapaksembakodonorojo.model.UsersResponse
import c.m.lapaksembakodonorojo.util.Constants
import c.m.lapaksembakodonorojo.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging

class UserProfilePresenter : Presenter<UserProfileView> {
    private var mView: UserProfileView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null
    private var firebaseInstanceId: FirebaseInstanceId? = null

    override fun onAttach(view: UserProfileView) {
        mView = view
    }

    override fun onDetach() {
        mView = null
    }

    fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        authentication = FirebaseAuth.getInstance()
        firebaseInstanceId = FirebaseInstanceId.getInstance()
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


    @SuppressLint("DefaultLocale")
    fun removeFCMToken() {
        if (userAuthentication()) {
            val userUID = authentication?.currentUser?.uid
            val dataUser = mapOf(
                "fcm_token" to FieldValue.delete()
            )

            // subscribe this application for firebase cloud messaging topic
            FirebaseMessaging.getInstance().unsubscribeFromTopic("$userUID")

            db?.collection("users")
                ?.document(userUID.toString())
                ?.update(dataUser)
                ?.addOnSuccessListener { Log.d(Constants.DEBUG_TAG, "remove success") }
                ?.addOnFailureListener { Log.e(Constants.ERROR_TAG, "$it") }
        }
    }
}