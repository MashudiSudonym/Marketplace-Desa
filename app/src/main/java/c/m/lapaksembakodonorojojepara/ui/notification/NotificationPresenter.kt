package c.m.lapaksembakodonorojojepara.ui.notification

import android.util.Log
import c.m.lapaksembakodonorojojepara.model.NotificationCollectionResponse
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationPresenter : Presenter<NotificationView> {
    private var mView: NotificationView? = null
    private var db: FirebaseFirestore? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: NotificationView) {
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

    fun getNotification() {
        val userUID = authentication?.currentUser?.uid.toString()

        if (userAuthentication()) {
            mView?.showLoading()

            db?.collection("notification_collections")
                ?.whereEqualTo("user_order_uid", userUID)
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { snapshot, firestoreException ->
                    if (firestoreException != null) mView?.showNoNotificationResult()

                    val notificationData =
                        snapshot?.toObjects(NotificationCollectionResponse::class.java)

                    if (!notificationData.isNullOrEmpty()) {
                        mView?.hideLoading()
                        mView?.getNotification(notificationData)
                    } else {
                        mView?.showNoNotificationResult()
                    }
                }
        }
    }

    fun setNotificationAsRead(orderNumber: String) {
        val data = mapOf("read_notification" to true)

        if (userAuthentication()) {
            db?.collection("notification_collections")
                ?.whereEqualTo("order_number", orderNumber)
                ?.get()
                ?.addOnSuccessListener { snapshot ->
                    val notificationData =
                        snapshot?.toObjects(NotificationCollectionResponse::class.java)

                    notificationData?.forEach { response ->
                        db?.collection("notification_collections")
                            ?.document(response.uid.toString())
                            ?.update(data)
                            ?.addOnSuccessListener {
                                Log.d(Constants.DEBUG_TAG, "Success update data")
                            }
                            ?.addOnFailureListener { e -> Log.e("ERROR!!", "$e") }
                    }
                }
                ?.addOnFailureListener {
                    Log.e(
                        Constants.ERROR_TAG,
                        "$it"
                    )
                }
        }
    }
}