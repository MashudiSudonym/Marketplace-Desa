package c.m.marketplacedesa.ui.seller.sellerstoreorderhistory

import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.util.base.View

interface SellerStoreOrderHistoryView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}