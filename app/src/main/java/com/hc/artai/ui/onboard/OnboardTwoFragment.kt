package com.hc.artai.ui.onboard

import android.view.View
import com.hc.artai.R
import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.data.model.ArtStyleModel
import com.hc.artai.databinding.FragmentOnboardTwoBinding
import com.hc.artai.extensions.handleOnBackPressed
import com.hc.artai.extensions.navigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnboardTwoFragment : BaseFragment<OnboardViewModel, FragmentOnboardTwoBinding>() {

    override fun viewModelClass() = OnboardViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_onboard_two

    override fun onInitDataBinding() {
        handleOnBackPressed {
            requireActivity().finish()
        }
        viewModel.saveOnBoardingState()
        launchHomeScreen()
    }

    private fun launchHomeScreen(){
        viewBinding.nextTwo.setOnClickListener {
            val artStyleModel = ArtStyleModel(null, null, null)
            navigate(OnboardTwoFragmentDirections.actionOnboardTwoFragmentToHomeFragment(
                0,
                artStyleModel,
                false
            ))
        }
    }

}