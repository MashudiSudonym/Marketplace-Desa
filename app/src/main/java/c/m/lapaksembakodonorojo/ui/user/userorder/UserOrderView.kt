package c.m.lapaksembakodonorojo.ui.user.userorder

import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.base.View

interface UserOrderView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}