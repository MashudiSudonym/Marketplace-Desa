package c.m.marketplacedesa.ui.user.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.database.MarketplaceDesaDatabase
import c.m.marketplacedesa.database.StoreEntity
import c.m.marketplacedesa.model.NotificationCollectionResponse
import c.m.marketplacedesa.model.StoreResponse
import c.m.marketplacedesa.ui.notification.NotificationActivity
import c.m.marketplacedesa.ui.settings.SettingsActivity
import c.m.marketplacedesa.ui.signin.SignInActivity
import c.m.marketplacedesa.ui.user.completeuserprofile.CompleteUserProfileActivity
import c.m.marketplacedesa.ui.user.userorder.UserOrderActivity
import c.m.marketplacedesa.ui.user.userordercart.UserOrderCartActivity
import c.m.marketplacedesa.ui.user.userprofile.UserProfileActivity
import c.m.marketplacedesa.ui.user.userstore.UserStoreActivity
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.gone
import c.m.marketplacedesa.util.visible
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.actionitembadge.library.ActionItemBadge
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var marketplaceDesaDatabase: MarketplaceDesaDatabase
    private lateinit var presenter: MainPresenter
    private lateinit var mainAdapter: MainAdapter
    private lateinit var badgeSharedPreferences: SharedPreferences
    private val contentStore: MutableList<StoreResponse> = mutableListOf()
    private val localContentStore: MutableList<StoreEntity> = mutableListOf()
    private var badgeCount: Int = 0
    private var badgeSharedPreferencesValue: Int = 0
    private var getOrderNumberValue: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPresenter()
        onAttachView()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        // Check new notification
        presenter.checkNewNotification()

        // local database declaration
        marketplaceDesaDatabase = MarketplaceDesaDatabase.getDatabase(this)
        marketplaceDesaDatabase.storeDao()

        setSupportActionBar(toolbar_main)
        supportActionBar?.apply { title = getString(R.string.app_name) }

        // get store content data
        presenter.getStore()

        // Setup Store RecyclerView
        setupStoreRecyclerView()

        // Refresh data with SwipeRefresh
        swipe_refresh_main.setOnRefreshListener {
            swipe_refresh_main.isRefreshing = false
            // refresh store content data
            presenter.getStore()
        }

        // badge shopping cart
        badgeSharedPreferences = this.getSharedPreferences(
            getString(R.string.order_shared_preferences_name),
            Context.MODE_PRIVATE
        ) ?: return
        badgeSharedPreferencesValue = badgeSharedPreferences.getInt(
            getString(R.string.badge_shared_preferences_value_key),
            Constants.DEFAULT_INT_VALUE
        )
        // get order number
        getOrderNumberValue = badgeSharedPreferences.getString(
            getString(R.string.order_number_value_key),
            Constants.DEFAULT_STRING_VALUE
        )

        // check value of badgeSharedPreferencesValue and badgeCount
        if (badgeSharedPreferencesValue != 0) {
            badgeCount = badgeSharedPreferencesValue
            invalidateOptionsMenu()
        }

        // check order number
        if (badgeCount == 0 && getOrderNumberValue != "") {
            with(badgeSharedPreferences.edit()) {
                putString(
                    getString(R.string.order_number_value_key),
                    Constants.DEFAULT_STRING_VALUE
                )
                commit()
            }
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onStart() {
        super.onStart()

        // for new user check user profile data
        presenter.checkUserData()

        // check notification
        presenter.checkNewNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
    }

    override fun showLoading() {
        shimmerStart()
        tv_no_data_main.gone()
        rv_store.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        tv_no_data_main.gone()
        rv_store.visible()
    }

    override fun hideSearchLoading() {
        shimmerStop()
        tv_no_data_main.gone()
        rv_store.gone()
    }

    override fun showNoDataResult() {
        shimmerStop()
        tv_no_data_main.visible()
        rv_store.gone()
    }

    override fun getStore(storeData: List<StoreResponse>) {
        contentStore.clear()
        contentStore.addAll(storeData)
        mainAdapter.notifyDataSetChanged()

        // save to local database
        storeData.forEach {
            val storeEntity = StoreEntity(
                0L,
                it.uid,
                it.name,
                it.address,
                it.phone,
                it.owner,
                it.store_geopoint.latitude,
                it.store_geopoint.longitude
            )

            localContentStore.addAll(listOf(storeEntity))
        }

        CoroutineScope(Dispatchers.IO).launch {
            marketplaceDesaDatabase.storeDao().updateContent(localContentStore)
        }
    }

    override fun returnToCompleteUserProfile() {
        finish() // close this activity
        startActivity<CompleteUserProfileActivity>() // open complete user profile activity
    }

    override fun returnToSignInActivity() {
        finish() // close this activity
        startActivity<SignInActivity>() // open sign in activity
    }

    override fun getNotificationCount(notificationData: List<NotificationCollectionResponse>) {
        Log.d(Constants.DEBUG_TAG, "${notificationData.size}")

        if (notificationData.isNotEmpty()) {
            Snackbar.make(
                layout_main,
                getString(R.string.notification_alert),
                Snackbar.LENGTH_INDEFINITE
            ).setAction("SHOW") {
                startActivity<NotificationActivity>()
            }.show()
        }
    }

    private fun initPresenter() {
        presenter = MainPresenter()
    }

    private fun setupStoreRecyclerView() {
        mainAdapter = MainAdapter(contentStore) { response ->
            startActivity<UserStoreActivity>(
                Constants.UID to response.uid,
                Constants.NAME to response.name,
                Constants.ADDRESS to response.address,
                Constants.OWNER_UID to response.owner,
                Constants.STORE_LATITUDE to response.store_geopoint.latitude,
                Constants.STORE_LONGITUDE to response.store_geopoint.longitude,
                Constants.PHONE to response.phone
            )
        }

        rv_store.setHasFixedSize(true)
        rv_store.adapter = mainAdapter
    }

    // shimmer loading animation start
    private fun shimmerStart() {
        shimmer_frame_main.visible()
        shimmer_frame_main.startShimmer()
    }

    // shimmer loading animation stop
    private fun shimmerStop() {
        shimmer_frame_main.gone()
        shimmer_frame_main.stopShimmer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_main, menu)
        if (badgeCount > 0) {
            ActionItemBadge.update(
                this,
                menu?.findItem(R.id.menu_cart),
                getDrawable(R.drawable.ic_shopping_cart),
                ActionItemBadge.BadgeStyles.GREEN,
                badgeCount
            )
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cart -> {
                startActivity<UserOrderCartActivity>()
                true
            }
            R.id.menu_notification -> {
                startActivity<NotificationActivity>()
                true
            }
            R.id.menu_user -> {
                startActivity<UserProfileActivity>()
                true
            }
            R.id.menu_my_order -> {
                startActivity<UserOrderActivity>()
                true
            }
            R.id.menu_setting -> {
                startActivity<SettingsActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
