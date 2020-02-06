package c.m.lapaksembakodonorojojepara.ui.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.lapaksembakodonorojojepara.R
import c.m.lapaksembakodonorojojepara.model.NotificationCollectionResponse
import c.m.lapaksembakodonorojojepara.ui.seller.sellerstoreorderdetails.SellerStoreOrderDetailsActivity
import c.m.lapaksembakodonorojojepara.ui.user.userorderdetails.UserOrderDetailsActivity
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.gone
import c.m.lapaksembakodonorojojepara.util.visible
import kotlinx.android.synthetic.main.activity_notification.*
import org.jetbrains.anko.startActivity

class NotificationActivity : AppCompatActivity(), NotificationView {

    private lateinit var presenter: NotificationPresenter
    private lateinit var adapter: NotificationAdapter
    private var content: MutableList<NotificationCollectionResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = NotificationPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        // get notification data
        presenter.getNotification()

        setSupportActionBar(toolbar_notification)
        supportActionBar?.apply {
            title = getString(R.string.notification)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // setup recycler view
        setupRecyclerView()

        // swipe refresh data
        swipe_refresh_notification.setOnRefreshListener {
            swipe_refresh_notification.isRefreshing = false
            presenter.getNotification()
        }
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(content) {
            // update notification read status
            presenter.setNotificationAsRead(it.order_number.toString())

            if (it.order_title_message == "You Have a New Order") {
                startActivity<SellerStoreOrderDetailsActivity>(
                    Constants.ORDER_NUMBER to it.order_number
                )
            } else if (it.order_title_message == "Anda Memiliki Pesanan Baru") {
                startActivity<SellerStoreOrderDetailsActivity>(
                    Constants.ORDER_NUMBER to it.order_number
                )
            } else {
                startActivity<UserOrderDetailsActivity>(
                    Constants.ORDER_NUMBER to it.order_number
                )
            }
        }
        rv_notification.adapter = adapter
        rv_notification.setHasFixedSize(true)
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
        rv_notification.gone()
        tv_no_notification.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        rv_notification.visible()
        tv_no_notification.gone()
    }

    override fun showNoNotificationResult() {
        shimmerStop()
        rv_notification.gone()
        tv_no_notification.visible()
    }

    override fun getNotification(notificationData: List<NotificationCollectionResponse>) {
        content.clear()
        content.addAll(notificationData)
        adapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_notification.visible()
        shimmer_frame_notification.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_notification.gone()
        shimmer_frame_notification.stopShimmer()
    }
}
