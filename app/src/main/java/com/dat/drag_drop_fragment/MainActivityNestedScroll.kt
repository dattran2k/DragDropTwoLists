package com.dat.drag_drop_fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.dat.drag_drop_fragment.databinding.ActivityMainRvBinding
import com.dat.drag_drop_fragment.fragment.TextFragment
import com.dat.drag_drop_fragment.widget.MyScroller
import com.dat.drag_drop_fragment.widget.WidgetMaster
import com.dat.drag_drop_fragment.widget.callback.OnEditWidgetStateChanged


class MainActivityNestedScroll : AppCompatActivity(), OnEditWidgetStateChanged {
    companion object {
        const val WIDGET_RATIO = 1.31
    }

    private lateinit var binding: ActivityMainRvBinding
    val list = arrayListOf(1, 2, 3, 4, 5, 7, 8, 9)
    val list2 = arrayListOf(11, 22, 33, 44, 55, 66, 77, 88, 99)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRvBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.widgetMaster.setUpFragment(list.map {
            TextFragment.newInstance(it)
        }, supportFragmentManager, WidgetMaster.WidgetType.TO)
        binding.widgetMaster2.setUpFragment(list2.map {
            TextFragment.newInstance(it)
        }, supportFragmentManager, WidgetMaster.WidgetType.FROM)
        binding.widgetMaster.dragInstance.setOnEditWidgetStateChanged(this)
        binding.scrollTriggerPrevious.setOnDragListener(binding.widgetMaster.dragInstance)
        binding.scrollTriggerNext.setOnDragListener(binding.widgetMaster.dragInstance)
        binding.btn.setOnClickListener {
            ViewModel.isEnableEditModel.value = ViewModel.isEnableEditModel.value != true
        }

        binding.scroller.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.tabLayout.setScroll(scrollX, binding.widgetMaster.width)
        }
        binding.scroller.setOnPageChangeListener(object : MyScroller.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.setActiveMarker(position)
            }

            override fun onUpdateTotalPage(totalPage: Int) {
                binding.tabLayout.setMarkersCount(totalPage)
            }
        })
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