package com.hc.artai.data.billing


import com.hc.artai.core.viewmodel.BaseViewModel
import com.hc.artai.data.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    val preferences: Preferences
) : BaseViewModel() {
}