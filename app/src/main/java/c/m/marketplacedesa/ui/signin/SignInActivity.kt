package c.m.marketplacedesa.ui.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import c.m.marketplacedesa.ui.user.main.MainActivity
import c.m.marketplacedesa.util.Constants
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setSupportActionBar(toolbar_sign_in)
        supportActionBar?.apply { title = getString(R.string.app_name) }

        btn_sign_in.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        listOf(
                            AuthUI.IdpConfig.PhoneBuilder()
                                .setDefaultCountryIso(getString(R.string.id_defaut_country))
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
                startActivity<MainActivity>()
                this.finish()
            } else {
                if (response == null) {
                    toast("Sign in cancel")
                }

                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    toast("Check your internet connection")
                }

                Log.e(
                    "Sign In Error",
                    "Sign-in error: ${response?.error?.message} || ${response?.error}"
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
            R.id.menu_setting -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
