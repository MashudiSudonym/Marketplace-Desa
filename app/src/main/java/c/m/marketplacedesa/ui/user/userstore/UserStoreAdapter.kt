package c.m.marketplacedesa.ui.user.userstore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.ProductsResponse
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.invisible
import c.m.marketplacedesa.util.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product_order.*
import kotlin.random.Random

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
        fun bind(contentProduct: ProductsResponse) {
            var productOrderCount = 0
            // create order number
            val orderNumber = "md-${Random.nextInt(0, 1000).plus(69).times(5)}"
            // check user order status
            val userStoreOrderSharedPreferences = itemView.context.getSharedPreferences(
                itemView.context.getString(R.string.user_store_order_shared_preferences_name),
                Context.MODE_PRIVATE
            ) ?: return
            val userStoreOrder = userStoreOrderSharedPreferences.getString(
                itemView.context.getString(R.string.user_store_order_value_key),
                Constants.DEFAULT_STRING_VALUE
            )
            // badge shopping cart
            val badgeSharedPreferences = itemView.context.getSharedPreferences(
                itemView.context.getString(R.string.order_shared_preferences_name),
                Context.MODE_PRIVATE
            ) ?: return
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

                // save order number to shared preferences
                if (getOrderNumberValue == Constants.DEFAULT_STRING_VALUE) {
                    with(badgeSharedPreferences.edit()) {
                        putString(
                            itemView.context.getString(R.string.order_number_value_key),
                            orderNumber
                        )
                        commit()
                    }
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

                // visible and invisible button button
                if (productOrderCount == 0) {
                    btn_add_to_shopping_basket_order.visible()
                    btn_plus_sign_order.invisible()
                    btn_minus_sign_order.invisible()
                    tv_order_count_order.invisible()
                }

                // show product order count
                tv_order_count_order.text = productOrderCount.toString()
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
            tv_number_of_stock_order.text = contentProduct.stock.toString()
            tv_number_of_price_order.text = contentProduct.price.toString()
        }
    }
}