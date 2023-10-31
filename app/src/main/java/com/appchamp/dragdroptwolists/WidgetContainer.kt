package com.appchamp.dragdroptwolists

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TableRow
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.appchamp.dragdroptwolists.databinding.WidgetContainerBinding


class WidgetMaster : LinearLayoutCompat, WidgetContainer.ReplaceRequest {
    companion object {
        const val TAG = "WidgetMaster"
    }

    var viewPortWidth = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    var isEditMode = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(TAG, "onMeasure: ${rootView.width}")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (viewPortWidth != rootView.width) {
            viewPortWidth = rootView.width
            updateEditMode()
        }
    }

    fun updateEditMode(isEditMode: Boolean = this.isEditMode) {
        this.isEditMode = isEditMode
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is WidgetContainer)
                setViewParams(child)
        }
    }

    private fun setViewParams(view: View?) {
        view?.layoutParams?.width = if (isEditMode) rootView.width / 2 else rootView.width
        view?.requestLayout()
    }

    fun setUpFragment(listFragment: List<Fragment>, fragmentManager: FragmentManager) {
        listFragment.forEachIndexed { index, fragment ->
            val t = fragmentManager.beginTransaction()
            t.add(id, fragment, "$index")
            t.commit()
            Log.e(TAG, "setUpFragment: $fragment")
        }
        updateEditMode(isEditMode)
    }

    fun getAllWidgetContainer(): List<WidgetContainer> {
        val result = arrayListOf<WidgetContainer>()
        fun ViewGroup.getChildWidgetContainer() {
            for (i in 0 until this.childCount) {
                val view = this[i]
                if (view is WidgetContainer) {
                    result.add(view)
                } else if (view is ViewGroup)
                    view.getChildWidgetContainer()
            }
        }
        getChildWidgetContainer()
        return result
    }

    private fun getAllParentHoldFragmentContainer(): List<ViewGroup> {
        val result = arrayListOf<ViewGroup>()
        getAllWidgetContainer().forEach {
            result.addAll(it.getParentHoldFragmentContainer())
        }
        return result
    }

    override fun onDropReplace(parentHoldFragmentContainer: ViewGroup, addView: View) {
        val allParentHold = getAllParentHoldFragmentContainer()
        val targetPosition = allParentHold.indexOfFirst {
            it == parentHoldFragmentContainer
        }
        val sourcePosition = allParentHold.indexOfFirst {
            it == addView
        }

//        if (sou)
        swapViewChild(
            allParentHold.getOrNull(sourcePosition),
            allParentHold.getOrNull(targetPosition)
        )
        Log.e(TAG, "onDropReplace: $targetPosition")
    }

    //    private fun swapViewChild(fromGroup: ViewGroup?, toGroup: ViewGroup?) {
//        if (fromGroup == toGroup)
//            return
//        val childFrom = fromGroup?.getChildAt(0)!!
//        val childTo = toGroup?.getChildAt(0)!!
//        val move = ChangeTransform()
//            .addTarget(childTo)
//            .setDuration(10000)
//        TransitionManager.beginDelayedTransition(fromGroup, move)
//        fromGroup.removeAllViews()
//        toGroup.removeAllViews()
//        childTo.elevation = 10f
//        fromGroup.addView(childTo)
//        toGroup.addView(childFrom)
//    }
    private fun swapViewChild(fromGroup: ViewGroup?, toGroup: ViewGroup?) {
        if (fromGroup == toGroup)
            return
        val childFrom = fromGroup?.getChildAt(0)
        val childTo = toGroup?.getChildAt(0)

        fromGroup?.removeAllViews()
        toGroup?.removeAllViews()
        childTo?.let {
            fromGroup?.addView(childTo)
        }
        childFrom?.let {
            toGroup?.addView(childFrom)
        }
    }

    override fun createNewsWidgetContainer(fragmentAdd: Fragment) {

    }


}

class WidgetContainer : TableRow, DropAble.OnDrop {
    companion object {
        const val TAG = "WidgetContainer"
    }

    val dragInstance: DragListener by lazy {
        DragListener()
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs)

    val binding: WidgetContainerBinding =
        WidgetContainerBinding.inflate(LayoutInflater.from(context), this, true)

    private val mRootLayout = binding.root

    init {
        for (i in 0 until mRootLayout.childCount) {
            val v = mRootLayout.getChildAt(i)
            if (v is DropAble) {
                v.setonDropAddView(this)
                v.setOnDragListener(dragInstance)
            }
        }
        binding.parent1.setOnLongClickListener { v ->
            val ds = DragShadow(v)
            val data = ClipData.newPlainText("", "")
            v?.startDragAndDrop(data, ds, v, View.DRAG_FLAG_OPAQUE)
            true
        }
        binding.parent2.setOnLongClickListener { v ->
            val ds = DragShadow(v)
            val data = ClipData.newPlainText("", "")
            v?.startDragAndDrop(data, ds, v, View.DRAG_FLAG_OPAQUE)
            true
        }
    }

    fun addFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        val addPosition = getRemainAddPosition()
        if (addPosition == null) {
            findReplaceRequest()?.createNewsWidgetContainer(fragment)
            return
        }
        Log.e(TAG, "addFragment: $fragment")
        val fragmentContainerView = FragmentContainerView(context)
        fragmentContainerView.id = View.generateViewId()
        addPosition.addView(fragmentContainerView)
        val t: FragmentTransaction = fragmentManager.beginTransaction()
        t.replace(fragmentContainerView.id, fragment, fragment::class.java.simpleName)
        t.commit()
    }

    private fun getRemainAddPosition() = if (binding.parent1.childCount == 0)
        binding.parent1
    else if (binding.parent2.childCount == 0)
        binding.parent2
    else
        null

    fun getParentHoldFragmentContainer() = listOf(binding.parent1, binding.parent2)

    override fun onDropAddView(dropAble: DropAble, viewNeedDrop: View) {
        Log.e(TAG, "onDropAddView: ")
        when (dropAble) {
            binding.addView1 -> findReplaceRequest()?.onDropReplace(binding.parent1, viewNeedDrop)
            binding.addView2 -> findReplaceRequest()?.onDropReplace(binding.parent2, viewNeedDrop)
            is WidgetParent -> {
                findReplaceRequest()?.onDropReplace(dropAble, viewNeedDrop)
            }
        }
    }

    interface ReplaceRequest {
        fun onDropReplace(parentHoldFragmentContainer: ViewGroup, addView: View)
        fun createNewsWidgetContainer(fragmentAdd: Fragment)
    }

    private fun findReplaceRequest(): ReplaceRequest? {
        var v = parent as View?
        if (v is ReplaceRequest)
            return v
        while (v != null && v !is ReplaceRequest) {
            val parent = v.parent
            v = if (parent is View)
                parent
            else
                return null
        }
        return null
    }
}

class AddView : View, DropAble {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var onDrop: DropAble.OnDrop? = null
    override fun setonDropAddView(onDrop: DropAble.OnDrop) {
        this.onDrop = onDrop
    }

    override fun onHover() {
        layoutParams.width = toPx(24)
        setBackgroundColor(ContextCompat.getColor(context, R.color.purple_700))
    }

    override fun onNoHover() {
        layoutParams.width = toPx(16)
        setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))
    }

    override fun drop(viewNeedDrop: View) {
        onDrop?.onDropAddView(this, viewNeedDrop)
    }

}

class WidgetParent : FrameLayout, DropAble {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var onDrop: DropAble.OnDrop? = null

    override fun onHover() {
        if (childCount > 0)
            return
        setBackgroundColor(ContextCompat.getColor(context, R.color.purple_700))
    }

    override fun onNoHover() {
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

}

interface DropAble {
    fun onHover()
    fun onNoHover()
    fun setonDropAddView(onDrop: OnDrop)
    fun drop(viewNeedDrop: View)
    interface OnDrop {
        fun onDropAddView(dropAble: DropAble, viewNeedDrop: View)
    }
}