package com.hc.artai.extensions

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hc.artai.core.common.Util
import java.util.Locale

fun Fragment.dimenToPx(@DimenRes dimen: Int): Int {
    return resources.dimenToPx(dimen)
}

fun Fragment.color(@ColorRes resource: Int): Int {
    return requireContext().color(resource)
}

fun Fragment.font(@FontRes resource: Int): Typeface? {
    return requireContext().font(resource)
}

fun Fragment.drawable(@DrawableRes resource: Int): Drawable? {
    return requireContext().drawable(resource)
}

fun Fragment.activityResultLauncher(
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

fun Fragment.initMenu(
    @MenuRes menuResourceId: Int,
    onMenuItemSelected: (menuItem: MenuItem) -> Boolean,
    onCreateMenu: ((menu: Menu) -> Unit)? = null,
) {
    activity?.addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(menuResourceId, menu)
            onCreateMenu?.invoke(menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return onMenuItemSelected.invoke(menuItem)
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
}

fun Fragment.showDialog(dialog: DialogFragment) {
    val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()

    fragmentTransaction?.addToBackStack(null)

    fragmentTransaction?.let {
        dialog.show(it, this.javaClass.simpleName)
    }
}

fun Fragment.openMap(latitude: String, longitude: String) {
    val uri = String.format(
        Locale.ENGLISH,
        "geo:%f,%f?z=17&q=%f,%f",
        latitude.replace(Util.COMMA, Util.POINT).toDoubleOrZero(),
        longitude.replace(Util.COMMA, Util.POINT).toDoubleOrZero(),
        latitude.replace(Util.COMMA, Util.POINT).toDoubleOrZero(),
        longitude.replace(Util.COMMA, Util.POINT).toDoubleOrZero()
    )

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    startActivity(intent)
}

fun Fragment.handleOnBackPressed(block: () -> Unit) {
    activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            block.invoke()
        }
    }
    )
}

fun Fragment.navigate(navDirections: NavDirections) {
    findNavController().apply {
        currentDestination?.getAction(navDirections.actionId)?.run {
            navigate(navDirections.actionId, navDirections.arguments)
        }
    }
}
fun Fragment.popBack(@IdRes destinationId: Int, inclusive: Boolean = false) {
    findNavController().popBackStack(destinationId, inclusive)
}

fun Fragment.popBack() {
    if (!findNavController().popBackStack()) {
        activity?.finish()
    }
}