package com.adentech.artai.ui.main


import com.adentech.artai.core.viewmodel.BaseViewModel
import com.adentech.artai.data.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val preferences: Preferences
) : BaseViewModel() {
}