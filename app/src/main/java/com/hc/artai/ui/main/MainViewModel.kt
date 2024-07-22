package com.hc.artai.ui.main


import com.hc.artai.core.viewmodel.BaseViewModel
import com.hc.artai.data.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val preferences: Preferences
) : BaseViewModel() {
}