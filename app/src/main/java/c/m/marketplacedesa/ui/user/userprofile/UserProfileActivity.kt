package c.m.marketplacedesa.ui.user.userprofile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.UsersResponse
import c.m.marketplacedesa.ui.seller.completesellerstoreinformation.CompleteSellerStoreInformationActivity
import c.m.marketplacedesa.ui.signin.SignInActivity
import c.m.marketplacedesa.ui.user.usereditprofile.UserEditProfileActivity
import c.m.marketplacedesa.util.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.startActivity

class UserProfileActivity : AppCompatActivity(), UserProfileView {

    private lateinit var presenter: UserProfilePresenter
    private var uid: String? = ""
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
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
    }

    @SuppressLint("SetTextI18n")
    override fun getProfile(profileData: List<UsersResponse>) {
        profileData.forEach { response ->
            uid = response.uid
            imageProfile = response.image_profile
            name = response.name
            seller = response.seller
            address = response.address
            phone = response.phone
        }

        // Do logic here
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
        } else {
            // Seller Status Label
            tv_seller_status.text =
                "${getString(R.string.seller_status_title)} ${getString(R.string.not_a_seller)}"

            // Seller Status Button
            btn_store.text = getString(R.string.become_a_seller)

            // Complete Store Information
            btn_store.setOnClickListener {
                startActivity<CompleteSellerStoreInformationActivity>(
                    Constants.UID to uid,
                    Constants.PHONE to phone
                )
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
                    Constants.UID to uid,
                    Constants.NAME to name,
                    Constants.IMG_PROFILE to imageProfile,
                    Constants.ADDRESS to address,
                    Constants.PHONE to phone,
                    Constants.SELLER_STATUS to seller
                )
                true
            }
            R.id.menu_sign_out -> {
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

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
