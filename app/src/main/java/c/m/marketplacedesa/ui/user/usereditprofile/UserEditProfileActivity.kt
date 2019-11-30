package c.m.marketplacedesa.ui.user.usereditprofile

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.util.Constants
import kotlinx.android.synthetic.main.activity_user_edit_profile.*

class UserEditProfileActivity : AppCompatActivity() {

    private var userUID: String? = ""
    private var name: String? = ""
    private var imageProfile: String? = ""
    private var address: String? = ""
    private var phone: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit_profile)
        setSupportActionBar(toolbar_user_edit_profile)
        supportActionBar?.apply {
            title = getString(R.string.edit_profile)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val intent = intent
        userUID = intent.getStringExtra(Constants.UID)
        name = intent.getStringExtra(Constants.NAME)
        imageProfile = intent.getStringExtra(Constants.IMG_PROFILE)
        address = intent.getStringExtra(Constants.ADDRESS)
        phone = intent.getStringExtra(Constants.PHONE)


    }

    // app bar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_edit_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
