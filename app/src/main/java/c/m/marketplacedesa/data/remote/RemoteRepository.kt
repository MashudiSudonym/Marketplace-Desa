package c.m.marketplacedesa.data.remote

import c.m.marketplacedesa.util.FirebaseQueryLiveData
import com.google.firebase.firestore.FirebaseFirestore

class RemoteRepository() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getStore() = FirebaseQueryLiveData(db.collection("store"))

    fun getProducts(storeUID: String) = FirebaseQueryLiveData(
        db.collection("products")
            .whereEqualTo("store", storeUID)
    )
}