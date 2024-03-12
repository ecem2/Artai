package com.adentech.artai.ui.watch


import com.adentech.artai.core.viewmodel.BaseViewModel
import com.adentech.artai.data.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WatchViewModel @Inject constructor(
    val preferences: Preferences
) : BaseViewModel() {
}