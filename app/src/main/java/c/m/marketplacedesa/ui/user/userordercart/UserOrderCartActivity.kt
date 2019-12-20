package c.m.marketplacedesa.ui.user.userordercart

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.util.Constants
import kotlinx.android.synthetic.main.activity_user_order_cart.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class UserOrderCartActivity : AppCompatActivity() {

    private lateinit var badgeSharedPreferences: SharedPreferences
    private var getOrderNumberValue: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order_cart)
        setSupportActionBar(toolbar_order_cart)
        supportActionBar?.apply {
            title = getString(R.string.checkout)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // SharedPreferences initiate
        badgeSharedPreferences = this.getSharedPreferences(
            getString(R.string.order_shared_preferences_name),
            Context.MODE_PRIVATE
        ) ?: return
        // get order number
        getOrderNumberValue = badgeSharedPreferences.getString(
            getString(R.string.order_number_value_key),
            Constants.DEFAULT_STRING_VALUE
        )

        Log.d(Constants.DEBUG_TAG, getOrderNumberValue.toString())

        // Button Logic
        btn_order.setOnClickListener {
            // Checkout Alert
            alert(
                getString(R.string.no_changes_to_orders),
                getString(R.string.order_confirmation)
            ) {
                yesButton { toast("Yes") }
                noButton { toast("No") }
            }.show()
        }

        btn_cancel_order.setOnClickListener {
            // cancel order alert
            alert(
                getString(R.string.you_want_to_cancel_the_order),
                getString(R.string.attention)
            ) {
                yesButton { toast("Yes") }
                noButton { toast("No") }
            }.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
