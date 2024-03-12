package com.adentech.artai.data.billing

import com.adentech.artai.core.viewmodel.BaseViewModel
import com.adentech.artai.data.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    val preferences: Preferences
) : BaseViewModel() {
}