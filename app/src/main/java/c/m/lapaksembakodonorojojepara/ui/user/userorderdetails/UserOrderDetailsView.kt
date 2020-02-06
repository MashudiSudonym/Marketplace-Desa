package c.m.lapaksembakodonorojojepara.ui.user.userorderdetails

import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface UserOrderDetailsView : View {
    fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>)
    fun getStoreName(storeName: String)
}