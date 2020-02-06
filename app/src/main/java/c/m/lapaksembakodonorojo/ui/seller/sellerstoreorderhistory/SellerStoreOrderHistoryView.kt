package c.m.lapaksembakodonorojo.ui.seller.sellerstoreorderhistory

import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.base.View

interface SellerStoreOrderHistoryView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}