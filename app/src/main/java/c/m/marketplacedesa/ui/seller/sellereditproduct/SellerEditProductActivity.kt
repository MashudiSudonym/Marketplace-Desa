@file:Suppress("DEPRECATION")

package c.m.marketplacedesa.ui.seller.sellereditproduct

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
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import c.m.marketplacedesa.R
import c.m.marketplacedesa.ui.seller.sellerstoreinformation.SellerStoreInformationActivity
import c.m.marketplacedesa.util.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.babedev.dexter.dsl.runtimePermission
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import kotlinx.android.synthetic.main.activity_seller_edit_product.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.random.Random

class SellerEditProductActivity : AppCompatActivity(), SellerEditProductView {

    private lateinit var presenter: SellerEditProductPresenter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var radioButtonStockStatus: RadioButton
    private var selectedStockOption: Int? = 0
    private var productUID: String? = ""
    private var storeUID: String? = ""
    private var productName: String? = ""
    private var productPrice: Int? = 0
    private var productStock: Boolean = false
    private var productImage: String? = ""
    private var filePath: Uri? = null
    private var imageFilePath: String? = ""
    private var randomNumber: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_edit_product)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = SellerEditProductPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.initFirebase()

        // validate device permission
        validatePermission()

        // generate random number for product
        randomNumber = Random.nextInt(0, 1000).plus(69).times(5).toString()

        setSupportActionBar(toolbar_seller_edit_product)
        supportActionBar?.apply {
            title = getString(R.string.edit_product_information)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val intent = intent
        productUID = intent.getStringExtra(Constants.UID)
        storeUID = intent.getStringExtra(Constants.STORE_UID)
        productName = intent.getStringExtra(Constants.NAME)
        productPrice = intent.getIntExtra(Constants.PRICE, 0)
        productStock = intent.getBooleanExtra(Constants.STOCK, false)
        productImage = intent.getStringExtra(Constants.IMG_PRODUCT)

        // Set last image profile
        Glide.with(this)
            .load(productImage)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(img_seller_edit_product)

        // Set name, price, stock field
        edt_name_seller_edit_product.setText(productName)
        edt_price_seller_edit_product.setText(productPrice.toString())
        when (productStock) {
            true -> radio_group_stock_seller_edit_product.check(R.id.radio_button_available_seller_edit_product)
            false -> radio_group_stock_seller_edit_product.check(R.id.radio_button_not_available_seller_edit_product)
        }

        // Select image from camera or gallery
        btn_choose_image_seller_edit_product.setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        onDetachView()
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
        val imageViewWidth = img_seller_edit_product.width
        val imageViewHeight = img_seller_edit_product.height

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
                        .into(img_seller_edit_product)
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
                        .into(img_seller_edit_product)
                }
            }
            else -> toast("Unrecognized request code")
        }
    }

    private fun saveUpdateProductData() {
        val productNameField = findViewById<EditText>(R.id.edt_name_seller_edit_product)
        val productPriceField = findViewById<EditText>(R.id.edt_price_seller_edit_product)

        // check radio button selected position
        radioButtonStockStatus =
            findViewById(radio_group_stock_seller_edit_product.checkedRadioButtonId)

        if (selectedStockOption != -1) {
            // check radio button value of select
            when (radioButtonStockStatus.text) {
                getString(R.string.available_stock_status) -> {
                    productStock = true
                }
                getString(R.string.not_available_stock_status) -> {
                    productStock = false
                }
            }
        }

        val isValid = productNameField.nonEmpty() && productPriceField.nonEmpty()
        val productName = edt_name_seller_edit_product.text.toString()
        val productPrice = edt_price_seller_edit_product.text.toString()

        if (isValid) {
            // upload store information data
            presenter.updateProductInformationData(
                productUID.toString(),
                productName,
                productPrice.toInt(),
                productStock,
                storeUID.toString(),
                filePath,
                randomNumber.toString(),
                storeUID.toString()
            )
        } else {
            // if not valid
            productNameField.error = getString(R.string.name_field_error)
            productPriceField.error = getString(R.string.address_field_error)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    // app bar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_seller_add_edit_product, menu)
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
                        saveUpdateProductData()
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
