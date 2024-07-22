package com.hc.artai.core.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hc.artai.core.viewmodel.ArtBaseViewModel

abstract class ArtBaseVMFragment<VM : ArtBaseViewModel> :
    ArtBaseFragment() {

    protected lateinit var viewModel: VM

    abstract fun viewModelClass(): Class<VM>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = createViewModel()
    }

    private fun createViewModel(): VM {
        return ViewModelProvider(this)[viewModelClass()]
    }
}