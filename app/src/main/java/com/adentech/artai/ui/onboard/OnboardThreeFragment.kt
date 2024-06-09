package com.adentech.artai.ui.onboard

import android.view.View
import com.adentech.artai.R
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.databinding.FragmentOnboardThreeBinding
import com.adentech.artai.extensions.navigate
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnboardThreeFragment : BaseFragment<OnboardViewModel, FragmentOnboardThreeBinding>() {

    override fun viewModelClass() = OnboardViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_onboard_three

    override fun onInitDataBinding() {
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
        navBar.visibility = View.GONE
        viewBinding.onboardExit.setOnClickListener {
            viewModel.saveOnBoardingState()
            launchHomeScreen()
        }
    }

    private fun launchHomeScreen() {
        val artStyleModel = ArtStyleModel(
            null,
            null,
            null
        )
        navigate(OnboardThreeFragmentDirections.actionOnboardThreeFragmentToHomeFragment(0, artStyleModel, false))
    }
}