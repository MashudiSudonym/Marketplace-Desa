package c.m.marketplacedesa.ui.user.userorderdetails

import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.util.base.View

interface UserOrderDetailsView : View {
    fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>)
}