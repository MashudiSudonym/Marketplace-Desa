package c.m.lapaksembakodonorojojepara.ui.seller.completesellerstoreinformation

import android.net.Uri
import android.util.Log
import c.m.lapaksembakodonorojojepara.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CompleteSellerStoreInformationPresenter : Presenter<CompleteSellerStoreInformationView> {
    private var mView: CompleteSellerStoreInformationView? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: CompleteSellerStoreInformationView) {
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

    fun uploadStorePhoto(filePath: Uri?) {
        val userUID = authentication?.currentUser?.uid.toString()
        val imageReference =
            storageReference?.child("users/$userUID/store-$userUID")

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

    fun saveStoreInformationData(
        storeName: String,
        storeAddress: String,
        storePhone: String,
        storeGeoPoint: GeoPoint
    ) {
        val userUID = authentication?.currentUser?.uid.toString()
        val storeUID = db?.collection("store")?.document()?.id.toString()
        val imageThumbnailsReference = storageReference?.child("users/$userUID/store-$userUID")

        if (userAuthentication()) {
            mView?.showProgressDialog()

            imageThumbnailsReference?.downloadUrl?.addOnSuccessListener {
                val docData = mapOf(
                    "address" to storeAddress,
                    "image_profile_store" to (it?.toString() ?: "-"),
                    "name" to storeName,
                    "phone" to storePhone,
                    "owner" to userUID,
                    "store_geopoint" to storeGeoPoint,
                    "uid" to storeUID
                )

                db?.collection("store")?.document(storeUID)
                    ?.set(docData)
                    ?.addOnSuccessListener {
                        mView?.progressDialogMessage("Update...")

                        db?.collection("users")?.document(userUID)
                            ?.update("seller", true)

                        mView?.finishThisActivityToNextActivity(storeUID)
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