package com.adentech.artai.ui.arts

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adentech.artai.R
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.databinding.FragmentArtStyleBinding
import com.adentech.artai.ui.home.ArtStyleAdapter
import com.adentech.artai.ui.home.HomeFragment
import com.adentech.artai.ui.home.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtStyleFragment : BaseFragment<HomeViewModel, FragmentArtStyleBinding>(), ArtStyleAdapter.OnArtItemSelectedListener {

    private val artStyleAdapter by lazy { ArtStyleAdapter() }
    private lateinit var backgroundBlurImage: ConstraintLayout
    private lateinit var contentLayout: LinearLayout
    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_art_style

    override fun onInitDataBinding() {
        viewBinding.ivRight.setOnClickListener {
            val transaction = childFragmentManager.beginTransaction()
            val homeFragment = HomeFragment()
            transaction.add(R.id.container, homeFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        //backgroundBlurImage = viewBinding.container

        setupArtStyles()
        //applyBlurEffect()
//        val blurredBm = getBlurredBitmap(requireContext(), R.drawable.blur_bg, 25f)
//        if (blurredBm != null) {
//            backgroundBlurImage.background = BitmapDrawable(resources, blurredBm)
//        } else {
//            Log.e("Error", "Blurred bitmap is null")
//        }
    }

    private fun setupArtStyles() {
        artStyleAdapter.submitList(viewModel.artList)
        artStyleAdapter.setOnArtItemSelectedListener(this)
        viewBinding.rvArtStyle.apply {
            adapter = artStyleAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
        }
    }
//    private fun getBlurredBitmap(context: Context, @DrawableRes drawableId: Int, radius: Float): Bitmap {
//        // Arka plan görüntüsünü al
//        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
//
//        // Bulanıklaştırma efekti uygula
//        val blurredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(blurredBitmap)
//        val paint = Paint()
//        paint.isAntiAlias = true
//        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
//        paint.shader = shader
//        val blurMaskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
//        paint.maskFilter = blurMaskFilter
//        canvas.drawBitmap(bitmap, 0f, 0f, paint)
//
//        return blurredBitmap
//    }

    override fun onResume() {
        super.onResume()
    }

    override fun onArtItemSelected(item: ArtStyleModel) {
        Log.d("salimmm", "onItemSelected artStyleModel $item")
    }
}