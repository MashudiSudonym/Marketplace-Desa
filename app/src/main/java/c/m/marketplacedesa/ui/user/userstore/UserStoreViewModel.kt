package c.m.marketplacedesa.ui.user.userstore

import androidx.lifecycle.*
import c.m.marketplacedesa.data.remote.RemoteRepository
import c.m.marketplacedesa.data.remote.response.ProductsResponse
import kotlinx.coroutines.cancel

class UserStoreViewModel(private val remoteRepository: RemoteRepository) : ViewModel() {

    private val _storeUID: MutableLiveData<String> = MutableLiveData()
    val getProducts: LiveData<List<ProductsResponse>> = Transformations.switchMap(_storeUID) {
        remoteRepository.getProducts(it).map { snapshot ->
            snapshot.toObjects(ProductsResponse::class.java)
        }
    }

    fun getStoreUID(storeUID: String) {
        _storeUID.value = storeUID
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}