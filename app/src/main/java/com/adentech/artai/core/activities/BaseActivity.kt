package com.adentech.artai.core.activities

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.adentech.artai.core.common.ViewBindingUtil
import com.adentech.artai.core.viewmodel.BaseViewModel
import com.adentech.artai.databinding.ActivityBaseBinding
import com.adentech.artai.extensions.setupActionBar

abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> :
    ArtBaseVmDbActivity<VM, DB>() {

    private lateinit var baseViewBinding: ViewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setupActionBar(this@BaseActivity)
    }

    override fun setResourceViewBinding(): View {
        baseViewBinding =
            ViewBindingUtil.inflate<ActivityBaseBinding>(layoutInflater)
        viewBinding = ViewBindingUtil.inflate(
            layoutInflater,
            (baseViewBinding as ActivityBaseBinding).baseContentFrame,
            true,
            viewDataBindingClass()
        )
        viewBinding.lifecycleOwner = this
        return baseViewBinding.root
    }
}