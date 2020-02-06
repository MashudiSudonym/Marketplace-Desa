package c.m.lapaksembakodonorojojepara.ui.user.userorderhistory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.lapaksembakodonorojojepara.R
import c.m.lapaksembakodonorojojepara.model.TemporaryOrderItemProductResponse
import c.m.lapaksembakodonorojojepara.ui.user.userorderdetails.UserOrderDetailsActivity
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.gone
import c.m.lapaksembakodonorojojepara.util.visible
import kotlinx.android.synthetic.main.activity_user_order_history.*
import org.jetbrains.anko.startActivity

class UserOrderHistoryActivity : AppCompatActivity(), UserOrderHistoryView {

    private lateinit var presenter: UserOrderHistoryPresenter
    private lateinit var adapter: UserOrderHistoryAdapter
    private var content: MutableList<TemporaryOrderItemProductResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order_history)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = UserOrderHistoryPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()
        presenter.getUserOrder()

        setSupportActionBar(toolbar_user_order_history)
        supportActionBar?.apply {
            title = getString(R.string.my_order_history)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        adapter = UserOrderHistoryAdapter(content) {
            startActivity<UserOrderDetailsActivity>(
                Constants.ORDER_NUMBER to it.order_number
            )
        }
        rv_user_order_history.setHasFixedSize(true)
        rv_user_order_history.adapter = adapter

        swipe_refresh_user_order_history.setOnRefreshListener {
            swipe_refresh_user_order_history.isRefreshing = false
            presenter.getUserOrder()
        }
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
        rv_user_order_history.gone()
        tv_no_user_order_history.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        rv_user_order_history.visible()
        tv_no_user_order_history.gone()
    }

    override fun showNoDataResult() {
        shimmerStop()
        rv_user_order_history.gone()
        tv_no_user_order_history.visible()
    }

    override fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>) {
        content.clear()
        content.addAll(userOrder)
        adapter.notifyDataSetChanged()
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_user_order_history.visible()
        shimmer_frame_user_order_history.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_user_order_history.gone()
        shimmer_frame_user_order_history.stopShimmer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
