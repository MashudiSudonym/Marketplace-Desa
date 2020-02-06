package c.m.lapaksembakodonorojo.ui.seller.sellerstoreorderdetails

import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.base.View

interface SellerStoreOrderDetailsView : View {
    fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>)
    fun alertUpdateSuccess()
}