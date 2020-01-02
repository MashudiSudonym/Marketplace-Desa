package c.m.marketplacedesa.ui.seller.sellereditstoreinformation

import android.net.Uri
import android.util.Log
import c.m.marketplacedesa.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SellerEditStoreInformationPresenter : Presenter<SellerEditStoreInformationView> {
    private var mView: SellerEditStoreInformationView? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: SellerEditStoreInformationView) {
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

    fun storeUpdateInformationData(
        storeUID: String,
        storeName: String,
        storeAddress: String,
        storePhone: String,
        storeGeoPoint: GeoPoint,
        filePath: Uri?
    ) {
        val userUID = authentication?.currentUser?.uid.toString()
        val imageThumbnailsReference = storageReference?.child("users/$userUID/store-$userUID")

        if (userAuthentication()) {
            // Photo Profile Upload
            if (filePath != null) {
                mView?.showProgressDialog()

                imageThumbnailsReference?.putFile(filePath)
                    ?.addOnSuccessListener {
                        imageThumbnailsReference.downloadUrl.addOnSuccessListener {
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
                                ?.update(docData)
                                ?.addOnSuccessListener {
                                    mView?.finishThisActivityToNextActivity(
                                        storeUID
                                    )
                                }
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
                        "address" to storeAddress,
                        "image_profile_store" to (it?.toString() ?: "-"),
                        "name" to storeName,
                        "phone" to storePhone,
                        "owner" to userUID,
                        "store_geopoint" to storeGeoPoint,
                        "uid" to storeUID
                    )

                    db?.collection("store")?.document(storeUID)
                        ?.update(docData)
                        ?.addOnSuccessListener {
                            mView?.progressDialogMessage("Update...")
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
}