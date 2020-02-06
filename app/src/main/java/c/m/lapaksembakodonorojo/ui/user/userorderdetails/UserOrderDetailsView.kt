package c.m.lapaksembakodonorojo.ui.user.userorderdetails

import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.base.View

interface UserOrderDetailsView : View {
    fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>)
    fun getStoreName(storeName: String)
}