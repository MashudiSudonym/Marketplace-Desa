package c.m.marketplacedesa.ui.user.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import c.m.marketplacedesa.R
import c.m.marketplacedesa.data.remote.response.StoreResponse
import c.m.marketplacedesa.ui.settings.SettingsActivity
import c.m.marketplacedesa.ui.user.userprofile.UserProfileActivity
import c.m.marketplacedesa.ui.user.userstore.UserStoreActivity
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.gone
import c.m.marketplacedesa.util.visible
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var mainAdapter: MainAdapter
    private val contentStore: MutableList<StoreResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)
        supportActionBar?.apply { title = getString(R.string.app_name) }

        // Setup Store RecyclerView
        setupStoreRecyclerView()

        // Refresh data with SwipeRefresh
        swipe_refresh_main.setOnRefreshListener {
            swipe_refresh_main.isRefreshing = false
            observeGetStoreViewModel()
        }
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

        observeGetStoreViewModel()

        rv_store.setHasFixedSize(true)
        rv_store.adapter = mainAdapter
    }

    private fun observeGetStoreViewModel() {
        mainViewModel.getStoreContent().observe(this, Observer { data ->
            // on UI loading
            shimmerStart()
            rv_search_store.gone()
            rv_store.gone()
            tv_no_data_main.gone()

            if (!data.isNullOrEmpty()) {
                // on UI have data
                shimmerStop()
                rv_search_store.gone()
                rv_store.visible()
                tv_no_data_main.gone()

                // Initiate data to adapter
                contentStore.clear()
                contentStore.addAll(data)
                mainAdapter.notifyDataSetChanged()
            } else {
                // on UI don't have data
                shimmerStop()
                rv_search_store.gone()
                rv_store.gone()
                tv_no_data_main.visible()
            }
        })
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
            R.id.menu_cart -> true
            R.id.menu_user -> {
                startActivity<UserProfileActivity>()
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
