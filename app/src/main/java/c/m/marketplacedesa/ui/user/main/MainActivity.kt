package c.m.marketplacedesa.ui.user.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.StoreResponse
import c.m.marketplacedesa.ui.settings.SettingsActivity
import c.m.marketplacedesa.ui.signin.SignInActivity
import c.m.marketplacedesa.ui.user.userordercart.UserOrderCartActivity
import c.m.marketplacedesa.ui.user.userprofile.UserProfileActivity
import c.m.marketplacedesa.ui.user.userstore.UserStoreActivity
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.gone
import c.m.marketplacedesa.util.visible
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var presenter: MainPresenter
    private lateinit var mainAdapter: MainAdapter
    private val contentStore: MutableList<StoreResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPresenter()
        onAttachView()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

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
        tv_no_data_main.gone()
        rv_search_store.gone()
        rv_store.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        tv_no_data_main.gone()
        rv_store.visible()
        rv_search_store.gone()
    }

    override fun hideSearchLoading() {
        shimmerStop()
        tv_no_data_main.gone()
        rv_store.gone()
        rv_search_store.visible()
    }

    override fun showNoDataResult() {
        shimmerStop()
        tv_no_data_main.visible()
        rv_search_store.gone()
        rv_store.gone()
    }

    override fun getStore(storeData: List<StoreResponse>) {
        contentStore.clear()
        contentStore.addAll(storeData)
        mainAdapter.notifyDataSetChanged()
    }

    override fun returnToSignInActivity() {
        finish() // close this activity
        startActivity<SignInActivity>() // open sign in activity
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

    // app bar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cart -> {
                startActivity<UserOrderCartActivity>()
                true
            }
            R.id.menu_user -> {
                startActivity<UserProfileActivity>()
                true
            }
            R.id.menu_my_order -> {
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
