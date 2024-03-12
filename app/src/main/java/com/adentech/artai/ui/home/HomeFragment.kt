package com.adentech.artai.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adentech.artai.ArtaiApplication
import com.adentech.artai.R
import com.adentech.artai.core.common.ArgumentKey.HOME_SCREEN
import com.adentech.artai.core.common.ArgumentKey.REQUEST_MODEL
import com.adentech.artai.core.common.Util.EMPTY_STRING
import com.adentech.artai.core.fragments.BaseFragment
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.data.model.RequestModel
import com.adentech.artai.data.model.SizeModel
import com.adentech.artai.databinding.FragmentHomeBinding
import com.adentech.artai.extensions.hideKeyboard
import com.adentech.artai.extensions.multilineDone
import com.adentech.artai.extensions.multilineIme
import com.adentech.artai.ui.arts.ArtStyleFragment
import com.adentech.artai.ui.generate.GenerateFragment
import com.adentech.artai.ui.watch.WatchAdsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(),
    ArtStyleAdapter.OnArtItemSelectedListener {

    private val artStyleAdapter by lazy { ArtStyleAdapter() }
    private var selectedCount: Int = 0
    private lateinit var selectedArtStyle: String
    private lateinit var promptMessage: String
    private lateinit var selectedSize: String
    private var artStyle: String = ""
    val itemList: ArrayList<SizeModel> = ArrayList()

    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_home

    override fun onInitDataBinding() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        selectedArtStyle = viewModel.artList[0].name
        promptMessage = EMPTY_STRING
        selectedCount = 4
        textSizeColor()
        setupArtStyles()
        setupEdittext()

        viewBinding.buttonGenerate.setOnClickListener {
            if (ArtaiApplication.hasSubscription) {
                promptMessage = viewBinding.etPrompt.text.toString()
                if (promptMessage.isNotBlank() && promptMessage.isNotEmpty()) {
                    onGenerateClicked()

                } else {
                    Toast.makeText(requireContext(), "Please enter a prompt", Toast.LENGTH_SHORT)
                        .show()
                }
            }else{
                navigateWatchAdsFragment()
            }
        }

        viewBinding.tvSeeAll.setOnClickListener {
            //findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArtStyleFragment())
            //showBottomSheet()
            showArtStyleFragment()

            viewBinding.etPrompt.isEnabled = false
        }

    }
    private fun getSizeList(selectedLinearLayout: LinearLayoutCompat) {
        itemList.clear()
        when (selectedLinearLayout) {
            viewBinding.llSquare -> {
                itemList.add(SizeModel(icon = "", size = "1024x1024", isSelected = true))
                selectedSize = "1024x1024"
                hideKeyboard()
            }
            viewBinding.llRectangle -> {
                itemList.add(SizeModel(icon = "", size = "1024x1792", isSelected = true))
                selectedSize = "1024x1792"
                hideKeyboard()
            }
            viewBinding.llVertical -> {
                itemList.add(SizeModel(icon = "", size = "1792x1024", isSelected = true))
                selectedSize = "1792x1024"
                hideKeyboard()
            }
        }
    }
    private fun navigateGenerateFragment(requestModel: RequestModel) {
        val bundle = Bundle()
        bundle.putParcelable(REQUEST_MODEL, requestModel)
        val generateFragment = GenerateFragment()
        generateFragment.arguments = bundle

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, generateFragment)
            attach(HomeFragment())
            addToBackStack(HOME_SCREEN)
            commit()
        }
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
            viewBinding.llSquare.setBackgroundResource(R.drawable.bg_oval_black)
            getSizeList(viewBinding.llSquare)
        }
        viewBinding.llRectangle.setOnClickListener {
            clearBackgrounds()
            viewBinding.llRectangle.setBackgroundResource(R.drawable.bg_oval_black)
            getSizeList(viewBinding.llRectangle)
        }
        viewBinding.llVertical.setOnClickListener {
            clearBackgrounds()
            viewBinding.llVertical.setBackgroundResource(R.drawable.bg_oval_black)
            getSizeList(viewBinding.llVertical)
        }
    }
    private fun onGenerateClicked() {
        promptMessage = viewBinding.etPrompt.text.toString()
        if (promptMessage.isNotBlank() && promptMessage.isNotEmpty()) {
            hideKeyboard()
            sendRequest()
            Log.d("aaggaa", "Prompt Message: $promptMessage")
            Log.d("aaggaa", "Selected Size: $selectedSize")
            Log.d("aaggaa", "Selected Art Style: $selectedArtStyle")
        } else {
            Toast.makeText(requireContext(), "Prompt message cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendRequest() {
        if (context != null && activity != null) showProgress()
        val requestModel = RequestModel(
            prompt = promptMessage,
            size = selectedSize,
            model = "dall-e-3",
            art = artStyle
        )
        if (activity != null) {
            navigateGenerateFragment(requestModel)
        }
    }

//    fun showProgress() {
//        dialog?.show()
//    }
    private fun clearBackgrounds() {
        viewBinding.llSquare.setBackgroundResource(R.drawable.bg_size)
        viewBinding.llRectangle.setBackgroundResource(R.drawable.bg_size)
        viewBinding.llVertical.setBackgroundResource(R.drawable.bg_size)
    }
    private fun navigateWatchAdsFragment() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val watchAdsFragment = WatchAdsFragment()
        transaction.add(R.id.fragment_container, watchAdsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun showArtStyleFragment() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val artStyleFragment = ArtStyleFragment()
        transaction.add(R.id.fragment_container, artStyleFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun setupArtStyles() {
        viewBinding.rvArtStyle.apply {
            adapter = artStyleAdapter
            layoutManager =
                object : LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false) {
                    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                        lp.width = width / 4
                        return true
                    }
                }
            setHasFixedSize(true)
        }
        artStyleAdapter.submitList(viewModel.artList)
    }

    override fun onResume() {
        super.onResume()
        viewBinding.etPrompt.post {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(viewBinding.etPrompt, InputMethodManager.SHOW_IMPLICIT)
        }

    }


    private fun selectedSize(item: SizeModel) {
        hideKeyboard()
        selectedSize = item.size.toString()
    }

    override fun onArtItemSelected(item: ArtStyleModel) {
        selectedArtStyle = item.name
    }
}