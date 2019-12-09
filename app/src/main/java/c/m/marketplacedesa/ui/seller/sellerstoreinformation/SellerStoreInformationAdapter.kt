package c.m.marketplacedesa.ui.seller.sellerstoreinformation

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

class SellerStoreInformationAdapter(
    private val content: List<ProductsResponse>,
    private val onClickListener: (ProductsResponse) -> Unit
) :
    RecyclerView.Adapter<SellerStoreInformationAdapter.SellerStoreInformationViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellerStoreInformationViewHolder =
        SellerStoreInformationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
        )

    override fun getItemCount(): Int = content.size

    override fun onBindViewHolder(holder: SellerStoreInformationViewHolder, position: Int) =
        holder.bind(content[position], onClickListener)

    class SellerStoreInformationViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(content: ProductsResponse, onClickListener: (ProductsResponse) -> Unit) {
            item_product_layout.setOnClickListener { onClickListener(content) }

            Glide.with(itemView.context)
                .load(content.image_product)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(img_product)

            tv_product_title.text = content.name
            tv_number_of_stock.text = content.stock.toString()
            tv_number_of_price.text = content.price.toString()
        }
    }
}