package c.m.marketplacedesa.ui.seller.sellerstoreinformation

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.util.Constants
import kotlinx.android.synthetic.main.activity_seller_store_information.*

class SellerStoreInformationActivity : AppCompatActivity() {

    private var ownerUID: String? = ""
    private var storeUID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_store_information)

        val intent = intent
        ownerUID = intent.getStringExtra(Constants.OWNER_UID)
        storeUID = intent.getStringExtra(Constants.STORE_UID)

        setSupportActionBar(toolbar_seller_store_information)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        collapsing_toolbar_seller_store_information.apply {
            setExpandedTitleColor(Color.WHITE)
        }

        tv_title_seller_store_information.text = ownerUID
        tv_address_seller_store_information.text = ownerUID
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
