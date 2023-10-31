package com.appchamp.dragdroptwolists

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appchamp.dragdroptwolists.databinding.ActivityMainRvBinding
import com.appchamp.dragdroptwolists.fragment.ContainerFragment

class MainActivityNestedScroll : AppCompatActivity() {
    val dragInstance: DragListener = DragListener()
    private lateinit var binding: ActivityMainRvBinding
    val list = arrayListOf(1, 2, 3, 4, 5)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRvBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val listFragment = List(((list.size / 2.0) + 0.5).toInt()) {
            ContainerFragment.newInstance(
                list.subList(it * 2, list.size).take(2).joinToString()
            )
        }

        binding.widgetMaster.setUpFragment(listFragment, supportFragmentManager)
        ViewModel.isEnableEditModel.observe(this) {
            binding.widgetMaster.updateEditMode(it)
        }
        binding.btn.setOnClickListener {
            ViewModel.isEnableEditModel.value = ViewModel.isEnableEditModel.value != true

        }
    }
}