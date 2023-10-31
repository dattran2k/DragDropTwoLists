package com.dat.drag_drop_fragment.widget

import android.animation.LayoutTransition
import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dat.drag_drop_fragment.DragShadow
import com.dat.drag_drop_fragment.getAllViews

class WidgetMaster : LinearLayoutCompat, DropAble.OnDrop {
    companion object {
        const val TAG = "WidgetMaster"
        const val MARGIN_START_EDIT_MODE = 40
    }

    var viewPortWidth = 0

    val dragInstance: DragListener by lazy {
        DragListener()
    }

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
            val widget = Widget(context)
            val replaceWidgetHolder = ReplaceWidgetHolder(context, widget)
            addView(replaceWidgetHolder)
            widget.addFragment(fragment, fragmentManager)
            addView(widget)
        }
        updateEditMode()
        setupDrag()
    }


    private fun getAllWidget(): List<ViewGroup> {
        val result = arrayListOf<ViewGroup>()
        getAllViews().filterIsInstance<Widget>().forEach {
            result.add(it)
        }
        return result
    }

    fun onDropReplace(parentHoldFragmentContainer: ViewGroup, addView: View) {
        val targetPosition = getAllViews().indexOfFirst {
            it == parentHoldFragmentContainer
        }
        val sourcePosition = getAllViews().indexOfFirst {
            it == addView
        }
        if (targetPosition == sourcePosition)
            return
        val from = getChildAt(sourcePosition)
        val to = getChildAt(targetPosition)
        removeTransition()
        removeView(from)
        removeView(to)
        addView(to, sourcePosition)
        addTransition()
        addView(from, targetPosition)
        Log.e(TAG, "onDropReplace: $targetPosition")
    }

    fun addTransition() {
        val transition = LayoutTransition()
        transition.setDuration(1000)
        transition.enableTransitionType(LayoutTransition.CHANGING)
        transition.enableTransitionType(LayoutTransition.APPEARING)
        transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        transition.enableTransitionType(LayoutTransition.DISAPPEARING)
        transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)
        layoutTransition = transition
    }

    fun removeTransition() {
        layoutTransition = null
    }

    fun setupDrag() {
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v is DropAble) {
                v.setonDropAddView(this)
                v.setOnDragListener(dragInstance)
            }
            if (v is Widget) {
                v.setOnLongClickListener {
                    val ds = DragShadow(v)
                    val data = ClipData.newPlainText("", "")
                    v.startDragAndDrop(data, ds, v, View.DRAG_FLAG_OPAQUE)
                    true
                }
            }
        }

    }

    override fun onDropAddView(dropAble: DropAble, viewNeedDrop: View) {
        Log.e(TAG, "onDropAddView: ")
        when (dropAble) {
//            addView1 -> onDropReplace(binding.parent1, viewNeedDrop)
//            binding.addView2 -> findReplaceRequest()?.onDropReplace(binding.parent2, viewNeedDrop)
            is Widget -> {
                onDropReplace(dropAble, viewNeedDrop)
            }
        }
    }

    override fun onHover(viewNeedDrop: View) {
        if (viewNeedDrop.tag != null || (viewNeedDrop.tag as? View?)?.parent != null)
            return
        val layoutParams = viewNeedDrop.layoutParams
        val tempView = View(context)
        tempView.layoutParams = layoutParams
        viewNeedDrop.tag = tempView
        addTransition()
        val index = getAllViews().indexOfFirst {
            it == viewNeedDrop
        }
        addView(tempView, index)
    }

    override fun onLeaveHover(viewNeedDrop: View) {
        val view = viewNeedDrop.tag as? View?
        view?.let {
            removeView(it)
        }
        viewNeedDrop.tag = null
    }

}