package com.hc.artai.core.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import com.hc.artai.core.common.ViewBindingUtil
import com.hc.artai.core.viewmodel.BaseViewModel
import com.hc.artai.databinding.ActivityBaseBinding
import com.hc.artai.extensions.setupActionBar

abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> :
    ArtBaseVmDbActivity<VM, DB>() {

    private lateinit var baseViewBinding: ViewDataBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar(this@BaseActivity)
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