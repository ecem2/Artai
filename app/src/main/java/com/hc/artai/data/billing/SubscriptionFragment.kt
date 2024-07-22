package com.hc.artai.data.billing



import com.hc.artai.R
import com.hc.artai.core.fragments.BaseFragment
import com.hc.artai.databinding.FragmentSubscriptionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment<SubscriptionViewModel, FragmentSubscriptionBinding>(){
        override fun viewModelClass() = SubscriptionViewModel::class.java

        override fun getResourceLayoutId() = R.layout.fragment_subscription

        override fun onInitDataBinding() {

            viewBinding.subExit.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()

            }

        }

    }
