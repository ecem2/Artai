package com.adentech.artai.ui.watch

import android.app.Activity
import android.util.Log
import com.adentech.artai.R
import com.adentech.artai.core.common.Constants.INTERSTITIAL_ID
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.billing.SubscriptionFragment
import com.adentech.artai.databinding.FragmentWatchAdsBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import java.util.Arrays

@AndroidEntryPoint
class WatchAdsFragment : BaseFragment<WatchViewModel, FragmentWatchAdsBinding>(){

    private var mInterstitialAd: InterstitialAd? = null

    override fun viewModelClass() = WatchViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_watch_ads

    override fun onInitDataBinding() {
        MobileAds.initialize(requireContext()) {
            loadInterAd()
        }
        viewBinding.buttonRemoveLimits.setOnClickListener {
            navigateSubscriptionFragment()
        }

        viewBinding.buttonWatchAds.setOnClickListener {
                loadInterAd()
                showInterAds()

        }

    }

    private fun navigateSubscriptionFragment(){
        val transaction = childFragmentManager.beginTransaction()
        val subscriptionFragment = SubscriptionFragment()
        transaction.add(R.id.fragment_container, subscriptionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    Log.e("InterstitialAd", "Failed to load ad: $adError")
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Log.d("InterstitialAd", "Ad loaded successfully")
                }
            })
    }
    private fun showInterAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    mInterstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }
            }
            requireActivity().let { activity ->
                mInterstitialAd?.show(activity)
            }

        }
    }
}