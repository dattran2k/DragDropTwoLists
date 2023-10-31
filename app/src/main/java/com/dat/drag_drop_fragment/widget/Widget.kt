package com.dat.drag_drop_fragment.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.dat.drag_drop_fragment.R
import com.dat.drag_drop_fragment.toPx
import com.dat.drag_drop_fragment.widget.WidgetMaster.Companion.MARGIN_START_EDIT_MODE

class Widget : FrameLayout, DropAble {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var onDrop: DropAble.OnDrop? = null


    override fun onHover(viewNeedDrop: View?) {
        if (childCount > 0)
            return
        setBackgroundColor(ContextCompat.getColor(context, R.color.purple_700))
    }

    override fun onLeaveHover(viewNeedDrop: View?) {
        if (childCount > 0)
            return
        setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))
    }

    override fun setonDropAddView(onDrop: DropAble.OnDrop) {
        this.onDrop = onDrop
    }

    override fun drop(viewNeedDrop: View) {
        if (childCount > 0)
            return
        onDrop?.onDropAddView(this, viewNeedDrop)
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
        layoutParams?.width =
            if (isEditMode) (viewPortWidth - toPx(MARGIN_START_EDIT_MODE)) / 4 else viewPortWidth / 2
        layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
//        setPadding(if (isEditMode) toPx(40) else 0, 0, 0, 0)
        requestLayout()
    }
}