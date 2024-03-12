package com.adentech.artai.core.fragments


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adentech.artai.R
import com.adentech.artai.core.viewmodel.BaseViewModel
import com.adentech.artai.databinding.DialogPermissionBinding


abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> :
    ArtBaseVmDbFragment<VM, DB>() {

    var dialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onInitDataBinding()
    }

    abstract fun onInitDataBinding()

    fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, viewBinding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    fun showProgress() {
        dialog?.show()
    }

    fun hideProgress() {
        dialog?.dismiss()
    }

    private val _isPermissionsGranted: MutableLiveData<Boolean> = MutableLiveData()
    val isPermissionsGranted: LiveData<Boolean> = _isPermissionsGranted

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkMediaPermission()
        } else {
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkMediaPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED -> {
                _isPermissionsGranted.postValue(true)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    _isPermissionsGranted.postValue(false)
                    showPermissionDialog()
                }
            }

            else -> {
                mediaPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }

    private val writePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            _isPermissionsGranted.postValue(true)
        } else {
            _isPermissionsGranted.postValue(false)
            showPermissionDialog()
        }
    }

    private val mediaPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            _isPermissionsGranted.postValue(true)
        } else {
            _isPermissionsGranted.postValue(false)
            showPermissionDialog()
        }
    }


    private fun showPermissionDialog() {
        val dialogBuilder = Dialog(requireContext(), R.style.CustomDialog)
        val dialogBinding = DialogPermissionBinding.inflate(layoutInflater)
        dialogBuilder.setContentView(dialogBinding.root)
        dialogBuilder.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialogBinding.apply {
            allowButton.setOnClickListener {
                checkPermissions()
                dialogBuilder.cancel()
            }

            buttonSettings.setOnClickListener {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }

            cancelButton.setOnClickListener {
                dialogBuilder.cancel()
            }
        }
        dialogBuilder.show()
    }
}