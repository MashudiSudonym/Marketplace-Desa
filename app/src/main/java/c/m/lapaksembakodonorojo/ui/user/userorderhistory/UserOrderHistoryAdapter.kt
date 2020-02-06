package c.m.lapaksembakodonorojo.ui.user.userorderhistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.lapaksembakodonorojo.R
import c.m.lapaksembakodonorojo.model.TemporaryOrderItemProductResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user_order.*

class UserOrderHistoryAdapter(
    private val content: List<TemporaryOrderItemProductResponse>,
    private val onClickListener: (TemporaryOrderItemProductResponse) -> Unit
) :
    RecyclerView.Adapter<UserOrderHistoryAdapter.UserOrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserOrderHistoryViewHolder =
        UserOrderHistoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_order, parent, false)
        )

    override fun getItemCount(): Int = content.size

    override fun onBindViewHolder(holder: UserOrderHistoryViewHolder, position: Int) =
        holder.bind(content[position], onClickListener)

    class UserOrderHistoryViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(
            content: TemporaryOrderItemProductResponse,
            onClickListener: (TemporaryOrderItemProductResponse) -> Unit
        ) {
            item_user_order_layout.setOnClickListener {
                onClickListener(content)
            }

            tv_user_order_body.text = content.order_number.toString()
        }
    }
}