package com.hc.artai.ui.webview


import android.util.Log
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.hc.artai.R
import com.hc.artai.core.common.Constants.HELP
import com.hc.artai.core.common.Constants.HELP_AND_SUPPORT_LINK
import com.hc.artai.core.common.Constants.PRIVACY
import com.hc.artai.core.common.Constants.PRIVACY_POLICY_LINK
import com.hc.artai.core.common.Constants.TERMS
import com.hc.artai.core.common.Constants.TERMS_OF_USE_LINK
import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.databinding.FragmentWebViewBinding
import com.hc.artai.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : BaseFragment<MainViewModel, FragmentWebViewBinding>() {


    private val webNavArgs: WebViewFragmentArgs by navArgs()

    override fun viewModelClass() = MainViewModel::class.java
    override fun getResourceLayoutId() = R.layout.fragment_web_view


    override fun onInitDataBinding() {
        val title = webNavArgs.title
        Log.d("ecoo", "Received title: $title")

        viewBinding.webView.webViewClient = WebViewClient()
        val url: String = when (title) {
            PRIVACY -> PRIVACY_POLICY_LINK
            TERMS -> TERMS_OF_USE_LINK
            HELP -> HELP_AND_SUPPORT_LINK
            else -> HELP_AND_SUPPORT_LINK
        }
        viewBinding.webView.loadUrl(url)
        viewBinding.webView.settings.javaScriptEnabled = true
        viewBinding.webView.settings.setSupportZoom(true)

    }
}