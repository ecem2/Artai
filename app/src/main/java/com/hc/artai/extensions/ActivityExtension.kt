package com.hc.artai.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.hc.artai.core.common.Util


fun Activity.redirectToPlayStore(storeUrl: String) {
    try {
        if (storeUrl.isNotBlank()) {
            val url: String = storeUrl.addHttp()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        }
    } catch (exception: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

fun String?.addHttp(): String {
    if (this == null) {
        return Util.EMPTY_STRING
    }
    return if (!this.startsWith("http://") && !this.startsWith("https://")) {
        "https://$this"
    } else {
        this
    }
}

fun ComponentActivity.activityResultLauncher(
    onResultOk: (resultData: Intent?) -> Unit,
    onResultCancel: ((resultData: Intent?) -> Unit)? = null
): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onResultOk.invoke(result.data)
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            onResultCancel?.invoke(result.data)
        }
    }
}

fun FragmentActivity.findNavHostFragment(navHostId: Int) =
    supportFragmentManager.findFragmentById(navHostId) as NavHostFragment



