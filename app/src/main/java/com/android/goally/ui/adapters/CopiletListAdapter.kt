package com.android.goally.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.goally.R
import com.android.goally.data.model.api.response.copilet.Activities
import com.android.goally.data.model.api.response.copilet.Routines
import com.android.goally.databinding.ItemCopilotBinding
import com.android.goally.databinding.ItemCopilotDetailsEvenBinding
import com.android.goally.databinding.ItemCopilotDetailsOddBinding
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView

class CopiletListAdapter (
    private val context: Context,
    private val onItemClick:  (copilotId: String) -> Unit
) : RecyclerView.Adapter<CopiletListAdapter.ViewHolder>() {

    private val copilotList = mutableListOf<Routines>()

    fun updateList(newList: List<Routines>) {
        copilotList.clear()
        copilotList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CopiletListAdapter.ViewHolder {
        // val view = LayoutInflater.from(parent.context).inflate(R.layout.item_copilot, parent, false)
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_copilot, parent, false)
        return ViewHolder(ItemCopilotBinding.bind(view))


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val copilot = copilotList[position]

        holder.binding.run {
            copilot.run {
                Glide.with(context)
                    .load(imgURL)
                    .placeholder(R.drawable.clock)
                    .into(copiletIv)

                copiletNameTv.text = copilot.name.lowercase().replaceFirstChar( Char::titlecase )
                folderTv.text = copilot.folder.lowercase().replaceFirstChar( Char::titlecase )
                scheduleV2?.run {
                    type?.let {
                        when (type) {
                            "REPEATING_DAILY" -> {
                                scheduleTv.text = dailyRepeatValues?.keys?.joinToString(separator = ", ") { it.lowercase().replaceFirstChar(Char::titlecase) }
                            }

                            "REPEATING_YEARLY" -> {
                                scheduleTv.text = yearlyRepeatDateValue?.lowercase()?.replaceFirstChar( Char::titlecase )
                            }
                        }
                    }
                }
            }


            holder.itemView.setOnClickListener(View.OnClickListener {
                onItemClick(copilot._id)
            })
        }
    }


    override fun getItemCount(): Int = copilotList.size


    inner class ViewHolder(val binding: ItemCopilotBinding) :
        RecyclerView.ViewHolder(binding.root)
}



