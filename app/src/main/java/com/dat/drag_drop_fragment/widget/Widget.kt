package com.dat.drag_drop_fragment.widget

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.dat.drag_drop_fragment.DragShadow
import com.dat.drag_drop_fragment.R
import com.dat.drag_drop_fragment.toPx
import com.dat.drag_drop_fragment.widget.WidgetMaster.Companion.MARGIN_START_EDIT_MODE
import com.dat.drag_drop_fragment.widget.WidgetMaster.Companion.findWidgetMaster

open class Widget : FrameLayout, DropAble {
    companion object {
        const val TAG = "Widget"
    }

    constructor(context: Context, isEditMode: Boolean = false) : super(context) {
        this.isEditMode = isEditMode
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isEditMode: Boolean = false
    private val dragInstance: DragListener by lazy {
        DragListener()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findWidgetMaster()?.let {
            updateMode(isEditMode, it.viewPortWidth)
        }
        setOnLongClickListener {
            val ds = DragShadow(this)
            val data = ClipData.newPlainText("", "")
            if (isEditMode)
                startDragAndDrop(data, ds, this, View.DRAG_FLAG_OPAQUE)
            isEditMode
        }
        setBackgroundColor(ContextCompat.getColor(context, R.color.color_widget))
        val padding = toPx(16)
        setPadding(padding, padding, padding, padding)
        Log.e(TAG, "onAttachedToWindow: ")
    }

    override fun onHover(viewDrop: View?) {
        if (childCount > 0) {
            if (viewDrop == this)
                return
            viewDrop?.let {
                findWidgetMaster()?.onHover(this, it)
            }
            return
        }
    }

    override fun onLeaveHover(viewDrop: View?) {
        if (childCount > 0) {
            findWidgetMaster()?.onLeaveHover(this)
        }

    }


    override fun onDrop(viewDrop: View) {
        findWidgetMaster()?.onDropAddView(this, viewDrop)
    }

    fun addFragment(fragmentAdd: Fragment, fragmentManager: FragmentManager) {
        val fragmentContainerView = FragmentContainerView(context)
        fragmentContainerView.id = View.generateViewId()
        addView(fragmentContainerView)
        val t: FragmentTransaction = fragmentManager.beginTransaction()
        t.replace(fragmentContainerView.id, fragmentAdd, fragmentAdd::class.java.simpleName)
        t.commit()
    }

    fun updateMode(isEditMode: Boolean, viewPortWidth: Int) {
        this.isEditMode = isEditMode
        if (isEditMode) {
            setOnDragListener(dragInstance)
        } else {
            setOnDragListener(null)
        }
        layoutParams?.width = if (isEditMode) (viewPortWidth - toPx(MARGIN_START_EDIT_MODE)) / 4 else viewPortWidth / 2
        layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        val currentViewPos = findWidgetMaster()?.indexOfChild(this)
        updateLayoutParams<MarginLayoutParams> {
            marginStart = if (currentViewPos != null && currentViewPos > -1 && currentViewPos % 2 == 0) toPx(16) else 0
        }
        requestLayout()
    }

}
