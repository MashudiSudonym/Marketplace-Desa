package c.m.marketplacedesa.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import c.m.marketplacedesa.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}