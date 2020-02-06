package c.m.lapaksembakodonorojojepara.ui.user.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c.m.lapaksembakodonorojojepara.R
import c.m.lapaksembakodonorojojepara.model.StoreResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_store.*

class MainAdapter(
    private val contentStore: List<StoreResponse>,
    private val onClickListener: (StoreResponse) -> Unit
) :
    RecyclerView.Adapter<MainAdapter.StoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder =
        StoreViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_store, parent, false)
        )

    override fun getItemCount(): Int = contentStore.size
    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) =
        holder.bind(contentStore[position], onClickListener)

    class StoreViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(contentStore: StoreResponse, onClickListener: (StoreResponse) -> Unit) {

            item_store_layout.setOnClickListener { onClickListener(contentStore) }

            Glide.with(itemView.context)
                .load(contentStore.image_profile_store)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(img_store)

            tv_store_title.text = contentStore.name
        }
    }
}