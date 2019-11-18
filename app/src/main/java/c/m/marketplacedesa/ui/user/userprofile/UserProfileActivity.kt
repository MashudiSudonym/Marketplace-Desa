package c.m.marketplacedesa.ui.user.userprofile

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.ui.user.usereditprofile.UserEditProfileActivity
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.startActivity

class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(toolbar_profile)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit_profile -> {
                startActivity<UserEditProfileActivity>()
                true
            }
            R.id.menu_sign_out -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
