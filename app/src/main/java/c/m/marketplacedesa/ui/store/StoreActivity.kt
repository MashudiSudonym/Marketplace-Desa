package c.m.marketplacedesa.ui.store

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import kotlinx.android.synthetic.main.activity_store.*

class StoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)
        setSupportActionBar(toolbar_product)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }
}
