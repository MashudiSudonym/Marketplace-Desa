package c.m.marketplacedesa.ui.user.userstore

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.util.gone
import c.m.marketplacedesa.util.visible
import kotlinx.android.synthetic.main.activity_user_store.*

class UserStoreActivity : AppCompatActivity() {

    private var uid: String? = ""
    private var name: String? = ""
    private var address: String? = ""
    private var owner: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_store)
        setSupportActionBar(toolbar_product)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        uid = intent.getStringExtra("uid")
        name = intent.getStringExtra("name")
        address = intent.getStringExtra("address")
        owner = intent.getStringExtra("owner")

        tv_store_name.text = name
        tv_store_address.text = address
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_product.visible()
        shimmer_frame_product.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_product.gone()
        shimmer_frame_product.stopShimmer()
    }

    // app bar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_store, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cart -> true
            R.id.menu_details_store -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}
