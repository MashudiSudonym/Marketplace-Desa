package c.m.marketplacedesa.ui.user.userordercart

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.ui.user.main.MainActivity
import c.m.marketplacedesa.util.Constants
import kotlinx.android.synthetic.main.activity_user_order_cart.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton

class UserOrderCartActivity : AppCompatActivity(), UserOrderCartView {

    private lateinit var presenter: UserOrderCartPresenter
    private lateinit var adapter: UserOrderCartAdapter
    private lateinit var badgeSharedPreferences: SharedPreferences
    private lateinit var firebaseSharedPreferences: SharedPreferences
    private lateinit var userStoreOrderSharedPreferences: SharedPreferences
    private lateinit var radioButtonDeliveryOption: RadioButton
    private var selectedDeliveryOption: Int? = 0
    private var contentList: MutableList<TemporaryOrderItemProductResponse> = mutableListOf()
    private var getOrderNumberValue: String? = ""
    private var orderNumber: String? = ""
    private var orderBy: String? = ""
    private var estimateTotalPrice: Int? = 0
    private var totalPrice: Int? = 0
    private var deliveryOption: Int? = 1
    private var storeOwnerUID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order_cart)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = UserOrderCartPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        setSupportActionBar(toolbar_order_cart)
        supportActionBar?.apply {
            title = getString(R.string.checkout)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // SharedPreferences initiate
        userStoreOrderSharedPreferences = this.getSharedPreferences(
            getString(R.string.user_store_order_shared_preferences_name),
            Context.MODE_PRIVATE
        ) ?: return
        firebaseSharedPreferences = this.getSharedPreferences(
            getString(R.string.firebase_shared_preferences),
            Context.MODE_PRIVATE
        ) ?: return
        badgeSharedPreferences = this.getSharedPreferences(
            getString(R.string.order_shared_preferences_name),
            Context.MODE_PRIVATE
        ) ?: return
        // get order number
        getOrderNumberValue = badgeSharedPreferences.getString(
            getString(R.string.order_number_value_key),
            Constants.DEFAULT_STRING_VALUE
        )

        // get data
        presenter.getTemporaryOrder(getOrderNumberValue.toString())

        // Swipe refresh
        swipe_refresh_order_cart.setOnRefreshListener {
            swipe_refresh_order_cart.isRefreshing = false
            // refresh content
            presenter.getTemporaryOrder(getOrderNumberValue.toString())
        }

        // recycler view
        setupItemOrderListRecyclerView()
    }

    private fun setupItemOrderListRecyclerView() {
        adapter = UserOrderCartAdapter(contentList)
        rv_cart_order_list.adapter = adapter
        rv_cart_order_list.setHasFixedSize(true)
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
    }

    override fun returnToMainActivity() {
        finish() // finish this activity
        startActivity<MainActivity>()
    }

    override fun getTemporaryOrder(temporaryOrderData: List<TemporaryOrderItemProductResponse>) {
        // initiate data to recycler view adapter
        contentList.clear()
        contentList.addAll(temporaryOrderData)
        adapter.notifyDataSetChanged()

        // get user data for user view
        temporaryOrderData.forEach { response ->
            orderNumber = response.order_number
            orderBy = response.order_by
            //totalPrice = response.total_price?.let { totalPrice?.plus(it) }
            estimateTotalPrice = response.total_price
            deliveryOption = response.delivery_option
            storeOwnerUID = response.store_owner_uid
        }

        // count total price
        totalPrice = totalPrice?.plus(estimateTotalPrice as Int)

        // show data in text view
        tv_name_of_user_order.text = orderBy
        tv_number_of_order_number.text = orderNumber
        tv_number_estimated_total_price.text = totalPrice.toString()
        tv_number_of_total_price.text = totalPrice.toString()

        // activate button checkout and cancel order
        if (temporaryOrderData.isNullOrEmpty()) {
            deactiveOrderAndCancelButton()
        } else {
            activeOrderAndCancelButton()
        }

        // check delivery option
        when (deliveryOption) {
            1 -> radio_group_shipping_method.check(R.id.radio_button_take_it_by_yourself)
            2 -> radio_group_shipping_method.check(R.id.radio_button_delivered_to_home)
        }

        // Button Logic
        btn_order.setOnClickListener {
            // check radio button selected position
            radioButtonDeliveryOption =
                findViewById(radio_group_shipping_method.checkedRadioButtonId)

            // Checkout Alert
            alert(
                getString(R.string.no_changes_to_orders),
                getString(R.string.order_confirmation)
            ) {
                yesButton {
                    // check radio button selected or not
                    if (selectedDeliveryOption != -1) {
                        // check radio button value of select
                        when (radioButtonDeliveryOption.text) {
                            getString(R.string.take_it_by_yourself) -> {
                                presenter.updateDeliveryOption(orderNumber.toString(), 1)
                                presenter.sendOrderNotification(
                                    storeOwnerUID.toString(),
                                    "${getString(
                                        R.string.you_have_a_new_order_with_order_number
                                    )} $getOrderNumberValue ${getString(
                                        R.string.from
                                    )} $orderBy",
                                    getString(R.string.yout_have_a_new_order)
                                )
                                deactiveOrderAndCancelButton()
                                clearSharedPreferences()
                            }
                            getString(R.string.delivered_to_home) -> {
                                presenter.updateDeliveryOption(orderNumber.toString(), 2)
                                presenter.sendOrderNotification(
                                    storeOwnerUID.toString(),
                                    "${getString(
                                        R.string.you_have_a_new_order_with_order_number
                                    )} $getOrderNumberValue ${getString(
                                        R.string.from
                                    )} $orderBy",
                                    getString(R.string.yout_have_a_new_order)
                                )
                                deactiveOrderAndCancelButton()
                                clearSharedPreferences()
                            }
                        }
                    }
                }
                noButton { }
            }.show()
        }

        btn_cancel_order.setOnClickListener {
            // cancel order alert
            alert(
                getString(R.string.you_want_to_cancel_the_order),
                getString(R.string.attention)
            ) {
                yesButton {
                    presenter.deleteOrder(getOrderNumberValue.toString())
                    clearSharedPreferences()
                }
                noButton { }
            }.show()
        }
    }

    private fun activeOrderAndCancelButton() {
        btn_order.apply {
            isEnabled = true
            setBackgroundColor(Color.parseColor("#43A047"))
        }
        btn_cancel_order.apply {
            isEnabled = true
            setTextColor(Color.RED)
        }
    }

    private fun deactiveOrderAndCancelButton() {
        btn_order.apply {
            isEnabled = false
            setBackgroundColor(Color.LTGRAY)
        }
        btn_cancel_order.apply {
            isEnabled = false
            setTextColor(Color.LTGRAY)
        }
    }

    private fun clearSharedPreferences() {
        with(userStoreOrderSharedPreferences.edit()) {
            clear()
            commit()
        }
        with(firebaseSharedPreferences.edit()) {
            clear()
            commit()
        }
        with(badgeSharedPreferences.edit()) {
            clear()
            commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
