package com.adentech.artai.ui.arts

import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.adentech.artai.R
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.databinding.FragmentArtStyleBinding
import com.adentech.artai.ui.home.HomeViewModel

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