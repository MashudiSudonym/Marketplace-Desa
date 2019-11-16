package c.m.marketplacedesa.data.remote

import com.google.firebase.firestore.FirebaseFirestore

class RemoteRepository() {
    private lateinit var firebaseFirestore: FirebaseFirestore

    fun getStore() = firebaseFirestore.collection("store")
}