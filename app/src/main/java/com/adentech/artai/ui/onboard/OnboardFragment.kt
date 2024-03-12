package com.adentech.artai.ui.onboard

import android.view.View
import com.adentech.artai.R
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.databinding.FragmentOnboardBinding
import com.adentech.artai.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnboardFragment : BaseFragment<OnboardViewModel, FragmentOnboardBinding>() {

    override fun viewModelClass() = OnboardViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_onboard

    override fun onInitDataBinding() {
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
        navBar.visibility = View.GONE
        viewBinding.buttonStart.setOnClickListener {
            viewModel.saveOnBoardingState()
            launchHomeScreen()
        }
    }

    private fun launchHomeScreen() {
        startActivity(MainActivity.newIntent(requireContext())).also {
            requireActivity().finish()
        }
    }
}