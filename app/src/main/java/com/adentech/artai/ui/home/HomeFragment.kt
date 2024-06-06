package com.adentech.artai.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.adentech.artai.core.common.ArgumentKey.SELECTED_ART_STYLE
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
import com.adentech.artai.extensions.parcelable
import com.adentech.artai.ui.arts.ArtStyleFragment
import com.adentech.artai.ui.watch.WatchAdsFragment
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
    private var selectedCount: Int = 0
    private lateinit var promptMessage: String
    private lateinit var selectedSize: String
    private val itemList: ArrayList<SizeModel> = ArrayList()
    var isSelected: Boolean = true
    private var selectArtStyle: ArtStyleModel? = null
    private val homeViewModel: HomeViewModel by viewModels()
    private var artStyle: String = ""
    private var selectedItemPosition: Int = 0
    private val homeNavArgs: HomeFragmentArgs by navArgs()


    override fun viewModelClass() = HomeViewModel::class.java

    override fun getResourceLayoutId() = R.layout.fragment_home

    override fun onInitDataBinding() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        handleArguments()
        selectedSize = itemList.size.toString()
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
            } else {
                navigateWatchAdsFragment()
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
            //findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArtStyleFragment())
            //showBottomSheet()
            showArtStyleFragment()

            viewBinding.etPrompt.isEnabled = false
        }

    }
    private fun handleArguments() {
//        val selectedItemPosition = arguments?.getInt("selectedItemPosition") ?: RecyclerView.NO_POSITION
//        val selectedArtStyle = arguments?.parcelable<ArtStyleModel>("selectedArtStyle")
        val selectedArtStyle = homeNavArgs.artStyle
        val selectedItemPosition = homeNavArgs.itemPosition
        if (selectedArtStyle != null && selectedItemPosition != RecyclerView.NO_POSITION) {
            selectArtStyle = selectedArtStyle
            this.selectedItemPosition = selectedItemPosition
            artStyle = selectedArtStyle.name
            artStyleAdapter.selectedItemPosition = selectedItemPosition
            artStyleAdapter.notifyDataSetChanged()
            Log.d("ecooo", "Selected Art Style: ${selectedArtStyle.name}")
        } else {
            selectDefaultArtStyle()
            Log.d("ecooo", "No art style selected")
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
                itemList.add(SizeModel(icon = "", size = "768x768", isSelected = true))
                selectedSize = "768x768"
                hideKeyboard()
            }

            viewBinding.llRectangle -> {
                itemList.add(SizeModel(icon = "", size = "768x1024", isSelected = true))
                selectedSize = "768x1024"
                hideKeyboard()
            }

            viewBinding.llVertical -> {
                itemList.add(SizeModel(icon = "", size = "1024x768", isSelected = true))
                selectedSize = "1024x768"
                hideKeyboard()
            }
        }
    }

    private fun navigateGenerateFragment(requestModel: RequestModel) {
//        val bundle = Bundle()
//        bundle.putParcelable(REQUEST_MODEL, requestModel)
//        val generateFragment = GenerateFragment()
//        generateFragment.arguments = bundle

        navigate(HomeFragmentDirections.actionHomeFragmentToGenerateFragment(requestModel))

//        parentFragmentManager.beginTransaction().apply {
//            attach(HomeFragment())
//            addToBackStack(HOME_SCREEN)
//            commit()
//        }
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
            Log.d("aaggaa", "Selected Art Style: $selectArtStyle")
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
            size = selectedSize
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
        viewBinding.fragmentContainer.isEnabled = false
        navigate(HomeFragmentDirections.actionHomeFragmentToArtStyleFragment())
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
        //artStyleAdapter.setOnArtItemSelectedListener(this)


    }

    override fun onResume() {
        super.onResume()
        Log.d("ecooo", "Home: onResume called")
        handleArguments()
        viewBinding.etPrompt.post {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(viewBinding.etPrompt, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun selectedSize(item: SizeModel) {
        hideKeyboard()
        //  selectedSize = item.size.toString()
    }

    override fun onItemClick(item: ArtStyleModel) {
        selectArtStyle = item
        artStyleAdapter.notifyDataSetChanged()
        Log.d("ecoo", "Selected Art Style: ${selectArtStyle?.name}")

    }

}