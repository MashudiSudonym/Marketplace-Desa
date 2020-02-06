package c.m.lapaksembakodonorojo.ui.seller.sellerstoreorderreceived

import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.base.View

interface SellerStoreOrderReceivedView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}