package com.dat.drag_drop_fragment.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.dat.drag_drop_fragment.R
import com.dat.drag_drop_fragment.widget.WidgetMaster.Companion.findWidgetMaster

class EmptyWidget : Widget {
    companion object {
        const val TAG = "EmptyWidget"
    }

    constructor(context: Context, isEditMode: Boolean) : super(context, isEditMode)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onHover(viewDrop: View?) {
        Log.e(TAG, "onHover: ")
        setBackgroundColor(ContextCompat.getColor(context, R.color.color_replace))
    }

    override fun onLeaveHover(viewDrop: View?) {
        Log.e(TAG, "onLeaveHover: ")
        setBackgroundColor(ContextCompat.getColor(context, R.color.color_widget))
        viewDrop?.let {
            findWidgetMaster()?.onLeaveHover(it)
        }
    }
}