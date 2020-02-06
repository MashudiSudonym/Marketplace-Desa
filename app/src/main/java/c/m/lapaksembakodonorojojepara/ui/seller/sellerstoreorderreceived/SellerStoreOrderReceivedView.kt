package c.m.lapaksembakodonorojojepara.ui.seller.sellerstoreorderreceived

import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface SellerStoreOrderReceivedView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}