package com.adentech.artai.ui.splash

import com.adentech.artai.R
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.databinding.FragmentSplashBinding
import com.adentech.artai.extensions.navigate
import com.adentech.artai.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<HomeViewModel, FragmentSplashBinding>() {

    override fun onInitDataBinding() {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        if (viewModel.preferences.getFirstLaunch()) {
            navigate(SplashFragmentDirections.actionSplashFragmentToOnboardFragment())
        } else {
            navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment(0, null))
        }
    }

    override fun viewModelClass() = HomeViewModel::class.java
    override fun getResourceLayoutId() = R.layout.fragment_splash
}