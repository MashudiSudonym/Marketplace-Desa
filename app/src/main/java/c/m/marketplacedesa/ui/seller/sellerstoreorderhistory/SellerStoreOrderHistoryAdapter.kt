package c.m.marketplacedesa.ui.seller.sellerstoreorderhistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.marketplacedesa.R
import c.m.marketplacedesa.model.TemporaryOrderItemProductResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user_order.*

class SellerStoreOrderHistoryAdapter(
    private val content: List<TemporaryOrderItemProductResponse>,
    private val onClickListener: (TemporaryOrderItemProductResponse) -> Unit
) :
    RecyclerView.Adapter<SellerStoreOrderHistoryAdapter.SellerStoreOrderHistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellerStoreOrderHistoryViewHolder =
        SellerStoreOrderHistoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_order, parent, false)
        )

    override fun getItemCount(): Int = content.size

    override fun onBindViewHolder(holder: SellerStoreOrderHistoryViewHolder, position: Int) =
        holder.bind(content[position], onClickListener)

    class SellerStoreOrderHistoryViewHolder(override val containerView: View) :
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