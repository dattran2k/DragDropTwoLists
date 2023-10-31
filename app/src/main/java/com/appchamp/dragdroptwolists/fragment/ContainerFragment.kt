package com.appchamp.dragdroptwolists.fragment

import android.content.ClipData
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.appchamp.dragdroptwolists.AddView
import com.appchamp.dragdroptwolists.DragListener
import com.appchamp.dragdroptwolists.DragShadow
import com.appchamp.dragdroptwolists.ViewModel
import com.appchamp.dragdroptwolists.databinding.FragmentContainerTestBinding


class ContainerFragment : Fragment() {
    val TAG = "ContainerFragment"

    companion object {
        val NAME = "123123"
        fun newInstance(id: String): ContainerFragment {
            val args = Bundle()
            args.putString(NAME, id)
            val fragment = ContainerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var binding: FragmentContainerTestBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContainerTestBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString(NAME)?.split(",") ?: listOf()
        Log.e(TAG, "onViewCreated: $name")
        name.getOrNull(0)?.let {
            binding.root.addFragment(TextFragment.newInstance(it), childFragmentManager)
        }
        name.getOrNull(1)?.let {
            binding.root.addFragment(TextFragment.newInstance(it), childFragmentManager)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView: ")
    }
}
