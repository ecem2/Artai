package com.hc.artai.core.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.hc.artai.core.viewmodel.ArtBaseViewModel

abstract class ArtBaseVmActivity<VM : ArtBaseViewModel> : ArtBaseActivity() {

    protected lateinit var viewModel: VM

    abstract fun viewModelClass(): Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[viewModelClass()]
    }
}