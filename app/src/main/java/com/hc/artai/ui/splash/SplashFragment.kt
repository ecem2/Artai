package com.hc.artai.ui.splash

import com.hc.artai.R
import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.data.model.ArtStyleModel
import com.hc.artai.databinding.FragmentSplashBinding
import com.hc.artai.extensions.navigate
import com.hc.artai.ui.home.HomeViewModel
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
            val artStyleModel = ArtStyleModel(
                null,
                null,
                null
            )
            navigate(
                SplashFragmentDirections.actionSplashFragmentToHomeFragment(
                    0,
                    artStyleModel,
                    false
                )
            )

        }
    }

    override fun viewModelClass() = HomeViewModel::class.java
    override fun getResourceLayoutId() = R.layout.fragment_splash
}