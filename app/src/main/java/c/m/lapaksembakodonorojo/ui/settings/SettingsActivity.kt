package c.m.lapaksembakodonorojo.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.lapaksembakodonorojo.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        setSupportActionBar(toolbar_setting)
        supportActionBar?.apply {
            title = getString(R.string.title_activity_settings)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }
}