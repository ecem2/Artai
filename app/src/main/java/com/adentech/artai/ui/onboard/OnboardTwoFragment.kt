package com.adentech.artai.ui.onboard

import android.view.View
import androidx.navigation.fragment.findNavController
import com.adentech.artai.R
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.databinding.FragmentOnboardTwoBinding
import com.adentech.artai.extensions.handleOnBackPressed
import com.adentech.artai.extensions.navigate
import com.adentech.artai.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnboardTwoFragment : BaseFragment<OnboardViewModel, FragmentOnboardTwoBinding>() {

    override fun viewModelClass() = OnboardViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_onboard_two

    override fun onInitDataBinding() {
        handleOnBackPressed {
            requireActivity().finish()
        }
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
        navBar.visibility = View.GONE
        viewBinding.nextTwo.setOnClickListener {
            navigate(OnboardTwoFragmentDirections.actionOnboardTwoFragmentToOnboardThreeFragment())
        }
    }

}