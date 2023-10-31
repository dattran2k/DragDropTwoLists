package com.dat.drag_drop_fragment.widget

import android.animation.LayoutTransition
import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dat.drag_drop_fragment.DragShadow
import com.dat.drag_drop_fragment.getAllViews
import com.dat.drag_drop_fragment.removeSelf
import com.dat.drag_drop_fragment.toPx

class WidgetMaster : LinearLayoutCompat, DropAble.OnDropCallBack {
    companion object {
        const val TAG = "WidgetMaster"
        const val MARGIN_START_EDIT_MODE = 40
        fun View?.findWidgetMaster(): WidgetMaster? {
            if (this is WidgetMaster)
                return this
            if (this == null)
                return null
            return (this.parent as? View?)?.findWidgetMaster()
        }
    }

    var viewPortWidth = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        addTransition()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        layoutParams.width = toPx(16)
    }

    var isEditMode = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(TAG, "onMeasure: ${rootView.width}")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.e(TAG, "onLayout: $changed")
        if (viewPortWidth != rootView.width) {
            viewPortWidth = rootView.width
            updateEditMode()
        }
    }

    fun updateEditMode(isEditMode: Boolean = this.isEditMode) {
        this.isEditMode = isEditMode
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is Widget)
                child.updateMode(isEditMode, viewPortWidth)
        }
    }

    fun setUpFragment(listFragment: List<Fragment>, fragmentManager: FragmentManager) {
        listFragment.forEachIndexed { index, fragment ->
//            val frameWrap = FrameLayout(context)
//            frameWrap.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
//            frameWrap.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            val widget = Widget(context, isEditMode)
//            val replaceWidgetHolder = ReplaceWidgetHolder(context, widget)
//            addView(replaceWidgetHolder)
            widget.addFragment(fragment, fragmentManager)
            addView(widget)
        }
        updateEditMode()
    }


    private fun getAllWidget(): List<ViewGroup> {
        val result = arrayListOf<ViewGroup>()
        getAllViews().filterIsInstance<Widget>().forEach {
            result.add(it)
        }
        return result
    }

    private fun onDropReplace(dropPlace: View, viewDrop: View) {
        if (dropPlace == viewDrop)
            return
        val dropPosition = getAllViews().indexOfFirst {
            it == viewDrop
        }
        val dropPlacePosition = getAllViews().indexOfFirst {
            it == dropPlace
        }
        if (dropPosition < 0 || dropPlacePosition < 0) {
            // Handle cases where the views aren't found in the parent view
            return
        }
        removeTransition()

        dropPlace.removeSelf()
        viewDrop.removeSelf()
        if (dropPlace !is EmptyWidget)
            addView(dropPlace, dropPosition)
//        addTransition()
        addView(viewDrop, dropPlacePosition)
        viewDrop.tag = null
        Log.e(TAG, "onDropReplace: $dropPlacePosition")
        updateEditMode()
    }

    private fun addTransition() {
        val transition = LayoutTransition()
        transition.setDuration(500)
        transition.enableTransitionType(LayoutTransition.CHANGING)
        transition.enableTransitionType(LayoutTransition.APPEARING)
        transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        transition.enableTransitionType(LayoutTransition.DISAPPEARING)
        transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)
        layoutTransition = transition
    }

    private fun removeTransition() {
        layoutTransition = null
    }

    override fun onDropAddView(dropPlace: View, viewDrop: View) {
        Log.e(TAG, "onDropAddView: ")
        if (dropPlace is EmptyWidget)
            onDropReplace(dropPlace, viewDrop)
    }

    override fun onHover(dropPlace: View, viewDrop: View) {
        Log.e(TAG, "onHover: $viewDrop")
        if (viewDrop.tag != null || (viewDrop.tag as? View?)?.parent != null)
            return
        val tempView = EmptyWidget(context, isEditMode)
        viewDrop.tag = tempView
        addTransition()
        val index = getAllViews().indexOfFirst {
            it == dropPlace
        }
        addView(tempView, index)

    }

    override fun onLeaveHover(viewDrop: View) {
        Log.e(TAG, "onLeaveHover: $viewDrop")
        val view = viewDrop.tag as? View?
        view?.post {
            removeTransition()
            removeView(view)
            viewDrop.tag = null
        }
    }

}