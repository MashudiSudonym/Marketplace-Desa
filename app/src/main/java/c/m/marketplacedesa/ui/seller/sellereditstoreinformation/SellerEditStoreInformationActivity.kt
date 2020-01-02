@file:Suppress("DEPRECATION")

package c.m.marketplacedesa.ui.seller.sellereditstoreinformation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import c.m.marketplacedesa.R
import c.m.marketplacedesa.ui.seller.sellerstoreinformation.SellerStoreInformationActivity
import c.m.marketplacedesa.util.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.babedev.dexter.dsl.runtimePermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import kotlinx.android.synthetic.main.activity_seller_edit_store_information.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class SellerEditStoreInformationActivity : AppCompatActivity(), SellerEditStoreInformationView {

    private lateinit var presenter: SellerEditStoreInformationPresenter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var geoPointStore: GeoPoint
    private var filePath: Uri? = null
    private var imageFilePath: String? = ""
    private var markerArrayList: ArrayList<Marker> = arrayListOf()
    private var storeUID: String? = ""
    private var storeName: String? = ""
    private var storeAddress: String? = ""
    private var storeImage: String? = ""
    private var storePhone: String? = ""
    private var storeLatitude: Double? = 0.0
    private var storeLongitude: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_edit_store_information)

        initPresenter()
        onAttachView()

        // maps UI initiate
        mv_edit_store_location.onCreate(savedInstanceState)
    }

    private fun initPresenter() {
        presenter = SellerEditStoreInformationPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        // request permission
        validatePermission()

        setSupportActionBar(toolbar_edit_seller_store_information)
        supportActionBar?.apply {
            title = getString(R.string.edit_store_information)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val intent = intent
        storeUID = intent.getStringExtra(Constants.STORE_UID)
        storeName = intent.getStringExtra(Constants.NAME)
        storeAddress = intent.getStringExtra(Constants.ADDRESS)
        storeImage = intent.getStringExtra(Constants.IMG_PROFILE)
        storePhone = intent.getStringExtra(Constants.PHONE)
        storeLatitude = intent.getDoubleExtra(Constants.STORE_LATITUDE, 0.0)
        storeLongitude = intent.getDoubleExtra(Constants.STORE_LONGITUDE, 0.0)

        // Set last image profile
        Glide.with(this)
            .load(storeImage)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(img_edit_seller_store_information)

        // Set value edit text
        edt_name_edit_seller_store_information.setText(storeName)
        edt_address_edit_seller_store_information.setText(storeAddress)
        edt_phone_edit_seller_store_information.setText(storePhone)
        geoPointStore = GeoPoint(storeLatitude as Double, storeLongitude as Double)

        // Maps for select store location
        mv_edit_store_location.getMapAsync { googleMap ->
            // setup maps type
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Control settings
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true

            googleMap.setOnMapClickListener { latLng ->
                // remove old location
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

                // set geo point store
                geoPointStore = GeoPoint(latLng.latitude, latLng.longitude)
            }

            if (markerArrayList.isNullOrEmpty()) {
                googleMap.run {
                    animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder().target(
                                LatLng(
                                    storeLatitude as Double,
                                    storeLongitude as Double
                                )
                            )
                                .zoom(16f).build()
                        )
                    )

                    // add marker to clicked point
                    val markerOptions = MarkerOptions().position(
                        LatLng(
                            storeLatitude as Double,
                            storeLongitude as Double
                        )
                    ).draggable(true)
                    val currentMarker = googleMap.addMarker(markerOptions)

                    // add current marker to array list
                    markerArrayList.add(currentMarker)
                }
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

        // Select image from camera or gallery
        btn_choose_image_edit_seller_store_information.setOnClickListener {
            alert(getString(R.string.take_or_choose)) {
                positiveButton(getString(R.string.choose_from_gallery)) {
                    // call File Manager or Gallery Internal / External Storage
                    GlobalScope.launch(Dispatchers.IO) {
                        showFileChooser()
                    }
                }
                negativeButton(getString(R.string.take_from_camera)) {
                    // call camera intent
                    GlobalScope.launch(Dispatchers.IO) {
                        takePhoto()
                    }
                }
            }.show()
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onResume() {
        super.onResume()
        mv_edit_store_location.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mv_edit_store_location.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
        mv_edit_store_location.onDestroy()
    }

    override fun showProgressDialog() {
        progressDialog = progressDialog(title = getString(R.string.upload_data_title))
        progressDialog.apply {
            setCancelable(false)
            show()
        }
    }

    override fun closeProgressDialog() {
        progressDialog.dismiss()
    }

    override fun progressDialogMessage(message: String) {
        progressDialog.setMessage(message)
    }

    override fun finishThisActivityToNextActivity(storeUID: String) {
        finish() // finish this activity
        startActivity<SellerStoreInformationActivity>(
            Constants.STORE_UID to storeUID
        )
    }

    private fun validatePermission() {
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
    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, Constants.PICK_PHOTO_CODE)
        }
    }

    private fun takePhoto() {
        try {
            // Temporary for preview image/bitmap not save to local storage (internal / external)
            val imageFile = createImageFile()
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (callCameraIntent.resolveActivity(packageManager) != null) {
                val authorities = "$packageName.fileprovider"

                filePath = FileProvider.getUriForFile(
                    this,
                    authorities, imageFile
                )
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
                startActivityForResult(callCameraIntent, Constants.CAMERA_REQUEST_CODE)
            }
        } catch (e: IOException) {
            toast("Could not create file")
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

        if (storageDir?.exists() == false) storageDir.mkdirs()
        imageFilePath = imageFile.absolutePath

        return imageFile
    }

    private fun setScaledBitmap(): Bitmap {
        val imageViewWidth = img_edit_seller_store_information.width
        val imageViewHeight = img_edit_seller_store_information.height

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFilePath, bmOptions)
        val bitmapWidth = bmOptions.outWidth
        val bitmapHeight = bmOptions.outHeight

        val scaleFactor = min(bitmapWidth / imageViewWidth, bitmapHeight / imageViewHeight)

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeFile(imageFilePath, bmOptions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Glide.with(this)
                        .load(setScaledBitmap())
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                        )
                        .into(img_edit_seller_store_information)
                }
            }
            Constants.PICK_PHOTO_CODE -> {
                if (data != null) {
                    filePath = data.data

                    val selectedImage =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)

                    Glide.with(this)
                        .load(selectedImage)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                        )
                        .into(img_edit_seller_store_information)
                }
            }
            else -> toast("Unrecognized request code")
        }
    }

    private fun saveStoreUpdateInformationData() {
        val nameStoreField = findViewById<EditText>(R.id.edt_name_edit_seller_store_information)
        val addressStoreField =
            findViewById<EditText>(R.id.edt_address_edit_seller_store_information)
        val phoneStoreField = findViewById<EditText>(R.id.edt_phone_edit_seller_store_information)
        val nameStore = edt_name_edit_seller_store_information.text.toString()
        val addressStore = edt_address_edit_seller_store_information.text.toString()
        val phoneStore = edt_phone_edit_seller_store_information.text.toString()
        val isValid =
            nameStoreField.nonEmpty() && addressStoreField.nonEmpty() && phoneStoreField.nonEmpty()

        if (isValid && (markerArrayList.size > 0)) {
            presenter.storeUpdateInformationData(
                storeUID.toString(),
                nameStore,
                addressStore,
                phoneStore,
                geoPointStore,
                filePath
            )
        } else {
            // if not valid
            nameStoreField.error = getString(R.string.name_field_error)
            addressStoreField.error = getString(R.string.address_field_error)
            phoneStoreField.error = getString(R.string.phone_field_error)

            // show alert to select store location
            if (markerArrayList.size == 0) {
                alert(
                    getString(R.string.select_store_location_alert),
                    getString(R.string.title_select_store_location_alert)
                ) {
                    yesButton { }
                }.apply {
                    isCancelable = false
                    show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    // app bar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_seller_edit_store, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                alert(
                    getString(R.string.message_check_your_data),
                    getString(R.string.title_check_your_data)
                ) {
                    yesButton {
                        saveStoreUpdateInformationData()
                    }
                    noButton { }
                }.apply {
                    isCancelable = false
                    show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
