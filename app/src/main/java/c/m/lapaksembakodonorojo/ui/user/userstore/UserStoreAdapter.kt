package c.m.lapaksembakodonorojo.ui.user.userstore

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.lapaksembakodonorojo.R
import c.m.lapaksembakodonorojo.model.ProductsResponse
import c.m.lapaksembakodonorojo.model.StoreResponse
import c.m.lapaksembakodonorojo.util.Constants
import c.m.lapaksembakodonorojo.util.invisible
import c.m.lapaksembakodonorojo.util.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product_order.*
import org.jetbrains.anko.design.snackbar

class UserStoreAdapter(
    private val contentProduct: List<ProductsResponse>
) :
    RecyclerView.Adapter<UserStoreAdapter.UserStoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserStoreViewHolder =
        UserStoreViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_order, parent, false)
        )

    override fun getItemCount(): Int = contentProduct.size

    override fun onBindViewHolder(holder: UserStoreViewHolder, position: Int) =
        holder.bind(contentProduct[position])

    class UserStoreViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        @SuppressLint("RestrictedApi")
        fun bind(contentProduct: ProductsResponse) {
            var productOrderCount = 0
            // firebase initialize
            val db = FirebaseFirestore.getInstance()
            val temporaryOrderItemProductKey =
                db.collection("temporary_order_item_product").document().id
            // save temporaryOrderItemProductKey to SharedPreferences
            val firebaseSharedPreferences = itemView.context.getSharedPreferences(
                itemView.context.getString(R.string.firebase_shared_preferences),
                Context.MODE_PRIVATE
            ) ?: return
            // get firebase temporary order key
            var firebaseTemporaryOrderItemProductKey: String?
            // get owner uid
            var storeOwnerUID = ""
            FirebaseFirestore.getInstance().collection("store")
                .whereEqualTo("uid", contentProduct.store)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) Log.e(Constants.ERROR_TAG, "$exception")

                    val storeData = snapshot?.toObjects(StoreResponse::class.java)

                    storeData?.forEach { response ->
                        storeOwnerUID = response.owner.toString()
                    }
                }
            // user uid
            val userUID = FirebaseAuth.getInstance().currentUser?.uid
            // check user order status
            val userStoreOrderSharedPreferences = itemView.context.getSharedPreferences(
                itemView.context.getString(R.string.user_store_order_shared_preferences_name),
                Context.MODE_PRIVATE
            ) ?: return
            // get user store order
            val userStoreOrder = userStoreOrderSharedPreferences.getString(
                itemView.context.getString(R.string.user_store_order_value_key),
                Constants.DEFAULT_STRING_VALUE
            )
            // get user name
            val userName = userStoreOrderSharedPreferences.getString(
                itemView.context.getString(R.string.user_name_value_key),
                Constants.DEFAULT_STRING_VALUE
            )
            // badge shopping cart
            val badgeSharedPreferences = itemView.context.getSharedPreferences(
                itemView.context.getString(R.string.order_shared_preferences_name),
                Context.MODE_PRIVATE
            ) ?: return
            // get product order value
            val getProductOrderCountValue = badgeSharedPreferences.getInt(
                "${contentProduct.uid}",
                Constants.DEFAULT_INT_VALUE
            )
            // get order number
            val getOrderNumberValue = badgeSharedPreferences.getString(
                itemView.context.getString(R.string.order_number_value_key),
                Constants.DEFAULT_STRING_VALUE
            )

            // set default value productOrderCount and check store product position
            if (getProductOrderCountValue != 0 && userStoreOrder == contentProduct.store) {
                productOrderCount = getProductOrderCountValue

                // visible and invisible button
                btn_add_to_shopping_basket_order.invisible()
                btn_plus_sign_order.visible()
                btn_minus_sign_order.visible()
                tv_order_count_order.visible()

                // show product order count
                tv_order_count_order.text = productOrderCount.toString()
            }

            // add and remove shopping cart button
            btn_add_to_shopping_basket_order.setOnClickListener {
                productOrderCount++

                if (itemView.context is UserStoreActivity) {
                    (itemView.context as UserStoreAddOrRemoveInterface).addProduct()
                }

                // add order value by product name
                with(badgeSharedPreferences.edit()) {
                    putInt("${contentProduct.uid}", productOrderCount)
                    commit()
                }

                // visible and invisible button button
                btn_add_to_shopping_basket_order.invisible()
                btn_plus_sign_order.visible()
                btn_minus_sign_order.visible()
                tv_order_count_order.visible()

                // show product order count
                tv_order_count_order.text = productOrderCount.toString()

                // add store uid for checker user order on this store
                with(userStoreOrderSharedPreferences.edit()) {
                    putString(
                        itemView.context.getString(R.string.user_store_order_value_key),
                        contentProduct.store
                    )
                    commit()
                }

                // save temporary order key
                with(firebaseSharedPreferences.edit()) {
                    putString(
                        "${contentProduct.uid}",
                        temporaryOrderItemProductKey
                    )
                    commit()
                }

                // show notification added product
                itemView.snackbar("Add ${contentProduct.name} to shopping cart")

                // firebase data map
                val temporaryOrderItemProduct = mapOf(
                    "uid" to temporaryOrderItemProductKey,
                    "image_product" to contentProduct.image_product,
                    "name" to contentProduct.name,
                    "number_of_product_orders" to productOrderCount,
                    "total_price" to contentProduct.price?.times(productOrderCount),
                    "order_number" to getOrderNumberValue,
                    "order_status" to 0,
                    "payment_status" to false,
                    "is_canceled" to false,
                    "order_by" to userName,
                    "store_owner_uid" to storeOwnerUID,
                    "delivery_option" to 1,
                    "store_uid" to contentProduct.store,
                    "user_order_uid" to userUID
                )
                val orderByOrderNumber = mapOf(
                    temporaryOrderItemProductKey to true
                )

                // send data to firebase
                if (FirebaseAuth.getInstance().currentUser != null) {
                    db.collection("temporary_order_item_product")
                        .document(temporaryOrderItemProductKey)
                        .set(temporaryOrderItemProduct)
                        .addOnSuccessListener { Log.d(Constants.DEBUG_TAG, "Success add data") }
                        .addOnFailureListener { e -> Log.e("ERROR!!", "$e") }

                    db.collection("order_by_order_number")
                        .document(getOrderNumberValue.toString())
                        .set(orderByOrderNumber, SetOptions.merge())
                        .addOnSuccessListener { Log.d(Constants.DEBUG_TAG, "Success update data") }
                        .addOnFailureListener { e ->
                            Log.e("ERROR!!", "$e")
                        }

                    Log.d(Constants.DEBUG_TAG, temporaryOrderItemProductKey)
                }
            }

            btn_plus_sign_order.setOnClickListener {
                productOrderCount++

                if (itemView.context is UserStoreActivity) {
                    (itemView.context as UserStoreAddOrRemoveInterface).addProduct()
                }

                // add order value by product name
                with(badgeSharedPreferences.edit()) {
                    putInt("${contentProduct.uid}", productOrderCount)
                    commit()
                }

                // show product order count
                tv_order_count_order.text = productOrderCount.toString()

                // get firebase temporary order key
                firebaseTemporaryOrderItemProductKey = firebaseSharedPreferences.getString(
                    "${contentProduct.uid}",
                    Constants.DEFAULT_STRING_VALUE
                )

                // firebase data map
                val temporaryOrderItemProduct = mapOf(
                    "number_of_product_orders" to productOrderCount,
                    "total_price" to contentProduct.price?.times(productOrderCount)
                )

                if (FirebaseAuth.getInstance().currentUser != null) {
                    // send data to firebase
                    db.collection("temporary_order_item_product")
                        .document(firebaseTemporaryOrderItemProductKey.toString())
                        .update(temporaryOrderItemProduct)
                        .addOnSuccessListener { Log.d(Constants.DEBUG_TAG, "Success update data") }
                        .addOnFailureListener { e -> Log.e("ERROR!!", "$e") }

                    Log.d(Constants.DEBUG_TAG, firebaseTemporaryOrderItemProductKey.toString())
                }
            }

            btn_minus_sign_order.setOnClickListener {
                productOrderCount--

                if (itemView.context is UserStoreActivity) {
                    (itemView.context as UserStoreAddOrRemoveInterface).removeProduct()
                }

                // add order value by product name
                with(badgeSharedPreferences.edit()) {
                    putInt("${contentProduct.uid}", productOrderCount)
                    commit()
                }

                // get firebase temporary order key
                firebaseTemporaryOrderItemProductKey = firebaseSharedPreferences.getString(
                    "${contentProduct.uid}",
                    Constants.DEFAULT_STRING_VALUE
                )

                // firebase data map
                val temporaryOrderItemProduct = mapOf(
                    "number_of_product_orders" to productOrderCount,
                    "total_price" to contentProduct.price?.times(productOrderCount)
                )

                if (FirebaseAuth.getInstance().currentUser != null) {
                    // send data to firebase
                    db.collection("temporary_order_item_product")
                        .document(firebaseTemporaryOrderItemProductKey.toString())
                        .update(temporaryOrderItemProduct)
                        .addOnSuccessListener { Log.d(Constants.DEBUG_TAG, "Success update data") }
                        .addOnFailureListener { e -> Log.e("ERROR!!", "$e") }

                    Log.d(Constants.DEBUG_TAG, firebaseTemporaryOrderItemProductKey.toString())

                    // visible and invisible button button
                    if (productOrderCount == 0) {
                        btn_add_to_shopping_basket_order.visible()
                        btn_plus_sign_order.invisible()
                        btn_minus_sign_order.invisible()
                        tv_order_count_order.invisible()
                        itemView.snackbar("Remove ${contentProduct.name} to shopping cart")

                        // send data to firebase
                        db.collection("temporary_order_item_product")
                            .document(firebaseTemporaryOrderItemProductKey.toString())
                            .delete()

                        db.collection("order_by_order_number")
                            .document(getOrderNumberValue.toString())
                            .delete()
                    }
                }
                // show product order count
                tv_order_count_order.text = productOrderCount.toString()
            }

            // if item stock is empty or limit, disable button add to shopping cart
            if (contentProduct.stock == false) {
                btn_add_to_shopping_basket_order.isEnabled = false
                btn_add_to_shopping_basket_order.setBackgroundColor(Color.LTGRAY)
                btn_plus_sign_order.isEnabled = false
                btn_plus_sign_order.setBackgroundColor(Color.LTGRAY)
                tv_number_of_stock_order.text =
                    itemView.context.getString(R.string.not_available_stock_status)
            } else {
                btn_add_to_shopping_basket_order.isEnabled = true
                btn_add_to_shopping_basket_order.setBackgroundColor(Color.parseColor("#43A047"))
                btn_plus_sign_order.isEnabled = true
                btn_plus_sign_order.setBackgroundColor(Color.parseColor("#43A047"))
                tv_number_of_stock_order.text =
                    itemView.context.getString(R.string.available_stock_status)
            }

            // Layout show data
            Glide.with(itemView.context)
                .load(contentProduct.image_product)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(img_product_order)

            tv_product_title_order.text = contentProduct.name
            tv_number_of_price_order.text = contentProduct.price.toString()
        }
    }
}