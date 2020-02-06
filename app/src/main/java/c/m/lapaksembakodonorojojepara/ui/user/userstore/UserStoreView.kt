package c.m.lapaksembakodonorojojepara.ui.user.userstore

import c.m.lapaksembakodonorojojepara.model.ProductsResponse
import c.m.lapaksembakodonorojojepara.model.UsersResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface UserStoreView : View {
    fun showLoading()
    fun hideLoading()
    fun hideSearchLoading()
    fun showNoDataResult()
    fun getProduct(productData: List<ProductsResponse>)
    fun getUser(userData: List<UsersResponse>)
    fun returnToSignInActivity()
}