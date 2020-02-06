package c.m.lapaksembakodonorojojepara.ui.user.userorderhistory

import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.util.base.View

interface UserOrderHistoryView : View {
    fun showLoading()
    fun hideLoading()
    fun showNoDataResult()
    fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>)
}