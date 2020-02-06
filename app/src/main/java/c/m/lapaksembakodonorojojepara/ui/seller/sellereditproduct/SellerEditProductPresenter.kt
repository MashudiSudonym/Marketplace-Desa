package c.m.lapaksembakodonorojojepara.ui.seller.sellereditproduct

import android.net.Uri
import android.util.Log
import c.m.lapaksembakodonorojojepara.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SellerEditProductPresenter : Presenter<SellerEditProductView> {
    private var mView: SellerEditProductView? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: SellerEditProductView) {
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

    fun updateProductInformationData(
        productUID: String,
        productName: String,
        productPrice: Int,
        productStock: Boolean,
        productStore: String,
        filePath: Uri?,
        randomNumber: String
    ) {
        val userUID = authentication?.currentUser?.uid.toString()
        val imageThumbnailsReference =
            storageReference?.child("users/$userUID/products/product-$randomNumber-$userUID")

        if (userAuthentication()) {
            // Photo Profile Upload
            if (filePath != null) {
                mView?.showProgressDialog()

                imageThumbnailsReference?.putFile(filePath)
                    ?.addOnSuccessListener {
                        imageThumbnailsReference.downloadUrl.addOnSuccessListener {
                            val docData = hashMapOf(
                                "image_product" to (it?.toString() ?: "-"),
                                "name" to productName,
                                "uid" to productUID,
                                "price" to productPrice,
                                "stock" to productStock,
                                "store" to productStore
                            )

                            db?.collection("products")?.document(productUID)
                                ?.update(docData)
                                ?.addOnSuccessListener {
                                    mView?.finishThisActivityToNextActivity(
                                        productStore
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

                val docData = hashMapOf(
                    "name" to productName,
                    "uid" to productUID,
                    "price" to productPrice,
                    "stock" to productStock,
                    "store" to productStore
                )

                db?.collection("products")?.document(productUID)
                    ?.update(docData)
                    ?.addOnSuccessListener { mView?.finishThisActivityToNextActivity(productStore) }
                    ?.addOnFailureListener { e ->
                        mView?.closeProgressDialog()
                        Log.e("ERROR!!", "$e")
                    }
            }
        }
    }

    fun deleteProduct(
        productUID: String,
        productStore: String
    ) {
        if (userAuthentication()) {
            db?.collection("products")
                ?.document(productUID)
                ?.delete()
                ?.addOnSuccessListener { mView?.finishThisActivityToNextActivity(productStore) }
                ?.addOnFailureListener { e ->
                    Log.e("ERROR!!", "$e")
                }
        }
    }
}