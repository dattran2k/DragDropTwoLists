package com.dat.drag_drop_fragment

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment


fun View.animateAlpha(from: Float, to: Float) {
    val animation1 = AlphaAnimation(from, to)
    animation1.duration = 100
    animation1.fillAfter = true
    this.startAnimation(animation1)
}

fun Context.toPx(dp: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
).toInt()

fun Fragment.toPx(dp: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
).toInt()

fun View.toPx(dp: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
).toInt()

fun ViewGroup.getAllViews(): List<View> {
    val views = ArrayList<View>()
    for (i in 0 until childCount) {
        views.add(getChildAt(i))
    }
    return views
}
fun View?.removeSelf() {
    this ?: return
    val parentView = parent as? ViewGroup ?: return
    parentView.removeView(this)
}