package com.adentech.artai.ui.generate

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.adentech.artai.R
import com.adentech.artai.core.common.ArgumentKey
import com.adentech.artai.core.common.ArgumentKey.REQUEST_MODEL
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.GenerateModel
import com.adentech.artai.data.model.RequestModel
import com.adentech.artai.data.model.SizeModel
import com.adentech.artai.data.remote.ApiService
import com.adentech.artai.databinding.FragmentGenerateBinding
import com.adentech.artai.extensions.makePostRequest
import com.adentech.artai.extensions.observe
import com.adentech.artai.extensions.parcelable
import com.adentech.artai.extensions.popBack
import com.adentech.artai.ui.home.HomeViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class GenerateFragment : BaseFragment<HomeViewModel, FragmentGenerateBinding>() {
    private var generatedImage: GenerateModel? = null
    private val args: GenerateFragmentArgs by navArgs()
    private var client: OkHttpClient = OkHttpClient()
    private val imageList: ArrayList<GenerateModel> = ArrayList()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requiredPermissions: Array<String> = arrayOf(
        android.Manifest.permission.READ_MEDIA_IMAGES
    )
    private var isStorageImagePermitted = false
    private var isRequiredPermissionsGranted = false
    val itemList: ArrayList<SizeModel> = ArrayList()
    private lateinit var selectedSize: String


    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_generate

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onInitDataBinding() {
        viewBinding.apply {
            buttonEdit.isEnabled = false
            buttonEdit.isClickable = false
            buttonShare.isEnabled = false
            buttonShare.isClickable = false
        }
        setupLottie()
       // setupArtStyles()
        viewBinding.ivBack.setOnClickListener {
            popBack()
        }


        observe(isPermissionsGranted, ::getPermissionState)
        viewBinding.buttonDownload.setOnClickListener {
            if (!isStorageImagePermitted) {
                requiredPermissionStorageImages()
            } else {
                if (imageList.isNotEmpty()) {
                    downloadImage(imageList[0].image)
                }
            }
        }
        getSizeList(viewBinding.bgImage)
        val translation = arguments?.parcelable<RequestModel>(REQUEST_MODEL)
        if (translation != null) {
            Log.e("aaeeaa", "translation ${translation}")
            if (generatedImage == null) {
                sendRequest(
                    translation.prompt,
                    translation.art,
                    translation.size
                )
            }
        }
//        viewBinding.buttonEdit.setOnClickListener {
//            Log.d("aassaa", "Edit butonuna tıklandı.")
//            sendRequest(
//                args.requestModel.prompt,
//                args.requestModel.count,
//                args.requestModel.size
//            )
//
//            val action =
//                GenerateFragmentDirections.actionGenerateFragmentToEditPhotoFragment()
//            try {
//                findNavController().navigate(action)
//            } catch (e: Exception) {
//            }
//        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requiredPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                requiredPermissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isStorageImagePermitted = true
            if (imageList.isNotEmpty()) {
                downloadImage(imageList[0].image)
            }
        } else {
            requestPermissionLauncher.launch(requiredPermissions[0])
        }
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                isStorageImagePermitted = true
                if (imageList.isNotEmpty()) {
                    downloadImage(imageList[0].image)
                }
            } else {
                isStorageImagePermitted = false
            }
        }

    private fun writeFileToStorage(body: ResponseBody): Boolean {
        val nameOfFile = ""
        var location: File? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                location = File(MediaStore.Downloads.EXTERNAL_CONTENT_URI.toString(), nameOfFile)
                if (location.exists()) {
                    location.delete()
                }

                val values: ContentValues = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, nameOfFile)
                values.put(MediaStore.Images.Media.DISPLAY_NAME, nameOfFile)
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                var uri: Uri? = null
                uri = requireContext().contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                )
                val descriptor: ParcelFileDescriptor? =
                    uri?.let { requireContext().contentResolver.openFileDescriptor(it, "w") }
                val fileDescriptor: FileDescriptor? = descriptor?.fileDescriptor
                val inputStream: InputStream = body.byteStream()
                val fileReader: ByteArray = byteArrayOf(4096.toByte())
                val fileSize: Long = body.contentLength()
                var fileSizeDownloaded = 0
                val outputStream: OutputStream = FileOutputStream(fileDescriptor)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read
                    Log.d("salimmm", "file download size: $fileSizeDownloaded from $fileSize")
                }

                outputStream.flush()
                if (inputStream != null) {
                    inputStream.close()
                }

                if (outputStream != null) {
                    outputStream.close()
                }

                val readLocation: File = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + "/" + nameOfFile
                )
                Log.d("salimmm", "readLocation $readLocation")
            } catch (e: Exception) {
                Log.e("salimmm", "WRITE EXC ${e.localizedMessage}")
            }
            return true
        } else {
            return false
        }
    }
    private fun setupClickListeners() {
        viewBinding.buttonDownload.setOnClickListener {
            if (isRequiredPermissionsGranted) {
                launchSaveProgress()
            } else {
                checkPermissions()
            }
        }
    }
    private fun launchSaveProgress() {
        writeToDisk(requireContext(), generatedImage?.image!!, "IMAGES")
    }
    @SuppressLint("Range")
    fun writeToDisk(context: Context, imageUrl: String, downloadSubfolder: String) {
        val imageUri = Uri.parse(imageUrl)
        val fileName = imageUri.lastPathSegment
        val downloadSubPath = downloadSubfolder + fileName
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(imageUri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDescription(imageUrl)
        request.allowScanningByMediaScanner()
       // request.setDestinationUri(getDownloadDestination(downloadSubPath))
        val downloadId: Long = downloadManager.enqueue(request)

        // Run a task in a background thread to check download progress
//        executor.execute {
//            var progress = 0
//            var isDownloadFinished = false
//            requireActivity().runOnUiThread {
//                if (activity != null) {
//                   // showLoadingDialog()
//                }
//            }
//            while (!isDownloadFinished) {
//                val cursor =
//                    downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
//                if (cursor.moveToFirst()) {
//                    val downloadStatus =
//                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
//                    when (downloadStatus) {
//                        DownloadManager.STATUS_RUNNING -> {
//                            val totalBytes =
//                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//                            if (totalBytes > 0) {
//                                val downloadedBytes =
//                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                                progress = (downloadedBytes * 100 / totalBytes).toInt()
//                            }
//                        }
//
//                        DownloadManager.STATUS_SUCCESSFUL -> {
//                            progress = 100
//                            isDownloadFinished = true
//                        }
//
//                        DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {
//
//                        }
//
//                        DownloadManager.STATUS_FAILED -> {
//                            isDownloadFinished = true
//                        }
//                    }
//                    val message = Message.obtain()
//                    message.arg1 = progress
//
//                }
//            }
//        }
    }
    private fun setupLottie() {
        viewBinding.apply {
            //buttonDownload.visibility = View.GONE
            ivGeneratedImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE
            animationView.playAnimation()
        }
    }

    private fun downloadImage(fileUrl: String) {
        var newUrl = ""
        newUrl = if (fileUrl.last() != '/') {
            "$fileUrl/"
        } else {
            fileUrl
        }
        Log.d("salimmmm", "fileUrl -> $newUrl")
        val builder: Retrofit.Builder = Retrofit.Builder().baseUrl(newUrl)
        val retrofit: Retrofit = builder.build()
        val downloadService: ApiService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseBody> = downloadService.downloadFile(newUrl)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val writeToDisk: Boolean =
                            response.body()?.let { writeFileToStorage(it) } == true
                        Log.d("salimmmm", "FILE DOWNLOADED OR NOT STATUS -> $writeToDisk")
                    } catch (e: Exception) {
                        Log.d("salimmmm", "onResponse Exception -> ${e.localizedMessage}")
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("salimmmm", "FILE DOWNLOAD onFailure -> $t")
            }
        })
    }
    private fun getPermissionState(isGranted: Boolean) {
        isRequiredPermissionsGranted = isGranted
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sendRequest(text: String, art: String?, size: String) {
        val requestBody = """
       {
       "prompt":"$text, $art",
       "n":1,
       "size":"$size",
       "model": "dall-e-3"
       }
    """

        Log.d("SendRequest", "Request Body: $requestBody")

        if (activity != null && context != null) {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
            client = builder.build()
            client.makePostRequest(requestBody.toRequestBody()) { result, error ->
                Log.d("aassdd", "Server response: $result")
                if (error != null) {
                    Log.e("aassdd", "Error: $error")
                    if (activity != null) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                            lifecycleScope.launch {
                                delay(1000)
                                parentFragmentManager.popBackStack(
                                    ArgumentKey.HOME_SCREEN,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                                )
                            }
                        }
                    }
                } else if (result != null && result.startsWith("https://")) {
                    Log.d("aassdd", "Received URL: $result")
                    if (activity != null) {
                        requireActivity().runOnUiThread {
                            Log.d("SendRequest", "Received URL: $result")
                            Glide.with(requireContext())
                                .load(result)
                                .into(viewBinding.ivGeneratedImage)
                            generatedImage = GenerateModel(
                                image = result,
                                animation = R.raw.loading
                            )
                            enabledButtons()
                        }
                    } else {
                        Log.e("aassdd", "Invalid URL received or result is null")
                    }
                }
            }
        }
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
    private fun enabledButtons() {
        viewBinding.apply {
            buttonEdit.isEnabled = true
            buttonEdit.isClickable = true
            buttonShare.isEnabled = true
            buttonShare.isClickable = true
            animationView.cancelAnimation()
            animationView.visibility = View.GONE
            buttonDownload.visibility = View.VISIBLE
            ivGeneratedImage.visibility = View.VISIBLE

        }
    }
//    private fun setupArtStyles() {
//        generateAdapter.submitList(viewModel.imageList)
//        viewBinding.rvArts.apply {
//            adapter = generateAdapter
//            layoutManager =
//                object : LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false) {
//                    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
//                        lp.width = width / 3
//                        return true
//                    }
//                }
//            setHasFixedSize(true)
//        }
//    }

    private fun onArtItemClicked(artStyleModel: GenerateModel, i: Int) {
        Log.d("salimmm", "artStyleModel $artStyleModel")
    }
}


