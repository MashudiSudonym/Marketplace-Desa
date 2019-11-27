package c.m.marketplacedesa.ui.user.userordercart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import kotlinx.android.synthetic.main.activity_user_order_cart.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class UserOrderCartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order_cart)
        setSupportActionBar(toolbar_order_cart)
        supportActionBar?.apply {
            title = getString(R.string.checkout)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Checkout Alert
        btn_order.setOnClickListener {
            alert(
                getString(R.string.no_changes_to_orders),
                getString(R.string.order_confirmation)
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
