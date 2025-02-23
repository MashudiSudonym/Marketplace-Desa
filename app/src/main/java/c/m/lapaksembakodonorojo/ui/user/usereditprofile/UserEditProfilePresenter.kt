package c.m.lapaksembakodonorojo.ui.user.usereditprofile

import android.net.Uri
import android.util.Log
import c.m.lapaksembakodonorojo.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserEditProfilePresenter : Presenter<UserEditProfileView> {
    private var mView: UserEditProfileView? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: UserEditProfileView) {
        mView = view
    }

    override fun onDetach() {
        mView = null
    }

    fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
        authentication = FirebaseAuth.getInstance()
    }

    private fun userAuthentication() = authentication?.currentUser != null

    fun sendUserUpdateData(
        name: String,
        address: String,
        userSellerStatus: Boolean,
        phone: String,
        filePath: Uri?
    ) {
        val userUID = authentication?.currentUser?.uid.toString()
        val imageThumbnailsReference = storageReference?.child("users/$userUID/$userUID")

        if (userAuthentication()) {
            // Photo Profile Upload
            if (filePath != null) {
                mView?.showProgressDialog()

                imageThumbnailsReference?.putFile(filePath)
                    ?.addOnSuccessListener {
                        imageThumbnailsReference.downloadUrl.addOnSuccessListener {
                            val docData = hashMapOf(
                                "address" to address,
                                "image_profile" to (it?.toString() ?: "-"),
                                "name" to name,
                                "phone" to phone,
                                "seller" to userSellerStatus,
                                "uid" to userUID
                            )

                            db?.collection("users")?.document(userUID)
                                ?.update(docData)
                                ?.addOnSuccessListener { mView?.returnUserProfileActivity() }
                                ?.addOnFailureListener { e ->
                                    Log.e("ERROR!!", "$e")
                                }
                        }

                        mView?.closeProgressDialog()
                    }
                    ?.addOnFailureListener {
                        mView?.closeProgressDialog()
                    }
                    ?.addOnProgressListener {
                        val progress = 100.0 * it.bytesTransferred / it.totalByteCount

                        mView?.progressDialogMessage("Uploaded $progress %...")
                    }
            } else {
                mView?.showProgressDialog()

                imageThumbnailsReference?.downloadUrl?.addOnSuccessListener {
                    val docData = mapOf(
                        "address" to address,
                        "image_profile" to (it?.toString() ?: "-"),
                        "name" to name,
                        "phone" to phone,
                        "seller" to userSellerStatus,
                        "uid" to userUID
                    )

                    db?.collection("users")?.document(userUID)
                        ?.update(docData)
                        ?.addOnSuccessListener {
                            mView?.progressDialogMessage("Update...")
                            mView?.returnUserProfileActivity()
                            mView?.closeProgressDialog()
                        }
                        ?.addOnFailureListener { e ->
                            mView?.closeProgressDialog()
                            Log.e("ERROR!!", "$e")
                        }
                }
            }
        }
    }
}