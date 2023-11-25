package com.keeghan.reciplan2.ui.add

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.radiobutton.MaterialRadioButton
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.AddBottomSheetBinding
import com.keeghan.reciplan2.databinding.FragmentAddBinding
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.LUNCH
import com.keeghan.reciplan2.utils.Constants.SNACK
import com.keeghan.reciplan2.utils.Constants.SUCCESS
import com.yalantis.ucrop.UCrop
import java.io.File
import kotlin.properties.Delegates

//TODO: Add youtube links to saved Recipes

class AddFragment : Fragment() {

    private lateinit var viewModel: AddViewModel
    private val mainBinding by lazy { FragmentAddBinding.inflate(layoutInflater) }

    private lateinit var tempImageUri: Uri
    private lateinit var webpCompressedImageFile: File

    //Bottom Dialog sheet bindings
    private var bottomSheet: BottomSheetDialog? = null
    private var _bottomSheetBinding: AddBottomSheetBinding? = null
    private val bottomSheetBinding get() = _bottomSheetBinding!!

    private var isSw600dp by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AddViewModel::class.java]
        isSw600dp = requireContext().resources.configuration.smallestScreenWidthDp >= 600
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = mainBinding.root

        //Open bottomSheetDialog on pressing the next button
        if (!isSw600dp) mainBinding.nextButton?.setOnClickListener { inflateBottomLayout() }

        //Launch activity for result for image
        mainBinding.chooseImageButton.setOnClickListener { getContent.launch(ADD_RECIPE_IMAGE_LOC) }
        if (isSw600dp) setUpSw600dpLayout() //tablet layout support

        return view
    }

    //Launch Activity for Result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        //define uri(name and location of un_cropped file)
        tempImageUri = Uri.fromFile(
            File(
                requireActivity().filesDir,
                "${mainBinding.recipeTitleEditText.text?.trim()?.replace(Regex("\\s"), "")}${System.nanoTime()}.jpg"
            )
        )

        //define uri(name and location of cropped file)
        webpCompressedImageFile = File(
            requireActivity().filesDir,
            "${mainBinding.recipeTitleEditText.text?.trim()?.replace(Regex("\\s"), "")}${System.nanoTime()}_c.webp"
        )

        //crop image and save in croppedImageUri
        if (uri != null) {
            UCrop.of(uri, tempImageUri).withAspectRatio(1F, 1F).withMaxResultSize(400, 400).start(requireActivity())
        }

        Glide.with(requireContext()).load(tempImageUri).into(mainBinding.recipeImageView)
//        Glide.with(requireContext()).load(tempImageUri).placeholder(drawable.ic_launcher_background).centerCrop()
//            .into(mainBinding.recipeImageView)
    }

    //Inflate bottom sheet  and show with no data
    private fun inflateBottomLayout() {
        bottomSheet = BottomSheetDialog(requireContext())
        _bottomSheetBinding = AddBottomSheetBinding.inflate(bottomSheet!!.layoutInflater)
        bottomSheet!!.setContentView(bottomSheetBinding.root)
        bottomSheet!!.behavior.peekHeight = -1
        bottomSheet!!.show()
        //set on clickListeners for bottomSheet
        setButtonSheetOnClickListeners()
    }

    //Save or Update Recipe and reset screen
    private fun saveOrUpdateRecipe() {
        val checkedTypeID = if (isSw600dp) mainBinding.radioGroup!!.checkedRadioButtonId
        else bottomSheetBinding.radioGroup.checkedRadioButtonId
        val radioButtonId = if (isSw600dp) mainBinding.root.findViewById<MaterialRadioButton>(checkedTypeID).id
        else bottomSheetBinding.root.findViewById<MaterialRadioButton>(checkedTypeID).id
        val mins = if (isSw600dp) mainBinding.timePicker!!.value else bottomSheetBinding.timePicker.value
        val favorite =
            if (isSw600dp) mainBinding.switchFavBtn!!.isChecked else bottomSheetBinding.switchFavBtn.isChecked
        val ingredients = if (isSw600dp) mainBinding.recipeIngredientEditText?.text?.trim()?.lines()!!.size
        else bottomSheetBinding.recipeIngredientEditText.text?.trim()?.lines()!!.size
        val collection = if (isSw600dp) mainBinding.switchColBtn!!.isChecked
        else bottomSheetBinding.switchColBtn.isChecked
        val userIngredient = if (isSw600dp) mainBinding.recipeIngredientEditText?.text?.trim().toString()
        else bottomSheetBinding.recipeIngredientEditText.text?.trim().toString()

        val recipe = Recipe(
            _id = generateUniqueId(),
            name = mainBinding.recipeTitleEditText.text?.trim().toString(),
            mins = mins,
            imageUrl = webpCompressedImageFile.path,
            favorite = favorite,
            ingredients = ingredients,
            userCreated = true,
            collection = collection,
            userDirection = mainBinding.recipeDescEditText.text?.trim().toString(),
            userIngredient = userIngredient,
            type = if (isSw600dp) typeOptionSelectedSw600dp(radioButtonId) else typeOptionSelected(radioButtonId)
        )
        viewModel.saveRecipe(tempImageUri, webpCompressedImageFile, recipe)

        clearScreen()
        if (!isSw600dp) {
            bottomSheet!!.dismissWithAnimation = true
            bottomSheet!!.dismiss()
        }
        viewModel.errorMsg.observe(viewLifecycleOwner) {
            if (it != "") {
                if (it == SUCCESS) showToast("Recipe Added") else showToast(it)
                viewModel.msgReset()
            }
        }
    }

    private fun setUpSw600dpLayout() {
        with(mainBinding) {
            switchColBtn?.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) switchFavBtn?.isChecked = false
            }

            switchFavBtn?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) switchColBtn?.isChecked = true
            }
            //Click on save button to save Recipe
            saveButton?.setOnClickListener { if (isRecipeValid()) saveOrUpdateRecipe() }
            timePicker?.maxValue = 300
            timePicker?.minValue = 15
            timePicker?.wrapSelectorWheel = true
        }
    }

    private fun setButtonSheetOnClickListeners() {
        bottomSheetBinding.switchColBtn.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) bottomSheetBinding.switchFavBtn.isChecked = false
        }

        bottomSheetBinding.switchFavBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) bottomSheetBinding.switchColBtn.isChecked = true
        }
        //Click on save button to save Recipe
        bottomSheetBinding.saveButton.setOnClickListener { if (isRecipeValid()) saveOrUpdateRecipe() }
        bottomSheetBinding.timePicker.maxValue = 300
        bottomSheetBinding.timePicker.minValue = 15
        bottomSheetBinding.timePicker.wrapSelectorWheel = true
    }

    //Validate if Recipe Title and Description are filled and Image is selected
    private fun isRecipeValid(): Boolean {
        val title = mainBinding.recipeTitleEditText.text?.trim().toString()
        val description = mainBinding.recipeDescEditText.text.trim().toString()
        val ingredients = if (isSw600dp) mainBinding.recipeIngredientEditText
        else bottomSheetBinding.recipeIngredientEditText

        return when {
            title.isBlank() -> {
                mainBinding.recipeTitleEditText.error = "Empty"
                false
            }

            description.isBlank() -> {
                mainBinding.recipeDescEditText.error = "Empty"
                false
            }

            //check if an image is selected, if editing recipe with no image pass true
            !::tempImageUri.isInitialized -> {
                showToast("Choose a recipe image")
                false
            }

            ingredients?.text?.trim().toString().isBlank() -> {
                ingredients?.error = "Empty"
                false
            }

            else -> true
        }
    }

    private fun typeOptionSelected(radioBtnId: Int): String {
        return when (radioBtnId) {
            bottomSheetBinding.optionBreakfast.id -> BREAKFAST
            bottomSheetBinding.optionLunch.id -> LUNCH
            bottomSheetBinding.optionDinner.id -> DINNER
            bottomSheetBinding.optionSnack.id -> SNACK
            else -> SNACK
        }
    }

    private fun typeOptionSelectedSw600dp(radioBtnId: Int): String {
        return when (radioBtnId) {
            mainBinding.optionBreakfast!!.id -> BREAKFAST
            mainBinding.optionLunch!!.id -> LUNCH
            mainBinding.optionDinner!!.id -> DINNER
            mainBinding.optionSnack!!.id -> SNACK
            else -> SNACK
        }
    }


    //Clear all the fields after a recipe is saved
    private fun clearScreen() {
        mainBinding.recipeDescEditText.text.clear()
        mainBinding.recipeTitleEditText.text?.clear()
        mainBinding.recipeImageView.setImageDrawable(null)
        if (isSw600dp) {
            mainBinding.recipeIngredientEditText?.text?.clear()
            mainBinding.switchColBtn?.isChecked = false
            mainBinding.switchFavBtn?.isChecked = false
            mainBinding.radioGroup?.clearCheck()
            mainBinding.timePicker?.value = 15
        } else {
            bottomSheetBinding.recipeIngredientEditText.text.clear()
            bottomSheetBinding.switchColBtn.isChecked = false
            bottomSheetBinding.switchFavBtn.isChecked = false
            bottomSheetBinding.radioGroup.clearCheck()
            bottomSheetBinding.timePicker.value = 15
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ADD_RECIPE_IMAGE_LOC = "image/*"
    }

    private fun generateUniqueId(): Int {
        return kotlin.math.abs(System.currentTimeMillis().toInt())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bottomSheetBinding = null
    }

}


