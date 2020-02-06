package c.m.lapaksembakodonorojo.ui.seller.selleraddproduct

import android.net.Uri
import android.util.Log
import c.m.lapaksembakodonorojo.util.base.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SellerAddProductPresenter : Presenter<SellerAddProductView> {
    private var mView: SellerAddProductView? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var authentication: FirebaseAuth? = null

    override fun onAttach(view: SellerAddProductView) {
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

    fun uploadProductPhoto(filePath: Uri?, randomNumber: String) {
        val userUID = authentication?.currentUser?.uid.toString()
        val imageReference =
            storageReference?.child("users/$userUID/products/product-$randomNumber-$userUID")

        if (userAuthentication()) {
            // Photo Product Upload
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

    fun saveProductInformationData(
        productName: String,
        productPrice: Int,
        productStock: Boolean,
        productStore: String,
        randomNumber: String
    ) {
        val userUID = authentication?.currentUser?.uid.toString()
        val productUID = db?.collection("products")?.document()?.id.toString()
        val imageThumbnailsReference =
            storageReference?.child("users/$userUID/products/product-$randomNumber-$userUID")

        if (userAuthentication()) {
            mView?.showProgressDialog()

            imageThumbnailsReference?.downloadUrl?.addOnSuccessListener {
                val docData = hashMapOf(
                    "image_product" to (it?.toString() ?: "-"),
                    "name" to productName,
                    "uid" to productUID,
                    "price" to productPrice,
                    "stock" to productStock,
                    "store" to productStore
                )

                db?.collection("products")?.document(productUID)
                    ?.set(docData)
                    ?.addOnSuccessListener {
                        mView?.progressDialogMessage("Update...")

                        mView?.finishThisActivityToNextActivity(productStore)
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