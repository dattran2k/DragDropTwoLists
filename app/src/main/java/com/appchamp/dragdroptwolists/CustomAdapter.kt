package com.appchamp.dragdroptwolists

import android.content.ClipData
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.DRAG_FLAG_OPAQUE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class CustomAdapter(
    val context: Context,
    private var list: ArrayList<String>,
    val dragListener: View.OnDragListener
) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder?>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CustomViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int = list.size

    fun getList(): ArrayList<String> = ArrayList(list)

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.text?.text = list[position]
        holder.frameLayout?.tag = list[position]
        holder.frameLayout?.setOnLongClickListener { v ->
            val ds = DragShadow(v)
            val data = ClipData.newPlainText("", "")
            v?.startDragAndDrop(data, ds, v, DRAG_FLAG_OPAQUE)
            v?.visibility = View.VISIBLE
            true
        }
        holder.frameLayout?.setOnDragListener(dragListener)
        holder.frameLayout?.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200))
        holder.frameLayout?.visibility = View.VISIBLE
    }

    class CustomViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {
        var text: TextView? = null
        var frameLayout: FrameLayout? = null

        init {
            text = itemView.findViewById(R.id.text)
            frameLayout = itemView.findViewById(R.id.frame_layout_item)
        }
    }

    fun updateList(list: List<String>) {
        this.list.clear()
        this.list.addAll(list)
    }
}