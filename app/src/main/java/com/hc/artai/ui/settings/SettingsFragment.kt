package com.hc.artai.ui.settings


import android.view.View
import com.hc.artai.ArtaiApplication
import com.hc.artai.R
import com.hc.artai.core.common.Constants
import com.hc.artai.core.common.Constants.PRIVACY
import com.hc.artai.core.common.Constants.TERMS
import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.data.model.ArtStyleModel
import com.hc.artai.databinding.FragmentSettingsBinding
import com.hc.artai.extensions.navigate
import com.hc.artai.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: BaseFragment<MainViewModel, FragmentSettingsBinding>() {
    override fun viewModelClass() = MainViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_settings



    override fun onInitDataBinding() {
        viewBinding.ivBack.setOnClickListener {
            navigateHome()
        }
        if (ArtaiApplication.hasSubscription){
            viewBinding.clPremium.visibility = View.GONE
        }
        viewBinding.privacy.setOnClickListener {
            navigateWebView(PRIVACY)
        }
        viewBinding.terms.setOnClickListener {
            navigateWebView(TERMS)
        }
    }

    private fun navigateWebView(title: String){
        navigate(SettingsFragmentDirections.actionSettingsFragmentToWebViewFragment(title))
    }
    private fun navigateHome() {
        val artStyleModel = ArtStyleModel(null, null, null)
        navigate(
            SettingsFragmentDirections.actionSettingsFragmentToHomeFragment(
                0,
                artStyleModel,
                false
            )
        )
    }
}