package c.m.lapaksembakodonorojo.ui.user.userorderhistory

import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.base.View

interface UserOrderHistoryView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}