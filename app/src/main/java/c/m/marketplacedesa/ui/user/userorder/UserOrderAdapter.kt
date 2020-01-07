package c.m.marketplacedesa.ui.user.userorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user_order.*

class UserOrderAdapter(
    private val content: List<TemporaryOrderItemProductResponse>,
    private val onClickListener: (String) -> Unit
) :
    RecyclerView.Adapter<UserOrderAdapter.UserOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserOrderViewHolder =
        UserOrderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_order, parent, false)
        )

    override fun getItemCount(): Int = content.size

    override fun onBindViewHolder(holder: UserOrderViewHolder, position: Int) =
        holder.bind(content[position], onClickListener)

    class UserOrderViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(content: TemporaryOrderItemProductResponse, onClickListener: (String) -> Unit) {
            item_user_order_layout.setOnClickListener {
                onClickListener(content.order_number.toString())
            }

            tv_user_order_body.text = content.order_number.toString()
        }
    }
}