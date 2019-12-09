package c.m.marketplacedesa.ui.seller.sellerstoreinformation

import c.m.marketplacedesa.model.ProductsResponse
import c.m.marketplacedesa.model.StoreResponse
import c.m.marketplacedesa.util.base.View

interface SellerStoreInformationView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getStore(storeData: List<StoreResponse>)
    fun getProduct(productData: List<ProductsResponse>)
}