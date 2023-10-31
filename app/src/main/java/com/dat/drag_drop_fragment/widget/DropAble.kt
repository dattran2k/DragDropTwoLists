package com.dat.drag_drop_fragment.widget

import android.view.View

interface DropAble {
    fun onHover(viewDrop: View?)
    fun onLeaveHover(viewDrop: View?)
    fun onDrop(viewDrop: View)
    interface OnDropCallBack {
        fun onDropAddView(dropPlace: View, viewDrop: View)
        fun onHover(dropPlace: View, viewDrop: View)
        fun onLeaveHover(viewDrop: View)
    }
}