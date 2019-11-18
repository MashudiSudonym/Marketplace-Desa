package c.m.marketplacedesa.ui.user.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import c.m.marketplacedesa.data.remote.RemoteRepository
import c.m.marketplacedesa.data.remote.response.StoreResponse
import kotlinx.coroutines.cancel

class MainViewModel(private val remoteRepository: RemoteRepository) : ViewModel() {

    fun getStoreContent(): LiveData<List<StoreResponse>> =
        remoteRepository.getStore().map { snapshot ->
            snapshot.toObjects(StoreResponse::class.java)
        }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

}