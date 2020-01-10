package c.m.marketplacedesa.ui.seller.sellerstoreorderdetails

import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.util.base.View

interface SellerStoreOrderDetailsView : View {
    fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>)
    fun alertUpdateSuccess()
}