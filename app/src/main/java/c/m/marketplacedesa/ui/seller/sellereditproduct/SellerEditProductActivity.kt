package c.m.marketplacedesa.ui.seller.sellereditproduct

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.util.Constants
import kotlinx.android.synthetic.main.activity_seller_edit_product.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class SellerEditProductActivity : AppCompatActivity() {

    private var productUID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_edit_product)
        setSupportActionBar(toolbar_seller_edit_product)
        supportActionBar?.apply {
            title = getString(R.string.edit_product_information)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val intent = intent
        productUID = intent.getStringExtra(Constants.UID)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    // app bar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_seller_add_edit_product, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                alert(
                    getString(R.string.message_check_your_data),
                    getString(R.string.title_check_your_data)
                ) {
                    yesButton {
                        toast("yes")
                    }
                    noButton { }
                }.apply {
                    isCancelable = false
                    show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
