package c.m.marketplacedesa.ui.user.userproductdetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.ui.user.userordercart.OrderCartActivity
import c.m.marketplacedesa.util.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_user_product_details.*
import org.jetbrains.anko.startActivity

class UserProductDetailsActivity : AppCompatActivity() {

    private var uid: String? = ""
    private var name: String? = ""
    private var imgProduct: String? = ""
    private var price: Int? = 0
    private var stock: Int? = 0
    private var storeUID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_product_details)

        val intent = intent
        uid = intent.getStringExtra(Constants.UID)
        name = intent.getStringExtra(Constants.NAME)
        imgProduct = intent.getStringExtra(Constants.IMG_PRODUCT)
        price = intent.getIntExtra(Constants.PRICE, 0)
        stock = intent.getIntExtra(Constants.STOCK, 0)
        storeUID = intent.getStringExtra(Constants.STORE_UID)

        setSupportActionBar(toolbar_product_details)
        supportActionBar?.apply {
            title = name
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        tv_name_product_details.text = name
        tv_number_of_price_product_details.text = price.toString()
        tv_number_of_stock_product_details.text = stock.toString()

        Glide.with(this)
            .load(imgProduct)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(img_product_details)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    // app bar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_product_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cart -> {
                startActivity<OrderCartActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
