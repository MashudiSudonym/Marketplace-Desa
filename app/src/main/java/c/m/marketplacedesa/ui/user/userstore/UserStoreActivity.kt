package c.m.marketplacedesa.ui.user.userstore

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import c.m.marketplacedesa.R
import c.m.marketplacedesa.data.remote.response.ProductsResponse
import c.m.marketplacedesa.ui.user.userproductdetails.UserProductDetailsActivity
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.gone
import c.m.marketplacedesa.util.visible
import kotlinx.android.synthetic.main.activity_user_store.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserStoreActivity : AppCompatActivity() {

    private var uid: String? = ""
    private var name: String? = ""
    private var address: String? = ""
    private var ownerUID: String? = ""
    private val userStoreViewModel: UserStoreViewModel by viewModel()
    private lateinit var userStoreAdapter: UserStoreAdapter
    private var contentProduct: MutableList<ProductsResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_store)
        setSupportActionBar(toolbar_product)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val intent = intent
        uid = intent.getStringExtra(Constants.UID)
        name = intent.getStringExtra(Constants.NAME)
        address = intent.getStringExtra(Constants.ADDRESS)
        ownerUID = intent.getStringExtra(Constants.OWNER_UID)

        tv_store_name.text = name
        tv_store_address.text = address

        // Setup Store RecyclerView
        setupProductRecyclerView()

        // Refresh data with SwipeRefresh
        swipe_refresh_product.setOnRefreshListener {
            swipe_refresh_product.isRefreshing = false
            observeGetProductsViewModel()
        }
    }

    private fun setupProductRecyclerView() {
        userStoreAdapter = UserStoreAdapter(contentProduct) { response ->
            startActivity<UserProductDetailsActivity>(
                Constants.UID to response.uid,
                Constants.NAME to response.name,
                Constants.IMG_PRODUCT to response.image_product,
                Constants.PRICE to response.price,
                Constants.STOCK to response.stock,
                Constants.STORE_UID to response.store
            )
        }

        observeGetProductsViewModel()

        rv_product.setHasFixedSize(true)
        rv_product.adapter = userStoreAdapter
    }

    private fun observeGetProductsViewModel() {
        userStoreViewModel.getStoreUID(uid.toString())
        userStoreViewModel.getProducts.observe(this, Observer { data ->
            // on UI loading
            shimmerStart()
            rv_search_product.gone()
            rv_product.gone()
            tv_no_data_product.gone()

            if (!data.isNullOrEmpty()) {
                // on UI have data
                shimmerStop()
                rv_search_product.gone()
                rv_product.visible()
                tv_no_data_product.gone()

                // Initiate data to adapter
                contentProduct.clear()
                contentProduct.addAll(data)
                userStoreAdapter.notifyDataSetChanged()

            } else {
                // on UI don't have data
                shimmerStop()
                rv_search_product.gone()
                rv_product.gone()
                tv_no_data_product.visible()
            }
        })
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
