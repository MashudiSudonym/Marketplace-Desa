package c.m.lapaksembakodonorojo.ui.seller.sellerstoreorderdetails

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import c.m.lapaksembakodonorojo.R
import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojo.util.Constants
import kotlinx.android.synthetic.main.activity_seller_store_order_details.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

class SellerStoreOrderDetailsActivity : AppCompatActivity(), SellerStoreOrderDetailsView {

    private lateinit var presenter: SellerStoreOrderDetailsPresenter
    private lateinit var adapter: SellerStoreOrderDetailsAdapter
    private var content: MutableList<TemporaryOrderItemProductResponse> = mutableListOf()
    private var orderNumber: String? = ""
    private var storeUID: String? = ""
    private var storeOwnerUID: String? = ""
    private var orderBy: String? = ""
    private var estimateTotalPrice: Int = 0
    private var deliveryOption: Int? = 0
    private var orderStatus: Int? = 0
    private var orderPaymentStatus: Boolean = false
    private var orderCanceled: Boolean = false
    private var totalPrice: Int = 0
    private var userOrderUID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_store_order_details)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = SellerStoreOrderDetailsPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        setSupportActionBar(toolbar_seller_order_details)
        supportActionBar?.apply {
            title = getString(R.string.order_received)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        orderNumber = intent.getStringExtra(Constants.ORDER_NUMBER)

        swipe_refresh_seller_order_details.setOnRefreshListener {
            swipe_refresh_seller_order_details.isRefreshing = false
        }

        presenter.getTemporaryOrder(orderNumber.toString())

        adapter = SellerStoreOrderDetailsAdapter(content)
        rv_seller_store_order_detail.setHasFixedSize(true)
        rv_seller_store_order_detail.adapter = adapter
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
            storeUID = response.store_uid
            storeOwnerUID = response.store_owner_uid
            userOrderUID = response.user_order_uid
        }

        // count total price
        totalPrice = estimateTotalPrice

        // show data in text view
        tv_name_of_user_order.text = orderBy
        tv_number_of_order_number.text = orderNumber
        tv_number_estimated_total_price.text = estimateTotalPrice.toString()
        tv_number_of_total_price.text = totalPrice.toString()

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

        when (orderCanceled) {
            true -> tv_payment_status.text = getString(R.string.order_cancel_status)
        }

        // order status
        when (orderStatus) {
            0 -> radio_group_order_progress.check(R.id.radio_button_waiting_status)
            1 -> radio_group_order_progress.check(R.id.radio_button_accept_status)
            2 -> radio_group_order_progress.check(R.id.radio_button_processed_status)
            3 -> radio_group_order_progress.check(R.id.radio_button_complete_status)
        }

        // finish order
        btn_finish_order.setOnClickListener {
            presenter.updatePaymentOrderStatus(orderNumber.toString())
            presenter.sendOrderStatusNotification(
                storeUID.toString(),
                orderNumber.toString(),
                orderBy.toString(),
                userOrderUID.toString(),
                "${getString(R.string.your_order_status_title)} ${getString(R.string.paid_off_status)}",
                "${getString(R.string.update_order_status)} : $orderNumber"
            )
            deactivateOrderAndCancelButton()
            deactivatedRadioButtonOrderStatus()
        }

        // cancel order
        btn_cancel_order.setOnClickListener {
            presenter.updateCancelOrderStatus(orderNumber.toString())
            presenter.sendOrderStatusNotification(
                storeUID.toString(),
                orderNumber.toString(),
                orderBy.toString(),
                userOrderUID.toString(),
                "${getString(R.string.your_order_status_title)} ${getString(R.string.order_cancel_status)}",
                "${getString(R.string.update_order_status)} : $orderNumber"
            )
            deactivateOrderAndCancelButton()
            deactivatedRadioButtonOrderStatus()
        }

        // view status on history order
        if (orderCanceled) {
            deactivateOrderAndCancelButton()
            deactivatedRadioButtonOrderStatus()
        }

        if (orderPaymentStatus) {
            deactivateOrderAndCancelButton()
            deactivatedRadioButtonOrderStatus()
        }
    }

    private fun deactivatedRadioButtonOrderStatus() {
        radio_button_waiting_status.isEnabled = false
        radio_button_accept_status.isEnabled = false
        radio_button_processed_status.isEnabled = false
        radio_button_complete_status.isEnabled = false
    }

    override fun alertUpdateSuccess() {
        alert(getString(R.string.update_order_status_success), getString(R.string.update_status)) {
            okButton { }
        }.show()
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_button_waiting_status ->
                    if (checked) {
                        presenter.updateProgressOrderStatus(orderNumber.toString(), 0)
                        presenter.sendOrderStatusNotification(
                            storeUID.toString(),
                            orderNumber.toString(),
                            orderBy.toString(),
                            userOrderUID.toString(),
                            "${getString(R.string.your_order_status_title)} ${getString(R.string.waiting_status)}",
                            "${getString(R.string.update_order_status)} : $orderNumber"
                        )
                    }
                R.id.radio_button_accept_status ->
                    if (checked) {
                        presenter.updateProgressOrderStatus(orderNumber.toString(), 1)
                        presenter.sendOrderStatusNotification(
                            storeUID.toString(),
                            orderNumber.toString(),
                            orderBy.toString(),
                            userOrderUID.toString(),
                            "${getString(R.string.your_order_status_title)} ${getString(R.string.order_accept_status)}",
                            "${getString(R.string.update_order_status)} : $orderNumber"
                        )
                    }
                R.id.radio_button_processed_status ->
                    if (checked) {
                        presenter.updateProgressOrderStatus(orderNumber.toString(), 2)
                        presenter.sendOrderStatusNotification(
                            storeUID.toString(),
                            orderNumber.toString(),
                            orderBy.toString(),
                            userOrderUID.toString(),
                            "${getString(R.string.your_order_status_title)} ${getString(R.string.process_status)}",
                            "${getString(R.string.update_order_status)} : $orderNumber"
                        )
                    }
                R.id.radio_button_complete_status ->
                    if (checked) {
                        presenter.updateProgressOrderStatus(orderNumber.toString(), 3)
                        presenter.sendOrderStatusNotification(
                            storeUID.toString(),
                            orderNumber.toString(),
                            orderBy.toString(),
                            userOrderUID.toString(),
                            "${getString(R.string.your_order_status_title)} ${getString(R.string.order_complete_status)}",
                            "${getString(R.string.update_order_status)} : $orderNumber"
                        )
                    }
            }
        }
    }

    private fun deactivateOrderAndCancelButton() {
        btn_finish_order.apply {
            isEnabled = false
            setBackgroundColor(Color.LTGRAY)
        }
        btn_cancel_order.apply {
            isEnabled = false
            setTextColor(Color.LTGRAY)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
