package com.dat.drag_drop_fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dat.drag_drop_fragment.databinding.ActivityMainRvBinding
import com.dat.drag_drop_fragment.fragment.TextFragment
import com.dat.drag_drop_fragment.widget.callback.OnEditWidgetStateChanged


class MainActivityNestedScroll : AppCompatActivity(), OnEditWidgetStateChanged {
    companion object {
        const val WIDGET_RATIO = 1.31
    }

    private lateinit var binding: ActivityMainRvBinding
    val list = arrayListOf(1, 2, 3, 4, 5, 7, 8, 9)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRvBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.widgetMaster.setUpFragment(list.map {
            TextFragment.newInstance(it)
        }, supportFragmentManager)
        binding.widgetMaster.dragInstance.setOnEditWidgetStateChanged(this)
        binding.scrollTriggerPrevious.setOnDragListener(binding.widgetMaster.dragInstance)
        binding.scrollTriggerNext.setOnDragListener(binding.widgetMaster.dragInstance)
        binding.btn.setOnClickListener {
            ViewModel.isEnableEditModel.value = !(ViewModel.isEnableEditModel.value == true)
        }
    }

    override fun onDragging(dragView: View?) {

    }

    override fun onDropped(dragView: View?) {

    }

    override fun onDeleted(dragView: View?) {

    }

    override fun onScrollNext() {
        binding.scroller.scrollToNextPage()
    }

    override fun onScrollPrevious() {
        binding.scroller.scrollToPreviousPage()
    }

}