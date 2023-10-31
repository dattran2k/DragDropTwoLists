package com.dat.drag_drop_fragment.widget

import android.view.View

interface DropAble {
    fun onHover(viewNeedDrop: View?)
    fun onLeaveHover(viewNeedDrop: View?)
    fun setonDropAddView(onDrop: OnDrop)
    fun drop(viewNeedDrop: View)
    interface OnDrop {
        fun onDropAddView(dropAble: DropAble, viewNeedDrop: View)
        fun onHover(viewNeedDrop: View)
        fun onLeaveHover(viewNeedDrop: View)
    }
}