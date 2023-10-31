package com.dat.drag_drop_fragment.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.dat.drag_drop_fragment.R
import com.dat.drag_drop_fragment.toPx


@SuppressLint("ViewConstructor")
class ReplaceWidgetHolder(
    context: Context?,
    private val linkWidget: Widget
) : View(context), DropAble {

    private var onDrop: DropAble.OnDrop? = null
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams?.width = toPx(16)
    }
    override fun setonDropAddView(onDrop: DropAble.OnDrop) {
        this.onDrop = onDrop
    }

    override fun onHover(viewNeedDrop: View?) {
        layoutParams.width = toPx(32)
        setBackgroundColor(ContextCompat.getColor(context, R.color.color_replace))
        requestLayout()
        onDrop?.onHover(linkWidget)
    }

    override fun onLeaveHover(viewNeedDrop: View?) {
        layoutParams.width = toPx(16)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        requestLayout()
        onDrop?.onLeaveHover(linkWidget)
    }

    override fun drop(viewNeedDrop: View) {
        onDrop?.onDropAddView(linkWidget, viewNeedDrop)
    }

}

