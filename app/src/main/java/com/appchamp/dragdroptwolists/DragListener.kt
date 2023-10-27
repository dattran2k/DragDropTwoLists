package com.appchamp.dragdroptwolists

import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DragListener internal constructor(private val listener: CustomListener) : View.OnDragListener {
    private var isDropped = false
    override fun onDrag(v: View, event: DragEvent): Boolean {
        Log.e("DragListener", "onDrag $event", )
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                isDropped = false
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.e("DragListener", "ACTION_DRAG_ENTERED $v", )
            }
            DragEvent.ACTION_DROP -> {
                isDropped = true
                var positionTarget = -1
                val viewSource = event.localState as View?
                val viewId = v.id
                val frameLayoutItem = R.id.frame_layout_item
                val emptyTextView1 = R.id.empty_list_text_view_1
                val emptyTextView2 = R.id.empty_list_text_view_2
                val recyclerView1 = R.id.recycler_view_1
                val recyclerView2 = R.id.recycler_view_2
                when (viewId) {
                    frameLayoutItem, emptyTextView1, emptyTextView2, recyclerView1, recyclerView2 -> {
                        val target: RecyclerView
                        when (viewId) {
                            emptyTextView1, recyclerView1 -> target = v.rootView.findViewById<View>(recyclerView1) as RecyclerView
                            emptyTextView2, recyclerView2 -> target = v.rootView.findViewById<View>(recyclerView2) as RecyclerView
                            else -> {
                                target = v.parent as RecyclerView
                                positionTarget = v.tag as Int
                            }
                        }
                        if (viewSource != null) {
                            val source = viewSource.parent as RecyclerView
                            val adapterSource = source.adapter as CustomAdapter?
                            val positionSource = viewSource.tag as Int
                            val list: String? = adapterSource?.getList()?.get(positionSource)
                            val listSource = adapterSource?.getList()?.apply {
                                removeAt(positionSource)
                            }
                            listSource?.let {
                                adapterSource.updateList(it)
                            }
                            Log.e("DragListener", "positionSource $positionSource", )
                            Log.e("DragListener", "positionTarget $positionTarget", )
                            adapterSource?.notifyItemRemoved(positionSource)
                            val adapterTarget = target.adapter as CustomAdapter?
                            val customListTarget = adapterTarget?.getList()
                            if (positionTarget >= 0) {
                                list?.let { customListTarget?.add(positionTarget, it) }
                            } else {
                                list?.let { customListTarget?.add(it) }
                            }
                            customListTarget?.let { adapterTarget.updateList(it) }
                            adapterTarget?.notifyItemInserted(positionTarget)
                            viewSource.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        return true
    }
}