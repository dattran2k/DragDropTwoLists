package com.appchamp.dragdroptwolists

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.DRAG_FLAG_OPAQUE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CustomAdapter(private var list: ArrayList<String>,val dragListener: DragListener) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder?>(), View.OnTouchListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CustomViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int = list.size

    fun updateList(list: ArrayList<String>) {
        this.list = list
    }

    fun getList(): ArrayList<String> = list

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val ds = DragShadow(v)
                val data = ClipData.newPlainText("", "")
                v?.visibility = View.GONE
                v?.startDragAndDrop(data, ds, v, DRAG_FLAG_OPAQUE)
                return true
            }
        }
        return false
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.text?.text = list[position]
        holder.frameLayout?.tag = position
        holder.frameLayout?.setOnTouchListener(this)
        holder.frameLayout?.setOnDragListener(dragListener)
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
}