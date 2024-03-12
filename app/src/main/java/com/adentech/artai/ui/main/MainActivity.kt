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
       // isSplashScreenActive = true
        setupNavigation()
        //isSplashScreenActive = false
    }

    private fun setupNavigation() {

        // 1. Adimda bos bir splashFragment yarat. icinde .getFirstLaunch() i kontrol et
        // 2. ilk acilista onboard_nav_graph
        // 3. onboard gecilmisse bottomnavigationli nav graph
        val navHostFragment = findNavHostFragment(R.id.nav_host_container)
        navController = navHostFragment.navController
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.main_nav_graph)

        if (viewModel.preferences.getFirstLaunch()) {
            graph.setStartDestination(R.id.onboard_graph)
        } else {
            graph.setStartDestination(R.id.home_nav_graph)
            initGraph()
            viewBinding.bottomNav.setOnApplyWindowInsetsListener(null)
        }

        navController.setGraph(graph, intent.extras)

        viewBinding.apply {
            bottomNav.setupWithNavController(navController)
            NavigationUI.setupWithNavController(bottomNav, navController)
        }
    }

    private fun initGraph() {
        viewBinding.bottomNav.setupWithNavController(navController)
        viewBinding.bottomNav.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.home_nav_graph -> {
                    returnHome()
                }

            }
        }

        viewBinding.bottomNav.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
        }
    }

    private fun popBack(@IdRes destinationId: Int, inclusive: Boolean = false) {
        navController.popBackStack(destinationId, inclusive)
    }

    private fun returnHome() {
        popBack(R.id.homeFragment)
    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when (intent?.getStringExtra(ArgumentKey.MAIN_SCREEN)) {
            MainScreen.HOME -> {
                viewBinding.bottomNav.selectedItemId = R.id.home_nav_graph
                popBack(R.id.homeFragment)
            }

        }
    }

//    override fun onResume() {
//        super.onResume()
//        // Before setting full screen flags, we must wait a bit to let UI settle; otherwise, we may
//        // be trying to set app to immersive mode before it's ready and the flags do not stick
//        viewBinding.navHostContainer.postDelayed({
//            hideSystemUI()
//        }, IMMERSIVE_FLAG_TIMEOUT)
//    }

    /** When key down event is triggered, relay it via local broadcast so fragments can handle it */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val KEY_EVENT_ACTION = "key_event_action"
        const val KEY_EVENT_EXTRA = "key_event_extra"
        private const val IMMERSIVE_FLAG_TIMEOUT = 500L
        fun newIntent(context: Context, returnScreen: String? = null) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(ArgumentKey.MAIN_SCREEN, returnScreen)
            }
    }
}