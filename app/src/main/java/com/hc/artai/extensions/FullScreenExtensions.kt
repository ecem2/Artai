@file:Suppress("DEPRECATION")

package com.hc.artai.extensions

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding


@RequiresApi(Build.VERSION_CODES.R)
fun setupActionBar(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.window.setDecorFitsSystemWindows(false)
        activity.window.insetsController?.let { controller ->
            controller.hide(
                WindowInsets.Type.statusBars() or
                        WindowInsets.Type.navigationBars()
            )
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        val decorView: View = activity.window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LOW_PROFILE)


    }
}