package c.m.marketplacedesa.ui.user.main

import android.annotation.SuppressLint
import android.util.Log
import c.m.marketplacedesa.model.NotificationCollectionResponse
import c.m.marketplacedesa.model.StoreResponse
import c.m.marketplacedesa.model.UsersResponse
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.base.Presenter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging

class MainPresenter : Presenter<MainView> {
    private var mView: MainView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null
    private var firebaseInstanceId: FirebaseInstanceId? = null
    private var fcmToken: String? = ""
    private var userUID: String? = ""

    override fun onAttach(view: MainView) {
        mView = view
    }

    override fun onDetach() {
        mView = null
    }

    @SuppressLint("DefaultLocale")
    fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        authentication = FirebaseAuth.getInstance()
        firebaseInstanceId = FirebaseInstanceId.getInstance()

        // get user UID
        userUID = authentication?.currentUser?.uid

        // subscribe this application for firebase cloud messaging topic
        FirebaseMessaging.getInstance().subscribeToTopic("$userUID")
            .addOnCompleteListener { task ->
                var msg = "subscribe"
                if (!task.isSuccessful) {
                    msg = "failed"
                }
                Log.d(Constants.DEBUG_TAG, "$msg $userUID")
            }

        // get fcm token
        firebaseInstanceId?.instanceId?.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) return@OnCompleteListener

            fcmToken = task.result?.token

            val userFcmData = mapOf(
                "fcm_token" to fcmToken
            )

            if (userAuthentication()) {
                db?.collection("users")
                    ?.document(userUID.toString())
                    ?.update(userFcmData)
                    ?.addOnSuccessListener { Log.d(Constants.DEBUG_TAG, "add success") }
                    ?.addOnFailureListener { Log.e(Constants.ERROR_TAG, "$it") }
            }
        })
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
                    mView?.getStore(storeList)
                } else {
                    mView?.showNoDataResult()
                }
            }
        } else {
            mView?.returnToSignInActivity()
        }
    }

    fun checkUserData() {
        if (userAuthentication()) {
            val userUID = authentication?.currentUser?.uid.toString()

            db?.collection("users")
                ?.whereEqualTo("uid", userUID)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e("Error!!", "$exception")

                    val userList = snapshot?.toObjects(UsersResponse::class.java)

                    if (userList?.isNotEmpty() == false) mView?.returnToCompleteUserProfile()
                }
        } else {
            mView?.returnToSignInActivity()
        }
    }

    fun checkNewNotification() {
        val userUID = authentication?.currentUser?.uid.toString()

        if (userAuthentication()) {
            db?.collection("notification_collections")
                ?.whereEqualTo("user_uid", userUID)
                ?.whereEqualTo("read_notification", false)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e("Error!!", "$exception")

                    val notificationData =
                        snapshot?.toObjects(NotificationCollectionResponse::class.java)

                    if (!notificationData.isNullOrEmpty()) {
                        mView?.getNotificationCount(notificationData)
                    }
                }
        }
    }
}