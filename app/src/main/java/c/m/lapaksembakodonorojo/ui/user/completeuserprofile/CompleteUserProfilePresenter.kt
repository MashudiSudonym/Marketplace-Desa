package c.m.lapaksembakodonorojo.ui.user.completeuserprofile

import android.net.Uri
import android.util.Log
import c.m.lapaksembakodonorojo.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CompleteUserProfilePresenter : Presenter<CompleteUserProfileView> {
    private var mView: CompleteUserProfileView? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: CompleteUserProfileView) {
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

    fun getUserPhoneFromAuthentication() {
        if (userAuthentication()) {
            val phone = authentication?.currentUser?.phoneNumber.toString()

            mView?.userPhoneDataFromAuthentication(phone)
        }
    }

    fun uploadUserPhoto(filePath: Uri?) {
        val userUID = authentication?.currentUser?.uid.toString()
        val imageReference =
            storageReference?.child("users/$userUID/$userUID")

        if (userAuthentication()) {
            // Photo Profile Upload
            if (filePath != null) {
                mView?.showProgressDialog()

                imageReference?.putFile(filePath)
                    ?.addOnSuccessListener {
                        mView?.closeProgressDialog()
                    }
                    ?.addOnFailureListener {
                        mView?.closeProgressDialog()
                    }
                    ?.addOnProgressListener {
                        val progress = 100.0 * it.bytesTransferred / it.totalByteCount

                        mView?.progressDialogMessage("Uploaded $progress %...")
                    }
            }
        }
    }

    fun sendUserData(name: String, address: String, userSellerStatus: Boolean, phone: String) {
        val userUID = authentication?.currentUser?.uid.toString()
        val imageThumbnailsReference = storageReference?.child("users/$userUID/$userUID")

        if (userAuthentication()) {
            mView?.showProgressDialog()

            imageThumbnailsReference?.downloadUrl?.addOnSuccessListener {
                val docData = hashMapOf(
                    "address" to address,
                    "image_profile" to (it?.toString() ?: "-"),
                    "name" to name,
                    "phone" to phone,
                    "seller" to userSellerStatus,
                    "uid" to userUID
                )

                db?.collection("users")?.document(userUID)
                    ?.set(docData)
                    ?.addOnSuccessListener {
                        mView?.progressDialogMessage("Update...")
                        mView?.returnMainActivity()
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