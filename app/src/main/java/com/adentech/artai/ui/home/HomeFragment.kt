package com.adentech.artai.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adentech.artai.ArtaiApplication
import com.adentech.artai.R
import com.adentech.artai.core.common.Util.EMPTY_STRING
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.data.model.RequestModel
import com.adentech.artai.data.model.SizeModel
import com.adentech.artai.databinding.FragmentHomeBinding
import com.adentech.artai.extensions.hideKeyboard
import com.adentech.artai.extensions.multilineDone
import com.adentech.artai.extensions.multilineIme
import com.adentech.artai.extensions.navigate
import com.adentech.artai.ui.arts.ArtStyleAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(),
    ArtStyleAdapter.ItemClickListener {

    private val artStyleAdapter: ArtStyleAdapter by lazy {
        ArtStyleAdapter(
            requireContext(),
            this@HomeFragment
        )
    }
    private lateinit var promptMessage: String
    private var selectedSize: SizeModel? = null
    private val itemList: ArrayList<SizeModel> = ArrayList()
    private var selectArtStyle: ArtStyleModel? = null
    private val homeViewModel: HomeViewModel by viewModels()
    private var artStyle: String = ""
    private var selectedItemPosition: Int = 0
    private val homeNavArgs: HomeFragmentArgs by navArgs()
    private val TEMPORARY_PREMIUM_KEY = "temporary_premium_access"
    private lateinit var sharedPreferences: SharedPreferences
    private val WATCHED_ADS_KEY = "watched_ads_key"

    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_home

    override fun onInitDataBinding() {
        sharedPreferences = requireContext().getSharedPreferences("ads_prefs", Context.MODE_PRIVATE)
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        handleArguments()
        selectedSize = SizeModel(
            icon = null,
            width = 768,
            height = 768,
            isSelected = true
        )
        promptMessage = EMPTY_STRING
        textSizeColor()
        setupArtStyles()
        setupEdittext()
        checkPremiumStatus()

        viewBinding.buttonGenerate.setOnClickListener {
            if (ArtaiApplication.hasSubscription) {
                viewModel.preferences.getWatchAds()
                promptMessage = viewBinding.etPrompt.text.toString()
                if (promptMessage.isNotBlank() && promptMessage.isNotEmpty()) {
                    onGenerateClicked()
                } else {
                    Toast.makeText(requireContext(), "Please enter a prompt", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                navigateIfNeeded()

            }

            promptMessage = viewBinding.etPrompt.text.toString()
            if (promptMessage.isNotBlank() && promptMessage.isNotEmpty()) {
                onGenerateClicked()

            } else {
                Toast.makeText(requireContext(), "Please enter a prompt", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewBinding.tvSeeAll.setOnClickListener {
            showArtStyleFragment()
            viewBinding.etPrompt.isEnabled = false
        }

    }

    private fun navigateIfNeeded() {
        val hasWatchedAds = sharedPreferences.getBoolean(WATCHED_ADS_KEY, false)
        val hasTemporaryAccess = hasPremiumAccess()
        if (!hasWatchedAds && !hasTemporaryAccess) {
            navigateWatchAdsFragment()
        } else {
            onGenerateClicked()
        }
    }

    private fun markAdsAsWatched() {
        sharedPreferences.edit().putBoolean(WATCHED_ADS_KEY, true).apply()
    }

    private fun grantTemporaryPremiumAccess() {
        sharedPreferences.edit().putBoolean(TEMPORARY_PREMIUM_KEY, true).apply()
    }

    private fun hasPremiumAccess(): Boolean {
        val hasSubscription = ArtaiApplication.hasSubscription
        val hasTemporaryAccess = sharedPreferences.getBoolean(TEMPORARY_PREMIUM_KEY, false)
        return hasSubscription || hasTemporaryAccess
    }

    private fun checkPremiumStatus() {
        val hasSubscription = ArtaiApplication.hasSubscription
        val hasTemporaryAccess = sharedPreferences.getBoolean(TEMPORARY_PREMIUM_KEY, false)

        if (hasSubscription || hasTemporaryAccess) {
            if (hasTemporaryAccess) {
                sharedPreferences.edit().putBoolean(TEMPORARY_PREMIUM_KEY, false).apply()
            }
        }
    }

    private fun handleArguments() {
        val selectedArtStyle = homeNavArgs.artStyle
        val selectedItemPosition = homeNavArgs.itemPosition
        val hasTemporaryAccess = homeNavArgs.hasTemporaryAccess // New argument for temporary access

        if (hasTemporaryAccess) {
            grantTemporaryPremiumAccess()
        }

        if (selectedArtStyle != null && selectedItemPosition != RecyclerView.NO_POSITION) {
            selectArtStyle = selectedArtStyle
            this.selectedItemPosition = selectedItemPosition
            artStyle = selectedArtStyle.name.toString()
            artStyleAdapter.selectedItemPosition = selectedItemPosition
            artStyleAdapter.notifyDataSetChanged()
        } else {
            selectDefaultArtStyle()
        }
    }


    private fun selectDefaultArtStyle() {
        selectedItemPosition = 0
        selectArtStyle = homeViewModel.artList[selectedItemPosition]
        selectArtStyle?.isSelected = true
        artStyle = selectArtStyle?.name ?: ""
        artStyleAdapter.selectedItemPosition = selectedItemPosition
        artStyleAdapter.notifyDataSetChanged()
    }

    private fun getSizeList(selectedLinearLayout: LinearLayoutCompat) {
        itemList.clear()
        when (selectedLinearLayout) {
            viewBinding.llSquare -> {
                itemList.add(SizeModel(icon = "", width = 768, height = 768, isSelected = true))
                hideKeyboard()
            }

            viewBinding.llRectangle -> {
                itemList.add(SizeModel(icon = "", width = 768, height = 1024, isSelected = true))
                hideKeyboard()
            }

            viewBinding.llVertical -> {
                itemList.add(SizeModel(icon = "", width = 1024, height = 768, isSelected = true))
                hideKeyboard()
            }
        }
    }

    private fun navigateGenerateFragment(requestModel: RequestModel) {
        navigate(HomeFragmentDirections.actionHomeFragmentToGenerateFragment(requestModel))
    }

    private fun setupEdittext() {
        viewBinding.etPrompt.apply {
            multilineIme(EditorInfo.IME_ACTION_DONE)
            multilineDone { hideKeyboard() }
        }
    }

    private fun textSizeColor() {
        viewBinding.llSquare.setOnClickListener {
            clearBackgrounds()
            viewBinding.llSquare.setBackgroundResource(R.drawable.bg_transparent_item)
            getSizeList(viewBinding.llSquare)
            selectedSize = SizeModel(
                icon = null,
                width = 768,
                height = 768,
                isSelected = true
            )
        }
        viewBinding.llRectangle.setOnClickListener {
            clearBackgrounds()
            viewBinding.llRectangle.setBackgroundResource(R.drawable.bg_transparent_item)
            getSizeList(viewBinding.llRectangle)
            selectedSize = SizeModel(
                icon = null,
                width = 768,
                height = 1024,
                isSelected = true
            )
        }
        viewBinding.llVertical.setOnClickListener {
            clearBackgrounds()
            viewBinding.llVertical.setBackgroundResource(R.drawable.bg_transparent_item)
            getSizeList(viewBinding.llVertical)
            selectedSize = SizeModel(
                icon = null,
                width = 1024,
                height = 768,
                isSelected = true
            )
        }
    }

    private fun onGenerateClicked() {
        promptMessage = viewBinding.etPrompt.text.toString()
        if (promptMessage.isNotBlank() && promptMessage.isNotEmpty()) {
            hideKeyboard()
            sendRequest()
        } else {
            Toast.makeText(requireContext(), "Prompt message cannot be empty", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun sendRequest() {
        if (context != null && activity != null) showProgress()
        val requestModel = RequestModel(
            prompt = promptMessage,
            style = selectArtStyle?.name ?: "",
            width = selectedSize?.width ?: 768,
            height = selectedSize?.height ?: 768
        )
        if (activity != null) {
            navigateGenerateFragment(requestModel)
        }
    }

    private fun clearBackgrounds() {
        viewBinding.llSquare.setBackgroundResource(R.drawable.bg_size)
        viewBinding.llRectangle.setBackgroundResource(R.drawable.bg_size)
        viewBinding.llVertical.setBackgroundResource(R.drawable.bg_size)
    }

    private fun navigateWatchAdsFragment() {
        navigate(HomeFragmentDirections.actionHomeFragmentToWatchAdsFragment())
    }

    private fun showArtStyleFragment() {
        viewBinding.fragmentContainer.isEnabled = false
        navigate(HomeFragmentDirections.actionHomeFragmentToArtStyleFragment())
    }

    private fun setupArtStyles() {
        viewBinding.rvArtStyle.apply {
            adapter = artStyleAdapter
            layoutManager =
                object : LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false) {
                    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                        lp.width = width / 3
                        return true
                    }
                }
            setHasFixedSize(true)
        }
        artStyleAdapter.submitList(viewModel.artList)


    }

    override fun onResume() {
        super.onResume()
        val hasWatchedAds = sharedPreferences.getBoolean(WATCHED_ADS_KEY, false)
        if (hasWatchedAds) {
            markAdsAsWatched()
        }

        handleArguments()
        viewBinding.etPrompt.post {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(viewBinding.etPrompt, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onItemClick(item: ArtStyleModel) {
        selectArtStyle = item
        artStyleAdapter.notifyDataSetChanged()

    }

}