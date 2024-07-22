package com.hc.artai.ui.arts

import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hc.artai.R

import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.data.model.ArtStyleModel
import com.hc.artai.databinding.FragmentArtStyleBinding
import com.hc.artai.ui.home.HomeViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtStyleFragment : BaseFragment<HomeViewModel, FragmentArtStyleBinding>(),
    ArtStyleAdapter.ItemClickListener {

    private val artStyleAdapter: ArtStyleAdapter by lazy {
        ArtStyleAdapter(
            requireContext(),
            this@ArtStyleFragment
        )
    }

    private var selectedArtStyle: ArtStyleModel? = null
    private var selectedItemPosition: Int = 0

    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_art_style

    override fun onInitDataBinding() {
//        dialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
        viewBinding.ivRight.setOnClickListener {
            navigateHome()
        }
        setupArtStyles()

        viewBinding.buttonContinue.setOnClickListener {
            navigateHome()
        }
    }

    private fun navigateHome() {
        try {
            val action = ArtStyleFragmentDirections.actionArtStyleFragmentToHomeFragment(
                selectedItemPosition,
                selectedArtStyle,
                false
            )
            findNavController().navigate(action)


        } catch (e: Exception) {
            Log.e("ecooo", "edsfgsd ${e.localizedMessage}")
        }
    }

    private fun setupArtStyles() {
        viewBinding.rvArtStyle.apply {
            adapter = artStyleAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
        }
        artStyleAdapter.submitList(viewModel.artList)
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onItemClick(item: ArtStyleModel) {
        selectedItemPosition = viewModel.artList.indexOf(item)
        selectedArtStyle = item
        item.isSelected = true
        artStyleAdapter.notifyDataSetChanged()

    }
}