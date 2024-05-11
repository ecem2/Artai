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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import androidx.work.WorkManager
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
import com.adentech.artai.extensions.makePostRequest
import com.adentech.artai.extensions.observe
import com.adentech.artai.extensions.parcelable
import com.adentech.artai.extensions.popBack
import com.adentech.artai.ui.home.HomeViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
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
       // requestPermissionLauncher.launch(requiredPermissions[0])


        viewBinding.apply {
            buttonShare.setOnClickListener {
                if (resultImage != null && resultImage != "") {
                    Log.d("ecemmm", "$resultImage")
                    viewModel.viewModelScope.launch {
                        val file = viewModel.downloadImage(resultImage!!, requireContext())
                        if (file != null) {
                            Log.d("ecemmm", "$file")
                            shareFile(file)
                        }
                    }
                }
            }
        }

        viewBinding.apply {
            buttonEdit.isEnabled = false
            buttonEdit.isClickable = false
            buttonShare.isEnabled = false
            buttonShare.isClickable = false
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
//                if (imageList.isNotEmpty()) {
//                    Log.d("ecemmm", "Download işlemi başlatılıyor. İndirilecek resim URL: ${imageList[0].image}")
//                    downloadImage(imageList[0].image)
//                    Log.d("ecemmm","ImageList: ${imageList[0].image}")
//                }
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
        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "com.adentech.artai" + ".provider",
                file
            )

            if (uri != null) {
                val shareBody = getString(R.string.room_ai_share)
                val sharingIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareBody)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/jpeg"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(
                    Intent.createChooser(
                        sharingIntent,
                        getString(R.string.share_using)
                    )
                )
            } else {
                Log.d("ecemmm", "Failed to get URI for file")
            }
        } catch (e: Exception) {
            Log.e("Share Error", "Error sharing file: ${e.message}")
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

    private fun writeFileToStorage(body: ResponseBody): Boolean {
        val nameOfFile = "IMAGE"
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
                viewBinding.ivGeneratedImage.setImageDrawable(Drawable.createFromPath(readLocation.toString()))
            } catch (e: Exception) {
                Log.e("salimmm", "WRITE EXC ${e.localizedMessage}")
            }
            return true
        } else {
            return false
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
         request.setDestinationUri(getDownloadDestination(downloadSubPath))
        val downloadId: Long = downloadManager.enqueue(request)

        // Run a task in a background thread to check download progress
        executor.execute {
            var progress = 0
            var isDownloadFinished = false
            requireActivity().runOnUiThread {
                if (activity != null) {
                    showLoadingDialog()
                }
            }
            val galleryUri = try {
                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    imageUri.toString(),
                    fileName,
                    ""
                )
            } catch (e: FileNotFoundException) {
                null
            }

            if (galleryUri != null) {
                Log.d("ecemmm","Image saved to gallery")
            } else {
                Log.d("ecemmm","Failed to save image to gallery")
            }
            while (!isDownloadFinished) {
                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                if (cursor.moveToFirst()) {
                    val downloadStatus =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (downloadStatus) {
                        DownloadManager.STATUS_RUNNING -> {
                            val totalBytes =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (totalBytes > 0) {
                                val downloadedBytes =
                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                progress = (downloadedBytes * 100 / totalBytes).toInt()
                            }
                        }

                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progress = 100
                            isDownloadFinished = true
                        }

                        DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {

                        }

                        DownloadManager.STATUS_FAILED -> {
                            isDownloadFinished = true
                        }
                    }
                    val message = Message.obtain()
                    message.arg1 = progress

                }
            }
        }
    }
    private fun getDownloadDestination(downloadSubPath: String): Uri {
        val picturesFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val destinationFile = File(picturesFolder, downloadSubPath)
        destinationFile.mkdirs()
        return Uri.fromFile(destinationFile)
    }
    private fun showLoadingDialog() {
        val dialogBuilder = Dialog(requireContext(), R.style.CustomDialog)
        dialogBinding = DialogLoadingProgressBinding.inflate(layoutInflater)
        dialogBuilder.setContentView(dialogBinding.root)
        dialogBuilder.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialogBuilder.setCancelable(false)

        dialogBinding.apply {
            tvProgress.text = "% 0"
            buttonDone.setOnClickListener {
                dialogBuilder.cancel()
            }
        }
        mDialog = dialogBuilder
        dialogBuilder.show()
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
        if (fileUrl.isNotEmpty()) {
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
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val writeToDisk: Boolean =
                                response.body()?.let { writeFileToStorage(it) } == true
                            Log.d("salimmmm", "FILE DOWNLOADED OR NOT STATUS -> $writeToDisk")
                        } catch (e: Exception) {
                            Log.d("salimmmm", "onResponse Exception -> ${e.localizedMessage}")
                        }
                    } else {
                        Log.d("salimmm", "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("salimmmm", "FILE DOWNLOAD onFailure -> $t")
                }
            })
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


