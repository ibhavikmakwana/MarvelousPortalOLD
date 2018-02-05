package com.marvelousportal.fragments.events

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.marvelousportal.R
import com.marvelousportal.activities.DetailActivity
import com.marvelousportal.models.Result
import com.marvelousportal.utils.Constant.Companion.EVENTS
import kotlinx.android.synthetic.main.layout_grid.view.*
import java.util.*

/**
 * Created by Bhavik Makwana on 1/30/2018.
 */

class EventsAdapter(context: Context, comics: List<Result>) : RecyclerView.Adapter<EventsAdapter.ItemViewHolder>() {

    private var mResult: List<Result>? = ArrayList()
    private var mContext: Context? = null

    init {
        mResult = comics
        mContext = context

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder? {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_grid, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val resultInfo = mResult?.get(position)
        holder.charName.text = resultInfo?.title
        val path = resultInfo?.thumbnail?.path + "." + resultInfo?.thumbnail?.extension
        Log.i("PATH", path)
        Glide.with(mContext).load(path).into(holder.charImage)
        holder.rootLayout.setOnClickListener {
            DetailActivity.launchActivity(mContext!!, resultInfo?.id!!, EVENTS)
        }
    }

    override fun getItemCount(): Int {
        return mResult!!.size
    }

    inner class ItemViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var charName = itemView.text_character_name!!
        var charImage = itemView.image_character!!
        var rootLayout = itemView.row_root!!
    }

    fun setUserList(character: List<Result>?) {
        mResult = character
        notifyDataSetChanged()
    }
}
