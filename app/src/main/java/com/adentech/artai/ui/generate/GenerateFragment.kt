package com.adentech.artai.ui.generate

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.adentech.artai.R
import com.adentech.artai.core.common.ArgumentKey.REQUEST_MODEL
import com.adentech.artai.core.common.Resource
import com.adentech.artai.core.common.Status
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.GenerateModel
import com.adentech.artai.data.model.RequestModel
import com.adentech.artai.data.model.SizeModel
import com.adentech.artai.data.model.output.OutputResponse
import com.adentech.artai.databinding.DialogLoadingProgressBinding
import com.adentech.artai.databinding.FragmentGenerateBinding
import com.adentech.artai.extensions.navigate
import com.adentech.artai.extensions.observe
import com.adentech.artai.extensions.parcelable
import com.adentech.artai.extensions.popBack
import com.adentech.artai.ui.home.HomeViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class GenerateFragment : BaseFragment<HomeViewModel, FragmentGenerateBinding>() {


    private var resultImage: String? = null
    private lateinit var dialogBinding: DialogLoadingProgressBinding
    private lateinit var mDialog: Dialog
    var file: File? = null
    private val updateDownloadProgress = 1

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requiredPermissions: Array<String> = (arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ))
    private var isStorageImagePermitted = false
    private var isRequiredPermissionsGranted = false
    val itemList: ArrayList<SizeModel> = ArrayList()
    private lateinit var selectedSize: String
    private val mainHandler: Handler = Handler(Looper.getMainLooper()) { msg ->
        if (msg.what == updateDownloadProgress) {
            val downloadProgress: Int = msg.arg1
            requireActivity().runOnUiThread {
                updateProgress(downloadProgress)
            }
        }
        true
    }

    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_generate

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onInitDataBinding() {
        val translation = arguments?.parcelable<RequestModel>(REQUEST_MODEL)
        if (translation != null) {
            adjustAspectRatio(translation.size)
            viewModel.getUrlForGeneration(translation.prompt, translation.style, translation.size)
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
                navigate(GenerateFragmentDirections.actionGenerateFragmentToHomeFragment(0, null))
            }
        }
        setupLottie()

        viewBinding.ivBack.setOnClickListener {
            popBack()
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
        getSizeList(viewBinding.bgImage)

    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress(progress: Int) {
        if (mDialog.isShowing) {
            requireActivity().runOnUiThread {
                dialogBinding.tvProgress.text = "% $progress"
                if (progress == 100) {
                    dialogBinding.buttonDone.visibility = View.VISIBLE
                    dialogBinding.tvLoading.text = getString(R.string.successfully_saved)
                }
            }
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
                Log.d("fatosss", "Resource is in loading state")
            }

            Status.ERROR -> {
                Log.e("fatosss", "Error loading image: ${resource.message}")
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
            //buttonDownload.visibility = View.GONE
            ivGeneratedImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
        }
    }

    private fun getPermissionState(isGranted: Boolean) {
        isRequiredPermissionsGranted = isGranted
    }

    private fun getSizeList(constraintLayout: ConstraintLayout) {
        itemList.clear()
        when (constraintLayout) {
            viewBinding.bgImage -> {
                itemList.add(SizeModel(icon = "", size = "768x768", isSelected = true))
                selectedSize = "768x768"
            }

            viewBinding.bgImage -> {
                itemList.add(SizeModel(icon = "", size = "768x1024", isSelected = true))
                selectedSize = "768x1024"
            }

            viewBinding.bgImage -> {
                itemList.add(SizeModel(icon = "", size = "1024x768", isSelected = true))
                selectedSize = "1024x768"
            }
        }
    }
    private fun adjustAspectRatio(size: String) {
        val ratio = when (size) {
            "768x768" -> "1:1"
            "768x1024" -> "3:4"
            "1024x768" -> "4:3"
            else -> "1:1"
        }
        Log.d("ecooo", "Adjusting aspect ratio to $ratio for size $size")
        setAspectRatio(ratio)
    }
    private fun setAspectRatio(ratio: String) {
        val params = viewBinding.ivGeneratedImage.layoutParams as ConstraintLayout.LayoutParams
        params.dimensionRatio = ratio
        viewBinding.ivGeneratedImage.layoutParams = params
        viewBinding.ivGeneratedImage.requestLayout()
        Log.d("ecooo", "Setting dimension ratio to $ratio")
    }
}


