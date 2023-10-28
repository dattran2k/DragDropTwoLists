package com.appchamp.dragdroptwolists

import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/** Ý tưởng khi drag fragment
 * fragment sẽ nằm trong fragment container, hay ở đâu đấy nhưng bắt buộc phải nằm trong 1 view
 * khi đó ta cần làm là có thể drag được view chứa fragment
 */

/** ý tưởng khi drop :
 * View pager chứa nhiều page, 1 page có 2 fragment
 * ở trái, phải fragment sẽ có 1 view nhỏ, khi drag tới sẽ thay đổi size và đổi màu
 * v = view, F = Fragment, 0 = rỗng, E = EmptyView
 * vFvFv : f1 + f2
 * 0E0Fv : E + f2
 * vF0E0 : f1 + E

 * Th1 : Khi kéo Fragment vào vị trí 1
 *      vFvFv -> vFvFv vFvEv
 *      0E0Fv -> vFvFv
 *      vF0E0 -> vFvFv
 * Th2 : Khi kéo fragment vào vị trí 2
 *      vFvFv -> vFvFv vFvEv
 *      0E0Fv -> 0E0Fv (Không có vị trí 2, ở giữa E và F và view rỗng)
 *      vF0E0 -> vF0E0 (Không có vị trí 2, ở giữa E và F và view rỗng)
 * Th3 : Khi kéo fragment vào vị trí 3
 *      vFvFv -> Khi này view sẽ tìm vị trí trống ở các page trước nó
 *               có -> Xoá view trống và đẩy các Fragment về trước 1 ô
 *               không -> Không biết ???????
 *      0E0Fv -> vFvFv
 *      vF0E0 -> vFvFv
 * => Xử lý ở viewpager có những trường hợp cần đẩy các fragment ra sau hoặc ra trước, chạy
animation để view giữa các viewpager (Cái này có thể sử dụng fade mà không dùng slide anim )
 */
class DragListener(val onViewEntered: (View) -> Unit, val onViewExited: (View) -> Unit) :
    View.OnDragListener {
    private var isDropped = false
    override fun onDrag(v: View, event: DragEvent): Boolean {
        Log.e(
            "DropListener",
            "DropListener onDrag\ntarget :$v\nsource :${event.localState}\naction : ${
                stringByAction(event.action)
            }"
        )
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                isDropped = false
                (event.localState as View).visibility = View.INVISIBLE
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                onViewExited(v)
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                onViewEntered(v)
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                val view = event.localState as? View?
                if (!isDropped) {
                    view?.post {
                        view.visibility = View.VISIBLE
                    }
                }
            }

            DragEvent.ACTION_DROP -> {
                isDropped = true
                // TODO ,khi vị trí scroll ở ngoài rv, thì sẽ không có position
                // Cần tìm cách
                val viewFrom = event.localState as View?
                if (viewFrom != null) {
                    val dataFrom = viewFrom.tag as String
                    val rvFrom = viewFrom.parent as RecyclerView
                    val adapterFrom = rvFrom.adapter as CustomAdapter
                    val listFrom = adapterFrom.getList()
                    val positionFrom = listFrom.indexOfFirst {
                        it == dataFrom
                    }
                    val viewTo = v
                    val rvTo = viewTo.parent as RecyclerView
                    val adapterTo = rvTo.adapter as CustomAdapter

                    if (positionFrom > -1) {
                        listFrom.removeAt(positionFrom)
                        adapterFrom.updateList(listFrom)
                        adapterFrom.notifyItemRemoved(positionFrom)
                        val listTo = adapterTo.getList()
                        val positionTo = listTo.indexOfFirst {
                            it == viewTo.tag
                        }.let {
                            if (it < 0)
                                listTo.size
                            else
                                it
                        }

                        var isRemove = false
                        if ((viewTo.tag as String).contains("Empty")) {
                            listTo.removeAt(positionTo)
                            isRemove = true
                        }

                        listTo.add(positionTo, dataFrom)
                        adapterTo.updateList(listTo)
                        if (isRemove)
                            adapterTo.notifyItemChanged(positionTo)
                        else
                            adapterTo.notifyItemInserted(positionTo)
                    }
                }
            }
        }

        return true
    }

    fun stringByAction(action: Int): String {
        return when (action) {
            DragEvent.ACTION_DRAG_STARTED -> "ACTION_DRAG_STARTED"
            DragEvent.ACTION_DRAG_EXITED -> "ACTION_DRAG_STARTED"
            DragEvent.ACTION_DRAG_ENTERED -> "ACTION_DRAG_STARTED"
            DragEvent.ACTION_DROP -> "ACTION_DRAG_STARTED"
            DragEvent.ACTION_DRAG_ENDED -> "ACTION_DRAG_STARTED"
            DragEvent.ACTION_DRAG_LOCATION -> "ACTION_DRAG_STARTED"
            else -> "$action"
        }
    }
}