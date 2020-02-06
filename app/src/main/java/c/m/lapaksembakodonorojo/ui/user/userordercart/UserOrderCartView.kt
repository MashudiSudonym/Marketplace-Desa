package c.m.lapaksembakodonorojo.ui.user.userordercart

import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.base.View

interface UserOrderCartView : View {
    fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>)
    fun returnToMainActivity()
}