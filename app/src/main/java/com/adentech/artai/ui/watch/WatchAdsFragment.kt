package com.adentech.artai.ui.watch

import android.app.Activity
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences
    private val AD_WATCH_COUNT_KEY = "ad_watch_count"
    private val REQUIRED_ADS_COUNT = 5
    private val HAS_SUBSCRIPTION_KEY = "has_subscription"

    override fun viewModelClass() = WatchViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_watch_ads

    override fun onInitDataBinding() {
        sharedPreferences = requireContext().getSharedPreferences("ads_prefs", Activity.MODE_PRIVATE)
        
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
        checkAdWatchCount()

    }
    private fun updatePremiumStatus() {
        val adWatchCount = sharedPreferences.getInt(AD_WATCH_COUNT_KEY, 0)
        if (adWatchCount >= REQUIRED_ADS_COUNT) {
            sharedPreferences.edit().putInt(AD_WATCH_COUNT_KEY, 0).apply()
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
                    // todo error caseini yonet
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
                    incrementAdWatchCount()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    mInterstitialAd = null
                    // todo error caseini yonet
                }
            }
            requireActivity().let { activity ->
                mInterstitialAd?.show(activity)
            }

        } else {
            // todo error caseini yonet
        }
    }
    private fun incrementAdWatchCount() {
        val adWatchCount = sharedPreferences.getInt(AD_WATCH_COUNT_KEY, 0) + 1
        sharedPreferences.edit().putInt(AD_WATCH_COUNT_KEY, adWatchCount).apply()
        Log.d("AdWatchCount", "Ad watch count: $adWatchCount")

        if (adWatchCount >= REQUIRED_ADS_COUNT) {
            Log.d("AdWatchCount", "User can now use the premium feature")
            enablePremiumFeature()
        }
    }
    private fun enablePremiumFeature() {
        sharedPreferences.edit().putBoolean(HAS_SUBSCRIPTION_KEY, true).apply()
        sharedPreferences.edit().putInt(AD_WATCH_COUNT_KEY, 0).apply()
    }

    private fun disablePremiumFeature() {
        sharedPreferences.edit().putBoolean(HAS_SUBSCRIPTION_KEY, false).apply()
    }
    private fun checkAdWatchCount() {
        val adWatchCount = sharedPreferences.getInt(AD_WATCH_COUNT_KEY, 0)
        val hasSubscription = sharedPreferences.getBoolean(HAS_SUBSCRIPTION_KEY, false)

        if (adWatchCount >= REQUIRED_ADS_COUNT && !hasSubscription) {
            enablePremiumFeature()
        } else if (hasSubscription) {

        } else {
            disablePremiumFeature()
        }
    }
    private fun usePremiumFeature() {
        // Logic for using the premium feature goes here

        // After using the premium feature, disable it again
        disablePremiumFeature()
        // Reset the ad watch count to 0 for the next cycle
        sharedPreferences.edit().putInt(AD_WATCH_COUNT_KEY, 0).apply()
    }
}