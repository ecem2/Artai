package com.hc.artai.ui.watch

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import com.hc.artai.R
import com.hc.artai.core.common.Constants.ADS_TO_SHOW
import com.hc.artai.core.common.Constants.AD_WATCH_COUNT_KEY
import com.hc.artai.core.common.Constants.HAS_SUBSCRIPTION_KEY
import com.hc.artai.core.common.Constants.INTERSTITIAL_ID
import com.hc.artai.core.common.Constants.REQUIRED_ADS_COUNT
import com.hc.artai.core.common.Constants.TEMPORARY_PREMIUM_KEY
import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.data.model.ArtStyleModel
import com.hc.artai.databinding.FragmentWatchAdsBinding
import com.hc.artai.extensions.navigate
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchAdsFragment : BaseFragment<WatchViewModel, FragmentWatchAdsBinding>() {

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var adShowCounter = 0
    override fun viewModelClass() = WatchViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_watch_ads

    override fun onInitDataBinding() {
        sharedPreferences =
            requireContext().getSharedPreferences("ads_prefs", Activity.MODE_PRIVATE)
        viewBinding.buttonRemoveLimits.setOnClickListener {
            navigateToHomeFragmentWithPremiumAccess()
        }

        viewBinding.buttonWatchAds.setOnClickListener {
            adShowCounter = 0

            MobileAds.initialize(requireContext()) {
                loadInterAd()
            }
            loadAndShowAds()
        }

        checkAdWatchCount()
    }

    private fun getWatchAds(): Int {
        return sharedPreferences.getInt(AD_WATCH_COUNT_KEY, 0)
    }


    private fun loadAndShowAds() {
        if (adShowCounter < ADS_TO_SHOW) {
            loadInterAd()
        }
    }

    private fun navigateToHomeFragmentWithPremiumAccess() {
        val artStyleModel = ArtStyleModel(
            null,
            null,
            null
        )
        navigate(
            WatchAdsFragmentDirections.actionWatchAdsFragmentToHomeFragment(
                0,
                artStyleModel,
                true
            )
        )
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
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    showInterAds()
                }
            })
    }

    private fun showInterAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    mInterstitialAd = null
                    incrementAdWatchCount()
                    adShowCounter++

                    if (adShowCounter < ADS_TO_SHOW) {
                        loadAndShowAds()
                    } else {
                        navigateToHomeFragmentWithPremiumAccess()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    mInterstitialAd = null
                }
            }

            requireActivity().let { activity ->
                mInterstitialAd?.show(activity)
            }

        }
    }

    private fun incrementAdWatchCount() {
        val adWatchCount = getWatchAds() + 1
        sharedPreferences.edit().putInt(AD_WATCH_COUNT_KEY, adWatchCount).apply()
        Log.d("AdWatchCount", "Ad watch count: $adWatchCount")

        if (adWatchCount >= REQUIRED_ADS_COUNT) {
            grantTemporaryPremiumAccess()
        }
    }

    private fun grantTemporaryPremiumAccess() {
        sharedPreferences.edit().putBoolean(HAS_SUBSCRIPTION_KEY, true).apply()
        sharedPreferences.edit().putBoolean(TEMPORARY_PREMIUM_KEY, true).apply()
    }


    private fun enablePremiumFeature() {
        sharedPreferences.edit().putBoolean(HAS_SUBSCRIPTION_KEY, true).apply()
        sharedPreferences.edit().putInt(AD_WATCH_COUNT_KEY, 0).apply()
    }

    private fun disablePremiumFeature() {
        sharedPreferences.edit().putBoolean(HAS_SUBSCRIPTION_KEY, false).apply()
    }

    private fun checkAdWatchCount() {
        val adWatchCount = getWatchAds()
        val hasSubscription = sharedPreferences.getBoolean(HAS_SUBSCRIPTION_KEY, false)

        if (adWatchCount >= REQUIRED_ADS_COUNT && !hasSubscription) {
            enablePremiumFeature()
        } else {
            disablePremiumFeature()
        }
    }
}