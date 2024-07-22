package com.hc.artai.ui.onboard

import android.view.View
import com.hc.artai.R
import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.data.model.ArtStyleModel
import com.hc.artai.databinding.FragmentOnboardThreeBinding
import com.hc.artai.extensions.navigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnboardThreeFragment : BaseFragment<OnboardViewModel, FragmentOnboardThreeBinding>() {

    override fun viewModelClass() = OnboardViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_onboard_three

    override fun onInitDataBinding() {
        viewBinding.onboardExit.setOnClickListener {
            viewModel.saveOnBoardingState()
            launchHomeScreen()
        }
    }

    private fun launchHomeScreen() {
//        val artStyleModel = ArtStyleModel(
//            null,
//            null,
//            null
//        )
       // navigate(OnboardThreeFragmentDirections.actionOnboardThreeFragmentToHomeFragment(0, artStyleModel, false))
    }
}