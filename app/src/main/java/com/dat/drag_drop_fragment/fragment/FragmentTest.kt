package com.dat.drag_drop_fragment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dat.drag_drop_fragment.databinding.FragmentTestBinding
import com.dat.drag_drop_fragment.databinding.ListItemBinding

class FragmentTest : Fragment() {
    lateinit var binding: FragmentTestBinding
    val list2 = arrayListOf(11, 22, 33, 44, 55,66, 77, 88, 99)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.rv2.setUpFragment(list2.map {
//            TextFragment.newInstance(it)
//        }, supportFragmentManager)
    }
}