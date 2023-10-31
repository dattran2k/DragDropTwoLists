package com.dat.drag_drop_fragment.widget

import android.util.Log
import android.view.DragEvent
import android.view.View

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
class DragListener : View.OnDragListener {
    val TAG = "DragListener"


    private var isDropped = false
    override fun onDrag(v: View, event: DragEvent): Boolean {
//        Log.e(
//            "DropListener",
//            "DropListener onDrag\ntarget :$v\nsource :${event.localState}\naction : ${
//                stringByAction(event.action)
//            }"
//        )
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                isDropped = false
//                (event.localState as View).visibility = View.INVISIBLE
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.e(TAG, "onDrag: ${stringByAction(event.action)}")
                if (v is DropAble) {
                    v.onHover(event.localState as? View?)
                }
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                if (v is ReplaceWidgetHolder) {
                    v.onLeaveHover(event.localState as? View?)
                }
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                val view = event.localState as? View?
//                view?.post {
//                    view.visibility = View.VISIBLE
//                }
                if (v is DropAble) {
                    v.onLeaveHover(v)
                }
            }

            DragEvent.ACTION_DROP -> {
                isDropped = true
//                (event.localState as? View?)?.visibility = View.VISIBLE
                val viewFrom = event.localState
                if (v is DropAble && viewFrom is View)
                    v.drop(viewFrom)
                else
                    return false
            }
        }

        return true
    }

    fun stringByAction(action: Int): String {
        return when (action) {
            DragEvent.ACTION_DRAG_STARTED -> "ACTION_DRAG_STARTED"
            DragEvent.ACTION_DRAG_EXITED -> "ACTION_DRAG_EXITED"
            DragEvent.ACTION_DRAG_ENTERED -> "ACTION_DRAG_ENTERED"
            DragEvent.ACTION_DROP -> "ACTION_DROP"
            DragEvent.ACTION_DRAG_ENDED -> "ACTION_DRAG_ENDED"
            DragEvent.ACTION_DRAG_LOCATION -> "ACTION_DRAG_LOCATION"
            else -> "$action"
        }
    }

}