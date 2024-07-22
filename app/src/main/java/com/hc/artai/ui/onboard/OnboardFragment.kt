package com.hc.artai.ui.onboard

import android.view.View
import com.hc.artai.R
import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.databinding.FragmentOnboardBinding
import com.hc.artai.extensions.handleOnBackPressed
import com.hc.artai.extensions.navigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnboardFragment : BaseFragment<OnboardViewModel, FragmentOnboardBinding>() {

    override fun viewModelClass() = OnboardViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_onboard

    override fun onInitDataBinding() {
        handleOnBackPressed {
            requireActivity().finish()
        }
        viewModel.saveOnBoardingState()
        launchHomeScreen()
    }

    private fun launchHomeScreen(){
        viewBinding.next.setOnClickListener {
            navigate(OnboardFragmentDirections.actionOnboardFragmentToOnboardTwoFragment())
        }
    }

}