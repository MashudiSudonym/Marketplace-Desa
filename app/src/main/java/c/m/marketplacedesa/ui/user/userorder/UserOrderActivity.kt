package c.m.marketplacedesa.ui.user.userorder

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import c.m.marketplacedesa.ui.user.userorderdetails.UserOrderDetailsActivity
import c.m.marketplacedesa.ui.user.userorderhistory.UserOrderHistoryActivity
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.gone
import c.m.marketplacedesa.util.visible
import kotlinx.android.synthetic.main.activity_user_order.*
import org.jetbrains.anko.startActivity

class UserOrderActivity : AppCompatActivity(), UserOrderView {

    private lateinit var presenter: UserOrderPresenter
    private lateinit var adapter: UserOrderAdapter
    private var content: MutableList<TemporaryOrderItemProductResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = UserOrderPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()
        presenter.getUserOrder()

        setSupportActionBar(toolbar_user_order)
        supportActionBar?.apply {
            title = getString(R.string.my_order)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        adapter = UserOrderAdapter(content) {
            startActivity<UserOrderDetailsActivity>(
                Constants.ORDER_NUMBER to it.order_number
            )
        }
        rv_user_order.setHasFixedSize(true)
        rv_user_order.adapter = adapter

        swipe_refresh_user_order.setOnRefreshListener {
            swipe_refresh_user_order.isRefreshing = false
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
        rv_user_order.gone()
        tv_no_user_order.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        rv_user_order.visible()
        tv_no_user_order.gone()
    }

    override fun showNoDataResult() {
        shimmerStop()
        rv_user_order.gone()
        tv_no_user_order.visible()
    }

    override fun getUserOrder(userOrder: List<TemporaryOrderItemProductResponse>) {
        content.clear()
        content.addAll(userOrder)
        adapter.notifyDataSetChanged()
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_user_order.visible()
        shimmer_frame_user_order.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_user_order.gone()
        shimmer_frame_user_order.stopShimmer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_order, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_order_history -> {
                startActivity<UserOrderHistoryActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
