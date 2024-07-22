package com.hc.artai.ui.watch


import com.hc.artai.core.viewmodel.BaseViewModel
import com.hc.artai.data.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WatchViewModel @Inject constructor(
    val preferences: Preferences
) : BaseViewModel() {
}