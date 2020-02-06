package c.m.lapaksembakodonorojo.ui.user.userstore

import c.m.lapaksembakodonorojo.model.ProductsResponse
import c.m.lapaksembakodonorojo.model.UsersResponse
import c.m.lapaksembakodonorojo.util.base.View

interface UserStoreView : View {
    fun showLoading()
    fun hideLoading()
    fun hideSearchLoading()
    fun showNoDataResult()
    fun getProduct(productData: List<ProductsResponse>)
    fun getUser(userData: List<UsersResponse>)
    fun returnToSignInActivity()
}