package c.m.lapaksembakodonorojo.ui.seller.sellerstoreinformation

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.lapaksembakodonorojo.R
import c.m.lapaksembakodonorojo.model.ProductsResponse
import c.m.lapaksembakodonorojo.model.StoreResponse
import c.m.lapaksembakodonorojo.ui.seller.selleraddproduct.SellerAddProductActivity
import c.m.lapaksembakodonorojo.ui.seller.sellereditproduct.SellerEditProductActivity
import c.m.lapaksembakodonorojo.ui.seller.sellereditstoreinformation.SellerEditStoreInformationActivity
import c.m.lapaksembakodonorojo.ui.seller.sellerstoreorderhistory.SellerStoreOrderHistoryActivity
import c.m.lapaksembakodonorojo.ui.seller.sellerstoreorderreceived.SellerStoreOrderReceivedActivity
import c.m.lapaksembakodonorojo.util.Constants
import c.m.lapaksembakodonorojo.util.gone
import c.m.lapaksembakodonorojo.util.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_seller_store_information.*
import org.jetbrains.anko.startActivity

class SellerStoreInformationActivity : AppCompatActivity(), SellerStoreInformationView {

    private lateinit var presenter: SellerStoreInformationPresenter
    private lateinit var sellerStoreInformationAdapter: SellerStoreInformationAdapter
    private var contentProduct: MutableList<ProductsResponse> = mutableListOf()
    private var ownerUID: String? = ""
    private var storeUID: String? = ""
    private var storeLatitude: Double? = 0.0
    private var storeLongitude: Double? = 0.0
    private var storeName: String? = ""
    private var storeAddress: String? = ""
    private var storeImage: String? = ""
    private var storePhone: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_store_information)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = SellerStoreInformationPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        val intent = intent
        ownerUID = intent.getStringExtra(Constants.OWNER_UID)
        storeUID = intent.getStringExtra(Constants.STORE_UID)

        if (storeUID.isNullOrBlank()) {
            presenter.getStoreByOwnerUID(ownerUID.toString())
        } else {
            presenter.getStoreByStoreUID(storeUID.toString())
        }

        setSupportActionBar(toolbar_seller_store_information)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        collapsing_toolbar_seller_store_information.apply {
            setExpandedTitleColor(Color.WHITE)
        }

        // Refresh data with SwipeRefresh
        swipe_refresh_seller_store_information.setOnRefreshListener {
            swipe_refresh_seller_store_information.isRefreshing = false
            // refresh store content data
            if (storeUID.isNullOrBlank()) {
                presenter.getStoreByOwnerUID(ownerUID.toString())
            } else {
                presenter.getStoreByStoreUID(storeUID.toString())
            }

            // refresh product content data
            presenter.getProduct(storeUID.toString())
        }

        // recycler view product list
        setupProductRecyclerView()

        // button add product
        btn_floating_add_product_seller_store_information.setOnClickListener {
            startActivity<SellerAddProductActivity>(Constants.STORE_UID to storeUID)
        }
    }

    private fun setupProductRecyclerView() {
        sellerStoreInformationAdapter = SellerStoreInformationAdapter(contentProduct) {
            startActivity<SellerEditProductActivity>(
                Constants.UID to it.uid,
                Constants.STORE_UID to it.store,
                Constants.IMG_PRODUCT to it.image_product,
                Constants.NAME to it.name,
                Constants.PRICE to it.price,
                Constants.STOCK to it.stock
            )
        }

        rv_product_seller_store_information.setHasFixedSize(true)
        rv_product_seller_store_information.adapter = sellerStoreInformationAdapter
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
    }

    override fun showLoading() {
        shimmerStart()
        tv_no_data_seller_store_information.gone()
        rv_product_seller_store_information.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        tv_no_data_seller_store_information.gone()
        rv_product_seller_store_information.visible()
    }

    override fun showNoDataResult() {
        shimmerStop()
        tv_no_data_seller_store_information.visible()
        rv_product_seller_store_information.gone()
    }

    override fun getStore(storeData: List<StoreResponse>) {
        storeData.forEach { response ->
            storeLatitude = response.store_geopoint.latitude
            storeLongitude = response.store_geopoint.longitude
            storeName = response.name
            storeAddress = response.address
            storeUID = response.uid
            storeImage = response.image_profile_store
            storePhone = response.phone
        }

        Glide.with(this)
            .load(storeImage)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(img_seller_store_information)

        tv_title_seller_store_information.text = storeName
        tv_address_seller_store_information.text = storeAddress
        tv_phone_number_store_information.text = storePhone

        presenter.getProduct(storeUID.toString())
    }

    override fun getProduct(productData: List<ProductsResponse>) {
        contentProduct.clear()
        contentProduct.addAll(productData)
        sellerStoreInformationAdapter.notifyDataSetChanged()
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_product_seller_store_information.visible()
        shimmer_frame_product_seller_store_information.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_product_seller_store_information.gone()
        shimmer_frame_product_seller_store_information.stopShimmer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_seller_store, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit_info_store -> {
                startActivity<SellerEditStoreInformationActivity>(
                    Constants.STORE_UID to storeUID,
                    Constants.STORE_LATITUDE to storeLatitude,
                    Constants.STORE_LONGITUDE to storeLongitude,
                    Constants.NAME to storeName,
                    Constants.PHONE to storePhone,
                    Constants.ADDRESS to storeAddress,
                    Constants.IMG_PROFILE to storeImage
                )
                true
            }
            R.id.menu_order_received -> {
                startActivity<SellerStoreOrderReceivedActivity>(
                    Constants.STORE_UID to storeUID
                )
                true
            }
            R.id.menu_order_history_store -> {
                startActivity<SellerStoreOrderHistoryActivity>(
                    Constants.STORE_UID to storeUID
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
