package com.adentech.artai.data.billing


import com.adentech.artai.R
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.databinding.FragmentSubscriptionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment<SubscriptionViewModel, FragmentSubscriptionBinding>(){
        override fun viewModelClass() = SubscriptionViewModel::class.java

        override fun getResourceLayoutId() = R.layout.fragment_subscription

        override fun onInitDataBinding() {


        }

    }
