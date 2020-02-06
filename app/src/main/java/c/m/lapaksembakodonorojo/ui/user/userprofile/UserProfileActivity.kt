package c.m.lapaksembakodonorojo.ui.user.userprofile

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.lapaksembakodonorojo.R
import c.m.lapaksembakodonorojo.model.UsersResponse
import c.m.lapaksembakodonorojo.ui.seller.completesellerstoreinformation.CompleteSellerStoreInformationActivity
import c.m.lapaksembakodonorojo.ui.seller.sellerstoreinformation.SellerStoreInformationActivity
import c.m.lapaksembakodonorojo.ui.signin.SignInActivity
import c.m.lapaksembakodonorojo.ui.user.usereditprofile.UserEditProfileActivity
import c.m.lapaksembakodonorojo.util.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity

class UserProfileActivity : AppCompatActivity(), UserProfileView {

    private lateinit var presenter: UserProfilePresenter
    private lateinit var userStoreOrderSharedPreferences: SharedPreferences
    private lateinit var badgeSharedPreferences: SharedPreferences
    private var userStoreOrder: String? = ""
    private var badgeSharedPreferencesValue: Int = 0
    private var userUID: String? = ""
    private var imageProfile: String? = ""
    private var name: String? = ""
    private var seller: Boolean = false
    private var address: String? = ""
    private var phone: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(toolbar_profile)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = UserProfilePresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()
        presenter.getProfile() // run get profile content data

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // Refresh data with SwipeRefresh
        swipe_refresh_profile.setOnRefreshListener {
            swipe_refresh_profile.isRefreshing = false
            // refresh profile content data
            presenter.getProfile()
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

        // check user order status
        userStoreOrderSharedPreferences = this.getSharedPreferences(
            getString(R.string.user_store_order_shared_preferences_name),
            Context.MODE_PRIVATE
        ) ?: return
        userStoreOrder = userStoreOrderSharedPreferences.getString(
            getString(R.string.user_store_order_value_key),
            Constants.DEFAULT_STRING_VALUE
        )
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onResume() {
        super.onResume()
        presenter.getProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
    }

    @SuppressLint("SetTextI18n")
    override fun getProfile(profileData: List<UsersResponse>) {
        profileData.forEach { response ->
            userUID = response.uid
            imageProfile = response.image_profile
            name = response.name
            seller = response.seller
            address = response.address
            phone = response.phone
        }

        Glide.with(this)
            .load(imageProfile)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(img_profile)

        tv_name.text = name
        tv_address.text = address
        tv_phone_number.text = phone

        // Seller Status
        if (seller) {
            // Seller Status Label
            tv_seller_status.text =
                "${getString(R.string.seller_status_title)} ${getString(R.string.seller)}"

            // Seller Status Button
            btn_store.text = getString(R.string.your_store_page)

            // Store Information Activity
            btn_store.setOnClickListener {
                startActivity<SellerStoreInformationActivity>(
                    Constants.OWNER_UID to userUID
                )
            }
        } else {
            // Seller Status Label
            tv_seller_status.text =
                "${getString(R.string.seller_status_title)} ${getString(R.string.not_a_seller)}"

            // Seller Status Button
            btn_store.text = getString(R.string.become_a_seller)

            // Complete Store Information
            btn_store.setOnClickListener {
                startActivity<CompleteSellerStoreInformationActivity>()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit_profile -> {
                startActivity<UserEditProfileActivity>(
                    Constants.UID to userUID,
                    Constants.NAME to name,
                    Constants.IMG_PROFILE to imageProfile,
                    Constants.ADDRESS to address,
                    Constants.PHONE to phone,
                    Constants.SELLER_STATUS to seller
                )
                true
            }
            R.id.menu_sign_out -> {
                // check user order not finish
                if (userStoreOrder != userUID && badgeSharedPreferencesValue != 0) {
                    alert(getString(R.string.alert_message_order), getString(R.string.attention)) {
                        okButton {
                            onBackPressed()
                        }
                    }.apply {
                        isCancelable = false
                        show()
                    }
                } else {
                    // remove fcm token from users database
                    presenter.removeFCMToken()

                    // user Sign Out
                    AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Finish this activity
                                finish()

                                // return to sign in activity
                                startActivity<SignInActivity>()
                            }
                        }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
