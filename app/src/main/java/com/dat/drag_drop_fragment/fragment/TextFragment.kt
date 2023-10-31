package com.dat.drag_drop_fragment.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dat.drag_drop_fragment.databinding.ListItemBinding

class TextFragment : Fragment() {
    val TAG = "TextFragment"

    companion object {
        val NAME = "123123"
        fun newInstance(id: String): TextFragment {
            val args = Bundle()
            args.putString(NAME, id)
            val fragment = TextFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var binding: ListItemBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString(NAME) ?: ""
        binding.text.text = name
        view.tag = name
        Log.e(TAG, "onViewCreated: $name")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView: ")
    }
}
