package c.m.marketplacedesa.ui.user.userorderdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_cart_order_list.*

class UserOrderDetailsAdapter(
    private val content: List<TemporaryOrderItemProductResponse>
) :
    RecyclerView.Adapter<UserOrderDetailsAdapter.UserOrderDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserOrderDetailsViewHolder =
        UserOrderDetailsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart_order_list, parent, false)
        )

    override fun getItemCount(): Int = content.size

    override fun onBindViewHolder(holder: UserOrderDetailsViewHolder, position: Int) =
        holder.bind(content[position])

    class UserOrderDetailsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(
            content: TemporaryOrderItemProductResponse
        ) {
            Glide.with(itemView.context)
                .load(content.image_product)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(img_product)

            tv_number_of_order_quantity.text = content.number_of_product_orders.toString()
            tv_number_of_total_price.text = content.total_price.toString()
            tv_product_title.text = content.name
        }
    }
}