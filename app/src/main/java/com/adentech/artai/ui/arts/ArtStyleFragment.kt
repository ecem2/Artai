package com.adentech.artai.ui.arts

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adentech.artai.R
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.data.model.RequestModel
import com.adentech.artai.databinding.FragmentArtStyleBinding
import com.adentech.artai.extensions.navigate
import com.adentech.artai.extensions.parcelable
import com.adentech.artai.ui.home.ArtStyleAdapter
import com.adentech.artai.ui.home.ArtStyleViewModel
import com.adentech.artai.ui.home.HomeFragment
import com.adentech.artai.ui.home.HomeFragmentDirections
import com.adentech.artai.ui.home.HomeViewModel
import com.adentech.artai.ui.watch.WatchAdsFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtStyleFragment : BaseFragment<HomeViewModel, FragmentArtStyleBinding>(), ArtStyleAdapter.ItemClickListener {

    private val artStyleAdapter: ArtStyleAdapter by lazy {
        ArtStyleAdapter(
            requireContext(),
            this@ArtStyleFragment
        )
    }
    private lateinit var backgroundBlurImage: ConstraintLayout
    private lateinit var contentLayout: LinearLayout
    private val sharedViewModel: ArtStyleViewModel by activityViewModels()
    private var selectedArtStyle: ArtStyleModel? = null
    private var selectedItemPosition: Int = 0

    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_art_style

    override fun onInitDataBinding() {
        viewBinding.ivRight.setOnClickListener {
            navigateHome()
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

        viewBinding.buttonContinue.setOnClickListener {
            navigateHome()
            }
    }
//    private fun navigateHome() {
//        val bundle = Bundle().apply {
//            putParcelable("selectedArtStyle", selectedArtStyle)
//            putInt("selectedItemPosition", selectedItemPosition)
//        }
//        Log.d("ecooo", "Navigating home with bundle: $bundle")
//
//        if (isAdded) {
//            val homeFragment = requireActivity().supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)
//            if (homeFragment != null) {
//                requireActivity().supportFragmentManager.popBackStack(HomeFragment::class.java.simpleName, 0)
//            } else {
//                val newHomeFragment = HomeFragment().apply {
//                    arguments = bundle
//                }
//                requireActivity().supportFragmentManager.beginTransaction().apply {
//                    replace(R.id.container, newHomeFragment, HomeFragment::class.java.simpleName)
//                    addToBackStack(HomeFragment::class.java.simpleName)
//                    commitAllowingStateLoss()
//                }
//            }
//        }
//    }

    private fun navigateHome() {

        try {
//            navigate(ArtStyleFragmentDirections.actionArtStyleFragmentToHomeFragment(
//                selectedItemPosition, selectedArtStyle
//            ))

            val action = ArtStyleFragmentDirections.actionArtStyleFragmentToHomeFragment(
                selectedItemPosition,
                selectedArtStyle
            )
            findNavController().navigate(action)
        } catch (e: Exception){
            Log.e("ecooo","edsfgsd ${e.localizedMessage}")
        }

//        if (isAdded) {
//            val action = ArtStyleFragmentDirections.actionArtStyleFragmentToHomeFragment(
//                selectedArtStyle = selectedArtStyle,
//                selectedItemPosition = selectedItemPosition
//            )
//            findNavController().navigate(action)
//        }
    }
    private fun setupArtStyles() {
        viewBinding.rvArtStyle.apply {
            adapter = artStyleAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
        }
        artStyleAdapter.submitList(viewModel.artList)
       // artStyleAdapter.setOnArtItemSelectedListener(this)

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


    override fun onItemClick(item: ArtStyleModel) {
        selectedItemPosition = viewModel.artList.indexOf(item)
        selectedArtStyle = item
        item.isSelected = true
        artStyleAdapter.notifyDataSetChanged()

    }
}