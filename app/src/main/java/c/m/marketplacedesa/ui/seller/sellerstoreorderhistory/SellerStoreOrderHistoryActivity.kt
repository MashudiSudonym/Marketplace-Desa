package c.m.marketplacedesa.ui.seller.sellerstoreorderhistory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.ui.seller.sellerstoreorderdetails.SellerStoreOrderDetailsActivity
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.gone
import c.m.marketplacedesa.util.visible
import kotlinx.android.synthetic.main.activity_seller_store_order_history.*
import org.jetbrains.anko.startActivity

class SellerStoreOrderHistoryActivity : AppCompatActivity(), SellerStoreOrderHistoryView {

    private lateinit var presenter: SellerStoreOrderHistoryPresenter
    private lateinit var adapter: SellerStoreOrderHistoryAdapter
    private var content: MutableList<TemporaryOrderItemProductResponse> = mutableListOf()
    private var storeUID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_store_order_history)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = SellerStoreOrderHistoryPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        setSupportActionBar(toolbar_seller_store_order_history)
        supportActionBar?.apply {
            title = getString(R.string.my_order_history)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        storeUID = intent.getStringExtra(Constants.STORE_UID)

        // get order history data
        presenter.getUserOrder(storeUID.toString())

        swipe_refresh_seller_store_order_history.setOnRefreshListener {
            swipe_refresh_seller_store_order_history.isRefreshing = false
            presenter.getUserOrder(storeUID.toString())
        }

        adapter = SellerStoreOrderHistoryAdapter(content) {
            startActivity<SellerStoreOrderDetailsActivity>(
                Constants.ORDER_NUMBER to it.order_number
            )
        }
        rv_seller_store_order_history.setHasFixedSize(true)
        rv_seller_store_order_history.adapter = adapter
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
        rv_seller_store_order_history.gone()
        tv_no_seller_store_order_history.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        rv_seller_store_order_history.visible()
        tv_no_seller_store_order_history.gone()
    }

    override fun showNoDataResult() {
        shimmerStop()
        rv_seller_store_order_history.gone()
        tv_no_seller_store_order_history.visible()
    }

    override fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>) {
        content.clear()
        content.addAll(userOrder)
        adapter.notifyDataSetChanged()
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_seller_store_order_history.visible()
        shimmer_frame_seller_store_order_history.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_seller_store_order_history.gone()
        shimmer_frame_seller_store_order_history.stopShimmer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
