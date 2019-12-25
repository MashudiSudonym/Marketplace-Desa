package c.m.marketplacedesa.ui.signin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.ui.settings.SettingsActivity
import c.m.marketplacedesa.ui.user.main.MainActivity
import c.m.marketplacedesa.util.Constants
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.github.babedev.dexter.dsl.runtimePermission
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setSupportActionBar(toolbar_sign_in)
        supportActionBar?.apply { title = getString(R.string.app_name) }

        // validate location permission
        runtimePermission {
            permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) {
                checked { }
            }
        }

        // sign in button
        btn_sign_in.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        listOf(
                            AuthUI.IdpConfig.PhoneBuilder()
                                .setDefaultCountryIso(getString(R.string.id_default_country))
                                .build()
                        )
                    ).build(),
                Constants.REQUEST_SIGN_IN_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_SIGN_IN_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // open main activity and finish this activity
                startActivity<MainActivity>()
                finish()
            } else {
                if (response == null) {
                    toast(getString(R.string.sign_in_cancel_alert))
                }

                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    toast(getString(R.string.check_internet_connection_alert))
                }

                Log.e(
                    "Sign In Error",
                    "Sign In error: ${response?.error?.message} || ${response?.error}"
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_sign_in, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_setting -> {
                startActivity<SettingsActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
