package com.adentech.artai.ui.generate

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
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
import com.adentech.artai.data.remote.ApiService
import com.adentech.artai.databinding.DialogLoadingProgressBinding
import com.adentech.artai.databinding.FragmentGenerateBinding
import com.adentech.artai.extensions.observe
import com.adentech.artai.extensions.parcelable
import com.adentech.artai.extensions.popBack
import com.adentech.artai.ui.home.HomeViewModel
import com.bumptech.glide.Glide
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileDescriptor
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class GenerateFragment : BaseFragment<HomeViewModel, FragmentGenerateBinding>() {

    private var generatedImage: GenerateModel? = null
    private var outputResponse: OutputResponse? = null
    private var client: OkHttpClient = OkHttpClient()
    private val imageList: ArrayList<GenerateModel> = ArrayList()
    private var output: String? = null
    private var isImageGenerated: Boolean = false
    private var imagesList: ArrayList<String> = ArrayList()
    private var resultImage: String? = null
    private val executor: ExecutorService = Executors.newFixedThreadPool(1)
    private lateinit var dialogBinding: DialogLoadingProgressBinding
    private lateinit var mDialog: Dialog
    var file: File? = null
    private val updateDownloadProgress = 1

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requiredPermissions: Array<String> = (arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                       ,android.Manifest.permission.READ_EXTERNAL_STORAGE
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
        Log.d(
            "fatosss",
            "Prompt: ${translation?.prompt}, Style: ${translation?.style}, Size: ${translation?.size}"
        )

        if (translation != null) {
            Log.e("fatosss", "translation ${translation}")

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
                        Log.d("ecemmm", "$resultImage")
                        Log.d("ecemmm", "$file")
                    }
                }
            }
        }


        viewBinding.apply {
            buttonEdit.isEnabled = false
            buttonEdit.isClickable = false
            //buttonShare.isEnabled = false
           // buttonShare.isClickable = false
        }
        setupLottie()

        viewBinding.ivBack.setOnClickListener {
            popBack()
        }


        observe(isPermissionsGranted, ::getPermissionState)
        viewBinding.buttonDownload.setOnClickListener {
            Log.d("ecemmm","Tiklandiiii......")
            if (!isStorageImagePermitted) {
                requiredPermissionStorageImages()
                Log.d("ecemmm","Izin verilmedi......")
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
        Log.d("ecemmm", "$uri")


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
            Log.d("ecemmm", "Dosya secenekleri acildiiii")

        } else {
            Log.d("ecemmm", "Dosya secenekleri acilmadiii")
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
                Log.d("fatosss", "Result: $result")
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

        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
                Log.d("ecemmm", "İzinler verildi")
                if (resultImage != null && resultImage != "") {
                    saveImageToGallery(resultImage!!)
                }
            } else {
                isStorageImagePermitted = false
                Log.d("ecemmm", "İzinler verilmedi")
            }
        }
    private fun setupLottie() {
        viewBinding.apply {
            //buttonDownload.visibility = View.GONE
            ivGeneratedImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
            animationView.playAnimation()
        }
    }
    private fun getPermissionState(isGranted: Boolean) {
        isRequiredPermissionsGranted = isGranted
    }

    private fun getSizeList(constraintLayout: ConstraintLayout) {
        itemList.clear()
        when (constraintLayout) {
            viewBinding.bgImage -> {
                itemList.add(SizeModel(icon = "", size = "1024x1024", isSelected = true))
                selectedSize = "1024x1024"
            }

            viewBinding.bgImage -> {
                itemList.add(SizeModel(icon = "", size = "1024x1792", isSelected = true))
                selectedSize = "1024x1792"
            }

            viewBinding.bgImage -> {
                itemList.add(SizeModel(icon = "", size = "1792x1024", isSelected = true))
                selectedSize = "1792x1024"
            }
        }
    }

//    private fun enabledButtons() {
//        viewBinding.apply {
//            buttonEdit.isEnabled = true
//            buttonEdit.isClickable = true
//            buttonShare.isEnabled = true
//            buttonShare.isClickable = true
//            animationView.cancelAnimation()
//            animationView.visibility = View.GONE
//            buttonDownload.visibility = View.VISIBLE
//            ivGeneratedImage.visibility = View.VISIBLE
//
//        }
    // }


    private fun onArtItemClicked(artStyleModel: GenerateModel, i: Int) {
        Log.d("salimmm", "artStyleModel $artStyleModel")
    }

}


