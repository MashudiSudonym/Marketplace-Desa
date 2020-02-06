package c.m.lapaksembakodonorojo.ui.user.userorderdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.lapaksembakodonorojo.R
import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.Constants
import kotlinx.android.synthetic.main.activity_user_order_details.*

class UserOrderDetailsActivity : AppCompatActivity(), UserOrderDetailsView {

    private lateinit var presenter: UserOrderDetailsPresenter
    private lateinit var adapter: UserOrderDetailsAdapter
    private var content: MutableList<TemporaryOrderItemProductResponse> = mutableListOf()
    private var orderNumber: String? = ""
    private var orderBy: String? = ""
    private var estimateTotalPrice: Int = 0
    private var deliveryOption: Int? = 0
    private var orderStatus: Int? = 0
    private var orderPaymentStatus: Boolean = false
    private var orderCanceled: Boolean = false
    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order_details)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = UserOrderDetailsPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        setSupportActionBar(toolbar_order_details)
        supportActionBar?.apply {
            title = "Order Details"
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        orderNumber = intent.getStringExtra(Constants.ORDER_NUMBER)

        presenter.getTemporaryOrder(orderNumber.toString())

        adapter = UserOrderDetailsAdapter(content)
        rv_order_details_item_list.setHasFixedSize(true)
        rv_order_details_item_list.adapter = adapter

        swipe_refresh_order_details.setOnRefreshListener {
            swipe_refresh_order_details.isRefreshing = false
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
    }

    override fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>) {
        content.clear()
        content.addAll(temporaryOrderData)
        adapter.notifyDataSetChanged()

        // get data response
        temporaryOrderData.forEach { response ->
            orderBy = response.order_by
            estimateTotalPrice += response.total_price as Int
            deliveryOption = response.delivery_option
            orderCanceled = response.is_canceled
            orderPaymentStatus = response.payment_status
            orderStatus = response.order_status
        }

        // count total price
        totalPrice = estimateTotalPrice

        // show data in text view
        tv_name_of_user_order.text = orderBy
        tv_number_of_order_number.text = orderNumber
        tv_number_estimated_total_price.text = estimateTotalPrice.toString()
        tv_number_of_total_price.text = totalPrice.toString()

        // order status
        when (orderStatus) {
            0 -> tv_status.text = getString(R.string.waiting_status)
            1 -> tv_status.text = getString(R.string.order_accept_status)
            2 -> tv_status.text = getString(R.string.process_status)
            3 -> {
                when (orderCanceled) {
                    true -> tv_status.text = getString(R.string.order_cancel_status)
                    false -> tv_status.text = getString(R.string.order_complete_status)
                }
            }
        }

        // delivery option
        when (deliveryOption) {
            1 -> tv_delivery.text = getString(R.string.take_it_by_yourself)
            2 -> tv_delivery.text = getString(R.string.delivered_to_home)
        }

        // payment status
        when (orderPaymentStatus) {
            true -> tv_payment_status.text = getString(R.string.paid_off_status)
            false -> tv_payment_status.text = getString(R.string.not_yet_paid_off_status)
        }
    }

    override fun getStoreName(storeName: String) {
        tv_name_of_store.text = storeName
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
