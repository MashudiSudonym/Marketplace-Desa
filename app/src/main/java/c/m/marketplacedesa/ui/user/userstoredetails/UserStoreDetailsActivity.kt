package c.m.marketplacedesa.ui.user.userstoredetails

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import c.m.marketplacedesa.R
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.displayLocationSettingRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_user_store_details.*

class UserStoreDetailsActivity : AppCompatActivity() {

    private var name: String? = ""
    private var address: String? = ""
    private var phone: String? = ""
    private var storeLatitude: Double? = 0.0
    private var storeLongitude: Double? = 0.0
    private var userLatitude: Double? = 0.0
    private var userLongitude: Double? = 0.0
    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_store_details)

        val intent = intent
        name = intent.getStringExtra(Constants.NAME)
        address = intent.getStringExtra(Constants.ADDRESS)
        phone = intent.getStringExtra(Constants.PHONE)
        storeLatitude = intent.getDoubleExtra(Constants.STORE_LATITUDE, 0.0)
        storeLongitude = intent.getDoubleExtra(Constants.STORE_LONGITUDE, 0.0)

        setSupportActionBar(toolbar_user_store_details)
        supportActionBar?.apply {
            title = name
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        tv_store_name.text = name
        tv_store_address.text = address
        tv_store_phone_number.text = phone

        // fab button open dialler application
        fab_call.setOnClickListener {
            val intentActionDial = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }

            startActivity(intentActionDial)
        }

        // maps UI initiate
        mv_user_store_details.onCreate(savedInstanceState)

        // if gps not turn on open settings location request
        displayLocationSettingRequest(this)

        // get gps hardware coordinate
        getGPSCoordinate()

        // maps UI launch
        mv_user_store_details.getMapAsync { googleMap ->
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager
                    .PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            } else {
                requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Constants.REQUEST_PERMISSION_CODE
                )
            }

            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true

            googleMap.run {
                animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(
                            LatLng(
                                storeLatitude as Double,
                                storeLongitude as Double
                            )
                        )
                            .zoom(18f).build()
                    )
                )

                addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(storeLatitude as Double, storeLongitude as Double)
                        )
                )
            }

        }
    }

    private fun getGPSCoordinate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (hasGps || hasNetwork) {
                if (hasGps) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationGps = location
                                    userLatitude = locationGps?.latitude
                                    userLongitude = locationGps?.longitude
                                }
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {
                            }

                            override fun onProviderEnabled(provider: String?) {}

                            override fun onProviderDisabled(provider: String?) {}
                        })

                    val localGpsLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (localGpsLocation != null) locationGps = localGpsLocation
                } else {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                if (hasNetwork) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0f, object : LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationNetwork = location
                                    userLatitude = locationNetwork?.latitude
                                    userLongitude = locationNetwork?.longitude
                                }
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {
                            }

                            override fun onProviderEnabled(provider: String?) {}

                            override fun onProviderDisabled(provider: String?) {}
                        })

                    val localNetworkLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (localNetworkLocation != null) locationNetwork = localNetworkLocation
                } else {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

                if (locationGps != null && locationNetwork != null) {
                    if (locationGps?.accuracy as Float > locationNetwork?.accuracy as Float) {
                        userLatitude = locationNetwork?.latitude
                        userLongitude = locationNetwork?.longitude
                    } else {
                        userLatitude = locationNetwork?.latitude
                        userLongitude = locationNetwork?.longitude
                    }
                }
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 101)
        }
    }

    @Suppress("SameParameterValue")
    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        mv_user_store_details.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mv_user_store_details.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mv_user_store_details.onDestroy()
    }
}
