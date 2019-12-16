package c.m.marketplacedesa.ui.user.userstore

import c.m.marketplacedesa.model.ProductsResponse
import c.m.marketplacedesa.model.UsersResponse
import c.m.marketplacedesa.util.base.View

interface UserStoreView : View {
    fun showLoading()
    fun hideLoading()
    fun hideSearchLoading()
    fun showNoDataResult()
    fun getProduct(productData: List<ProductsResponse>)
    fun getUser(userData: List<UsersResponse>)
    fun returnToSignInActivity()
}