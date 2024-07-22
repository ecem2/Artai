package com.hc.artai.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.hc.artai.R
import com.hc.artai.core.activities.BaseActivity
import com.hc.artai.core.common.ArgumentKey
import com.hc.artai.databinding.ActivityMainBinding
import com.hc.artai.extensions.findNavHostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    private lateinit var navController: NavController
    private var isSplashScreenActive = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
//        val navController = findNavController(R.id.fragment_container)
//        setupActionBarWithNavController(navController)
        splashScreen.setKeepOnScreenCondition {
            isSplashScreenActive
        }
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)


    }


    override fun viewDataBindingClass() = ActivityMainBinding::class.java

    override fun viewModelClass() = MainViewModel::class.java

    override fun onInitDataBinding() {
        isSplashScreenActive = true
        initGraph()

    }

    private fun initGraph() {
        isSplashScreenActive = false
        val navHostFragment = findNavHostFragment(R.id.nav_host_container) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController
        }

        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.home_nav_graph)

        val navController = navHostFragment?.navController
        navController?.setGraph(graph, intent.extras)
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