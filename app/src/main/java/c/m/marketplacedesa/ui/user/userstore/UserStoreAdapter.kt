package c.m.marketplacedesa.ui.user.userstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.ProductsResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.*

class UserStoreAdapter(
    private val contentProduct: List<ProductsResponse>,
    private val onClickListener: (ProductsResponse) -> Unit
) :
    RecyclerView.Adapter<UserStoreAdapter.UserStoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserStoreViewHolder =
        UserStoreViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
        )

    override fun getItemCount(): Int = contentProduct.size

    override fun onBindViewHolder(holder: UserStoreViewHolder, position: Int) =
        holder.bind(contentProduct[position], onClickListener)

    class UserStoreViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(contentProduct: ProductsResponse, onClickListener: (ProductsResponse) -> Unit) {

            item_product_layout.setOnClickListener { onClickListener(contentProduct) }

            Glide.with(itemView.context)
                .load(contentProduct.image_product)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(img_product)

            tv_product_title.text = contentProduct.name
            tv_number_of_stock.text = contentProduct.stock.toString()
            tv_number_of_price.text = contentProduct.price.toString()
        }
    }
}