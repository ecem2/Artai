package com.hc.artai.ui.onboard

import androidx.lifecycle.viewModelScope
import com.hc.artai.core.viewmodel.BaseViewModel
import com.hc.artai.data.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(
    private val preferences: Preferences
): BaseViewModel() {

    fun saveOnBoardingState() {
        viewModelScope.launch(Dispatchers.IO) {
            preferences.setFirstLaunch(false)
        }
    }

}