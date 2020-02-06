package c.m.lapaksembakodonorojo.ui.seller.sellerstoreinformation

import c.m.lapaksembakodonorojo.model.ProductsResponse
import c.m.lapaksembakodonorojo.model.StoreResponse
import c.m.lapaksembakodonorojo.util.base.View

interface SellerStoreInformationView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getStore(storeData: List<StoreResponse>)
    fun getProduct(productData: List<ProductsResponse>)
}