package c.m.lapaksembakodonorojojepara.ui.seller.sellerstoreorderdetails

import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface SellerStoreOrderDetailsView : View {
    fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>)
    fun alertUpdateSuccess()
}