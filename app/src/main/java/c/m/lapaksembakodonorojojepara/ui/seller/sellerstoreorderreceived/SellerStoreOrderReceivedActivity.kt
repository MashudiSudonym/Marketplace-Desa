package c.m.lapaksembakodonorojojepara.ui.seller.sellerstoreorderreceived

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.lapaksembakodonorojojepara.R
import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.ui.seller.sellerstoreorderdetails.SellerStoreOrderDetailsActivity
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.gone
import c.m.lapaksembakodonorojojepara.util.visible
import kotlinx.android.synthetic.main.activity_seller_store_order_received.*
import org.jetbrains.anko.startActivity

class SellerStoreOrderReceivedActivity : AppCompatActivity(), SellerStoreOrderReceivedView {

    private lateinit var presenter: SellerStoreOrderReceivedPresenter
    private lateinit var adapter: SellerStoreOrderReceivedAdapter
    private var content: MutableList<TemporaryOrderItemProductResponse> = mutableListOf()
    private var storeUID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_store_order_received)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = SellerStoreOrderReceivedPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        setSupportActionBar(toolbar_seller_store_order_received)
        supportActionBar?.apply {
            title = getString(R.string.order_received)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        storeUID = intent.getStringExtra(Constants.STORE_UID)

        // get order list
        presenter.getUserOrder(storeUID.toString())

        swipe_refresh_seller_store_order_received.setOnRefreshListener {
            swipe_refresh_seller_store_order_received.isRefreshing = false
            presenter.getUserOrder(storeUID.toString())
        }

        adapter = SellerStoreOrderReceivedAdapter(content) {
            startActivity<SellerStoreOrderDetailsActivity>(
                Constants.ORDER_NUMBER to it.order_number
            )
        }
        rv_seller_store_order_received.setHasFixedSize(true)
        rv_seller_store_order_received.adapter = adapter
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
        rv_seller_store_order_received.gone()
        tv_no_seller_store_order_received.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        rv_seller_store_order_received.visible()
        tv_no_seller_store_order_received.gone()
    }

    override fun showNoDataResult() {
        shimmerStop()
        rv_seller_store_order_received.gone()
        tv_no_seller_store_order_received.visible()
    }

    override fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>) {
        content.clear()
        content.addAll(userOrder)
        adapter.notifyDataSetChanged()
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_seller_store_order_received.visible()
        shimmer_frame_seller_store_order_received.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_seller_store_order_received.gone()
        shimmer_frame_seller_store_order_received.stopShimmer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
