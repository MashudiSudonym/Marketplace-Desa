package c.m.lapaksembakodonorojo.ui.notification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.lapaksembakodonorojo.R
import c.m.lapaksembakodonorojo.model.NotificationCollectionResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_notification.*

class NotificationAdapter(
    private val content: List<NotificationCollectionResponse>,
    private val onClickListener: (NotificationCollectionResponse) -> Unit
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
        )

    override fun getItemCount(): Int = content.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) =
        holder.bind(content[position], onClickListener)

    class NotificationViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(
            content: NotificationCollectionResponse,
            onClickListener: (NotificationCollectionResponse) -> Unit
        ) {
            item_notification_layout.setOnClickListener { onClickListener(content) }

            tv_notification_title.text = content.order_title_message
            tv_notification_body.text = content.order_body_message

            // set background color if notification not read
            when (content.read_notification) {
                false -> item_notification_layout.setBackgroundColor(Color.parseColor("#9543A047"))
                true -> item_notification_layout.setBackgroundColor(Color.WHITE)
            }
        }
    }
}