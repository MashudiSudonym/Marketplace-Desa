package c.m.lapaksembakodonorojojepara.ui.seller.sellerstoreinformation

import c.m.lapaksembakodonorojojepara.model.ProductsResponse
import c.m.lapaksembakodonorojojepara.model.StoreResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface SellerStoreInformationView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getStore(storeData: List<StoreResponse>)
    fun getProduct(productData: List<ProductsResponse>)
}