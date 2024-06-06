package com.adentech.artai.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.adentech.artai.R
import com.adentech.artai.core.activities.BaseActivity
import com.adentech.artai.core.common.ArgumentKey
import com.adentech.artai.databinding.ActivityMainBinding
import com.adentech.artai.extensions.findNavHostFragment
import com.adentech.artai.ui.generate.GenerateFragment
import com.adentech.artai.ui.home.HomeFragment
import com.adentech.artai.ui.onboard.OnboardFragment

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    private lateinit var navController: NavController
    private var isSplashScreenActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition{
            isSplashScreenActive
        }
    }

    override fun viewDataBindingClass() = ActivityMainBinding::class.java

    override fun viewModelClass() = MainViewModel::class.java

    override fun onInitDataBinding() {
        isSplashScreenActive = true
        initGraph()

//        if (viewModel.preferences.getFirstLaunch()) {
//
//        }else{
//            navigateHome()
//        }
        //isSplashScreenActive = false
    }

//    private fun setupNavigation() {
////            if (viewModel.preferences.getFirstLaunch()) {
////                isSplashScreenActive = true
////                onboardScreen()
////            } else {
////                homeScreen()
////                finish()
////            }
//        // 1. Adimda bos bir splashFragment yarat. icinde .getFirstLaunch() i kontrol et
//        // 2. ilk acilista onboard_nav_graph
//        // 3. onboard gecilmisse bottomnavigationli nav graph
//        val navHostFragment = findNavHostFragment(R.id.nav_host_container)
//        navController = navHostFragment.navController
//        val inflater = navHostFragment.navController.navInflater
//        val graph = inflater.inflate(R.navigation.home_nav_graph)
//
//        if (viewModel.preferences.getFirstLaunch()) {
//            graph.setStartDestination(R.id.onboard_nav_graph)
//
//            //graph.setStartDestination(R.id.onboard_graph)
//            graph.setStartDestination(R.id.splashFragment)
//        } else {
//            graph.setStartDestination(R.id.home_nav_graph)
//           // initGraph()
//        viewBinding.bottomNav.setOnApplyWindowInsetsListener(null)
//        }
//
//        navController.setGraph(graph, intent.extras)
//
//        viewBinding.apply {
//            bottomNav.setupWithNavController(navController)
//            NavigationUI.setupWithNavController(bottomNav, navController)
//        }
//        isSplashScreenActive = false
//
//    }

    private fun initGraph() {
        isSplashScreenActive = false
        val navHostFragment = findNavHostFragment(R.id.nav_host_container)
        navController = navHostFragment.navController

        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.home_nav_graph)

        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)
    }

    private fun popBack(@IdRes destinationId: Int, inclusive: Boolean = false) {
        navController.popBackStack(destinationId, inclusive)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        fun newIntent(context: Context, returnScreen: String? = null) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(ArgumentKey.MAIN_SCREEN, returnScreen)
            }
    }
}