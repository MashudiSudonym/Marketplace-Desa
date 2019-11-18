package c.m.marketplacedesa.ui.user.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import c.m.marketplacedesa.data.remote.RemoteRepository
import c.m.marketplacedesa.data.remote.response.StoreResponse
import c.m.marketplacedesa.util.FirebaseQueryLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel

class MainViewModel(private val remoteRepository: RemoteRepository) : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storeFirestoreLiveData = FirebaseQueryLiveData(db.collection("store"))

    fun getStoreContent(): LiveData<List<StoreResponse>> =
        remoteRepository.getStore().map { snapshot ->
            snapshot.toObjects(StoreResponse::class.java)
        }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

}