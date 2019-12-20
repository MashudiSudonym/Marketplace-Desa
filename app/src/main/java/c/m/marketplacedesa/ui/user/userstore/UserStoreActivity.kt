package c.m.marketplacedesa.ui.user.userstore

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.ProductsResponse
import c.m.marketplacedesa.model.UsersResponse
import c.m.marketplacedesa.ui.signin.SignInActivity
import c.m.marketplacedesa.ui.user.userordercart.UserOrderCartActivity
import c.m.marketplacedesa.ui.user.userstoredetails.UserStoreDetailsActivity
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.gone
import c.m.marketplacedesa.util.visible
import com.mikepenz.actionitembadge.library.ActionItemBadge
import kotlinx.android.synthetic.main.activity_user_store.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import kotlin.random.Random

class UserStoreActivity : AppCompatActivity(), UserStoreView, UserStoreAddOrRemoveInterface {

    private lateinit var presenter: UserStorePresenter
    private lateinit var userStoreAdapter: UserStoreAdapter
    private lateinit var userStoreOrderSharedPreferences: SharedPreferences
    private lateinit var badgeSharedPreferences: SharedPreferences
    private var contentProduct: MutableList<ProductsResponse> = mutableListOf()
    private var userStoreOrder: String? = ""
    private var orderNumber: String? = ""
    private var getOrderNumberValue: String? = ""
    private var badgeCount: Int = 0
    private var badgeSharedPreferencesValue: Int = 0
    private var userName: String? = ""
    private var uid: String? = ""
    private var name: String? = ""
    private var address: String? = ""
    private var ownerUID: String? = ""
    private var phone: String? = ""
    private var storeLatitude: Double? = 0.0
    private var storeLongitude: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_store)

        initPresenter()
        onAttachView()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()
        presenter.getUser()

        setSupportActionBar(toolbar_product)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        uid = intent.getStringExtra(Constants.UID)
        name = intent.getStringExtra(Constants.NAME)
        address = intent.getStringExtra(Constants.ADDRESS)
        ownerUID = intent.getStringExtra(Constants.OWNER_UID)
        phone = intent.getStringExtra(Constants.PHONE)
        storeLatitude = intent.getDoubleExtra(Constants.STORE_LATITUDE, 0.0)
        storeLongitude = intent.getDoubleExtra(Constants.STORE_LONGITUDE, 0.0)

        tv_store_name.text = name
        tv_store_address.text = address

        // get product content data
        presenter.getProduct(uid.toString())

        // Setup Store RecyclerView
        setupProductRecyclerView()

        // Refresh data with SwipeRefresh
        swipe_refresh_product.setOnRefreshListener {
            swipe_refresh_product.isRefreshing = false
            // refresh product content data
            presenter.getProduct(uid.toString())
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
        // create order number
        orderNumber = "md-${Random.nextInt(0, 1000).plus(69).times(5)}"

        // check value of badgeSharedPreferencesValue and badgeCount
        if (badgeSharedPreferencesValue != 0) {
            badgeCount = badgeSharedPreferencesValue
            invalidateOptionsMenu()
        }

        // check order number status if have order number in shared preferences do not create order number
        if (badgeCount == 0 && getOrderNumberValue != "") {
            with(badgeSharedPreferences.edit()) {
                putString(
                    getString(R.string.order_number_value_key),
                    Constants.DEFAULT_STRING_VALUE
                )
                commit()
            }
        } else if (getOrderNumberValue == "") {
            // create and save order number to shared preferences
            with(badgeSharedPreferences.edit()) {
                putString(
                    getString(R.string.order_number_value_key),
                    orderNumber
                )
                commit()
            }
        }

        // check user order status
        userStoreOrderSharedPreferences = this.getSharedPreferences(
            getString(R.string.user_store_order_shared_preferences_name),
            Context.MODE_PRIVATE
        ) ?: return
        userStoreOrder = userStoreOrderSharedPreferences.getString(
            getString(R.string.user_store_order_value_key),
            Constants.DEFAULT_STRING_VALUE
        )

        // check order user status on other store
        if (userStoreOrder != uid && badgeSharedPreferencesValue != 0) {
            alert(getString(R.string.alert_message_order), getString(R.string.attention)) {
                okButton { onBackPressed() }
            }.apply {
                isCancelable = false
                show()
            }
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
        tv_no_data_product.gone()
        rv_search_product.gone()
        rv_product.gone()
    }

    override fun hideLoading() {
        shimmerStop()
        tv_no_data_product.gone()
        rv_product.visible()
        rv_search_product.gone()
    }

    override fun hideSearchLoading() {
        shimmerStop()
        tv_no_data_product.gone()
        rv_product.gone()
        rv_search_product.visible()
    }

    override fun showNoDataResult() {
        shimmerStop()
        tv_no_data_product.visible()
        rv_search_product.gone()
        rv_product.gone()
    }

    override fun getProduct(productData: List<ProductsResponse>) {
        contentProduct.clear()
        contentProduct.addAll(productData)
        userStoreAdapter.notifyDataSetChanged()
    }

    override fun getUser(userData: List<UsersResponse>) {
        userData.forEach {
            userName = it.name
        }

        with(userStoreOrderSharedPreferences.edit()) {
            putString(getString(R.string.user_name_value_key), userName)
            commit()
        }
    }

    override fun returnToSignInActivity() {
        finish() // close this activity
        startActivity<SignInActivity>() // open sign in activity
    }

    private fun initPresenter() {
        presenter = UserStorePresenter()
    }

    private fun setupProductRecyclerView() {
        userStoreAdapter = UserStoreAdapter(contentProduct)

        rv_product.setHasFixedSize(true)
        rv_product.adapter = userStoreAdapter
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

    // add badge count
    override fun addProduct() {
        badgeCount++

        with(badgeSharedPreferences.edit()) {
            putInt(getString(R.string.badge_shared_preferences_value_key), badgeCount)
            commit()
        }

        invalidateOptionsMenu()
    }

    // remove badge count
    override fun removeProduct() {
        badgeCount--

        with(badgeSharedPreferences.edit()) {
            putInt(getString(R.string.badge_shared_preferences_value_key), badgeCount)
            commit()
        }

        invalidateOptionsMenu()
    }

    // device back button
    override fun onBackPressed() {
        if (badgeCount == 0) {
            with(badgeSharedPreferences.edit()) {
                putString(
                    getString(R.string.order_number_value_key),
                    Constants.DEFAULT_STRING_VALUE
                )
                commit()
            }
        }
        super.onBackPressed()
    }

    // action bar navigation up
    override fun onSupportNavigateUp(): Boolean {
        if (badgeCount == 0) {
            with(badgeSharedPreferences.edit()) {
                putString(
                    getString(R.string.order_number_value_key),
                    Constants.DEFAULT_STRING_VALUE
                )
                commit()
            }
        }
        return super.onSupportNavigateUp()
    }

    // app bar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_store, menu)
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
            R.id.menu_details_store -> {
                startActivity<UserStoreDetailsActivity>(
                    Constants.NAME to name,
                    Constants.ADDRESS to address,
                    Constants.PHONE to phone,
                    Constants.STORE_LATITUDE to storeLatitude,
                    Constants.STORE_LONGITUDE to storeLongitude
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
