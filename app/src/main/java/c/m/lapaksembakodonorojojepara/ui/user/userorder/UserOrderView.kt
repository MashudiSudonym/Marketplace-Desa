package c.m.lapaksembakodonorojojepara.ui.user.userorder

import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface UserOrderView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}