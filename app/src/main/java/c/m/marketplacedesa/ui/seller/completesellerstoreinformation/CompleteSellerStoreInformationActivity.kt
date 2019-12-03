package c.m.marketplacedesa.ui.seller.completesellerstoreinformation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import c.m.marketplacedesa.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_complete_seller_store_information.*
import org.jetbrains.anko.toast

class CompleteSellerStoreInformationActivity : AppCompatActivity() {

    private var markerArrayList: ArrayList<Marker> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_seller_store_information)

        // maps UI initiate
        mv_store_location.onCreate(savedInstanceState)

        setSupportActionBar(toolbar_complete_seller_store_information)
        supportActionBar?.apply {
            title = getString(R.string.complete_store_information)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        mv_store_location.getMapAsync { googleMap ->
            // setup maps type
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Control settings
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true

            googleMap.setOnMapClickListener { latLng ->
                if (markerArrayList.size > 0) {
                    val markerToRemove = markerArrayList[0]

                    // remove marker from list
                    markerArrayList.remove(markerToRemove)

                    // remove marker from the map
                    markerToRemove.remove()
                }

                // add marker to clicked point
                val markerOptions = MarkerOptions().position(latLng).draggable(true)
                val currentMarker = googleMap.addMarker(markerOptions)

                // add current marker to array list
                markerArrayList.add(currentMarker)

                toast("$latLng")
            }

            if (markerArrayList.isNullOrEmpty()) {
                googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(
                            LatLng(-6.616408, 110.693157)
                        ).zoom(16f).build()
                    )
                )
            } else {
                googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(
                            LatLng(
                                markerArrayList[0].position.latitude,
                                markerArrayList[0].position.longitude
                            )
                        ).zoom(16f).build()
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mv_store_location.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mv_store_location.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mv_store_location.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
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
