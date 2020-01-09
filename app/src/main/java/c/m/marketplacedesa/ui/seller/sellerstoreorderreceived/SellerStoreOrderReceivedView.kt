package c.m.marketplacedesa.ui.seller.sellerstoreorderreceived

import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.util.base.View

interface SellerStoreOrderReceivedView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}