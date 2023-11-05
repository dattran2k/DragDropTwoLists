package com.dat.drag_drop_fragment.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.dat.drag_drop_fragment.MainActivityNestedScroll
import com.dat.drag_drop_fragment.databinding.ItemLayoutBinding


class MyAdapter(private val data: List<Int>, val context: Context) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    val width = Resources.getSystem().displayMetrics.widthPixels
    val height = Resources.getSystem().displayMetrics.heightPixels
    var isEditMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateEditMode(rv: RecyclerView) {
        isEditMode = !isEditMode
        data.forEachIndexed { index, i ->
            val holder = rv.findViewHolderForLayoutPosition(index)
            if (holder is MyViewHolder) {
                holder.updateEditMode()
            }
        }
    }

    inner class MyViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams.width = width / 2
        }

        fun bind() {
            val fragment = TextFragment.newInstance(data[adapterPosition])
            val fragmentContainerView = FragmentContainerView(context)
            fragmentContainerView.id = View.generateViewId()
            // Đặt tỷ lệ 6:19 cho fragmentContainerView
            binding.parentLayout.addView(fragmentContainerView)
            val t: FragmentTransaction = (context as MainActivityNestedScroll).supportFragmentManager.beginTransaction()
            (fragmentContainerView.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
            t.replace(fragmentContainerView.id, fragment, fragment::class.java.simpleName)
            t.commit()
        }

        fun updateEditMode() {
            val targetScaleX = if (isEditMode) 0.5f else 1f
            val targetScaleY = if (isEditMode) 0.5f else 1f

            val targetWidth = if (isEditMode) width / 4 else width / 2
            val targetHeight = if (isEditMode) height / 2 else height

            val widthAnimator = ObjectAnimator.ofInt(binding.root.width, targetWidth)
            val heightAnimator = ObjectAnimator.ofInt(binding.root.height, targetHeight)
            val scaleXAnimator = ObjectAnimator.ofFloat(binding.root, View.SCALE_X, targetScaleX)
            val scaleYAnimator = ObjectAnimator.ofFloat(binding.root, View.SCALE_Y, targetScaleY)

            widthAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                val layoutParams = binding.root.layoutParams
                layoutParams.width = animatedValue
                binding.root.layoutParams = layoutParams
            }
            heightAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                val layoutParams = binding.root.layoutParams
                layoutParams.height = animatedValue
                binding.root.layoutParams = layoutParams
            }
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(heightAnimator, widthAnimator)
//            animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
            animatorSet.duration = 500 // Adjust the duration as needed (in milliseconds)

            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // Animation started
                }

                override fun onAnimationEnd(animation: Animator) {
                    // Animation ended
                }

                override fun onAnimationCancel(animation: Animator) {
                    // Animation canceled
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // Animation repeated
                }
            })
            animatorSet.start()
        }
    }
}
