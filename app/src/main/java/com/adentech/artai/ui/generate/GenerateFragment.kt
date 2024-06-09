package com.adentech.artai.ui.generate


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.adentech.artai.ArtaiApplication
import com.adentech.artai.R
import com.adentech.artai.core.common.ArgumentKey.REQUEST_MODEL
import com.adentech.artai.core.common.Resource
import com.adentech.artai.core.common.Status
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.data.model.RequestModel
import com.adentech.artai.data.model.output.OutputResponse
import com.adentech.artai.databinding.FragmentGenerateBinding
import com.adentech.artai.extensions.navigate
import com.adentech.artai.extensions.observe
import com.adentech.artai.extensions.parcelable
import com.adentech.artai.ui.home.HomeViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class GenerateFragment : BaseFragment<HomeViewModel, FragmentGenerateBinding>() {


    private var resultImage: String? = null
    var file: File? = null
    var width: Int = 768
    var height: Int = 768

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requiredPermissions: Array<String> = (arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ))
    private var isStorageImagePermitted = false
    private var isRequiredPermissionsGranted = false

    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_generate

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onInitDataBinding() {
        val translation = arguments?.parcelable<RequestModel>(REQUEST_MODEL)
        if (translation != null) {
            viewModel.getUrlForGeneration(
                translation.prompt,
                translation.style,
                translation.width,
                translation.height
            )
            observe(viewModel.urlForGeneration, ::getUrlForGeneration)
        }


        viewBinding.buttonShare.isEnabled = true
        viewBinding.buttonShare.isClickable = true
        viewBinding.apply {
            buttonShare.setOnClickListener {
                if (!resultImage.isNullOrBlank()) {
                    lifecycleScope.launch {
                        file = viewModel.downloadImage(resultImage!!, requireContext())
                        file?.let { it1 -> shareFile(it1) }
                    }
                }
            }

            btnRefresh.setOnClickListener {
                navigateHomeOrSubscription()
            }
        }
        setupLottie()

        viewBinding.ivBack.setOnClickListener {
            navigateHomeOrSubscription()
        }


        observe(isPermissionsGranted, ::getPermissionState)
        viewBinding.buttonDownload.setOnClickListener {
            if (!isStorageImagePermitted) {
                requiredPermissionStorageImages()
            } else {
                if (resultImage != null && resultImage != "") {
                    saveImageToGallery(resultImage!!)
                }
            }
        }

    }

    private fun navigateHomeOrSubscription() {
        val hasSubscription = ArtaiApplication.hasSubscription
        if (hasSubscription) {
            val artStyleModel = ArtStyleModel(null, null, null)
            navigate(
                GenerateFragmentDirections.actionGenerateFragmentToHomeFragment(
                    0,
                    artStyleModel,
                    false
                )
            )
        } else {
            navigate(GenerateFragmentDirections.actionGenerateFragmentToSubscriptionFragment())
        }
    }


    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext().applicationContext,
            "com.adentech.artai" + ".provider",
            file
        )

        val shareBody = getString(R.string.room_ai_share)
        val sharingIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareBody)
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/*"
        }
        val chooserIntent = Intent.createChooser(
            sharingIntent,
            getString(R.string.share_using)
        )
        if (sharingIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(chooserIntent)
        }
    }

    private fun getUrlForGeneration(resource: Resource<OutputResponse>) {

        viewModel.viewModelScope.launch {
            when (resource.status) {
                Status.SUCCESS -> {
                    viewModel.getResultImage(resource.data?.urls?.get.toString())
                    observe(viewModel.resultImage, ::getResultImage)
                }

                Status.LOADING -> {
                    Log.d("ecemmm", "Resource is in loading state")
                }

                Status.ERROR -> {
                    Log.d("ecemmm", "Resource encountered an error: ${resource.message}")
                }
            }
        }
    }


    private fun getResultImage(resource: Resource<String>) {
        when (resource.status) {
            Status.SUCCESS -> {

                val result = resource.data.toString()
                viewBinding.animationView.visibility = View.GONE
                viewBinding.ivGeneratedImage.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(result)
                    .into(viewBinding.ivGeneratedImage)
                resultImage = result
            }

            Status.LOADING -> {
                Log.d("ecemmm", "Resource is in loading state")
            }

            Status.ERROR -> {
                Log.e("ecemmm", "Error loading image: ${resource.message}")
            }
        }
    }

    private fun saveImageToGallery(imageUrl: String) {
        val fileName = "image.jpg"
        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setTitle(fileName)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requiredPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                requiredPermissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isStorageImagePermitted = true
            if (resultImage != null && resultImage != "") {
                saveImageToGallery(resultImage!!)
            }
        } else {
            requestPermissionLauncher.launch(requiredPermissions[0])
        }
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                isStorageImagePermitted = true
                if (resultImage != null && resultImage != "") {
                    saveImageToGallery(resultImage!!)
                }
            } else {
                isStorageImagePermitted = false
            }
        }

    private fun setupLottie() {
        viewBinding.apply {
            ivGeneratedImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
        }
    }

    private fun getPermissionState(isGranted: Boolean) {
        isRequiredPermissionsGranted = isGranted
    }


}


