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
import com.android.goally.databinding.ItemCopilotDetailsEvenBinding
import com.android.goally.databinding.ItemCopilotDetailsOddBinding
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView

class CopiletDetailsAdapter (
    private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val copilotList = mutableListOf<Activities>()

    fun updateList(newList: List<Activities>) {
        copilotList.clear()
        copilotList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       // val view = LayoutInflater.from(parent.context).inflate(R.layout.item_copilot, parent, false)
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        if (viewType == 0) { // odd index
            view = inflater.inflate(R.layout.item_copilot_details_odd, parent, false)
            return ViewHolder1(ItemCopilotDetailsOddBinding.bind(view))
        } else { // even index
            view = inflater.inflate(R.layout.item_copilot_details_even, parent, false)
            return ViewHolder2(ItemCopilotDetailsEvenBinding.bind(view))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val copilot = copilotList[position]
        when (holder) {
            is ViewHolder1 -> {
                holder.binding.run {
                    copilot.run {
                        Glide.with(context)
                            .load(imgUrl)
                            .placeholder(R.drawable.clock)
                            .into(activityOddIv)
                        activityOddNameTv.text = name
                        if (position==0) dotedTopIv.visibility =View.INVISIBLE

                    }
                }
            }
            is ViewHolder2 -> {
                holder.binding.run {
                    copilot.run {
                        Glide.with(context)
                            .load(imgUrl)
                            .placeholder(R.drawable.copilet_activity_placeholder)
                            .into(activityEvenIv)
                        avtivityEvenNameTv.text = name

                    }

                }
                // bind data to ViewHolder2
            }
        }

      //
     //   holder.bind(copilot)
    }

    override fun getItemCount(): Int = copilotList.size

    override fun getItemViewType(position: Int): Int {
        return position % 2 // 0 for odd index, 1 for even index
    }



    inner class ViewHolder1(val binding: ItemCopilotDetailsOddBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ViewHolder2(val binding: ItemCopilotDetailsEvenBinding) :
        RecyclerView.ViewHolder(binding.root)



}