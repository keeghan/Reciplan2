package com.keeghan.reciplan2.ui.add

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.radiobutton.MaterialRadioButton
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.AddBottomSheetBinding
import com.keeghan.reciplan2.databinding.FragmentAddBinding
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.LUNCH
import com.keeghan.reciplan2.utils.Constants.NOT_EDIT
import com.keeghan.reciplan2.utils.Constants.SNACK
import com.keeghan.reciplan2.utils.Constants.SUCCESS
import com.yalantis.ucrop.UCrop
import kotlinx.serialization.json.Json
import java.io.File

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = mainBinding.root
        viewModel = ViewModelProvider(this)[AddViewModel::class.java]

        //Open bottomSheetDialog on pressing the next button
        mainBinding.nextButton.setOnClickListener { inflateBottomLayout() }

        //Launch activity for result for image
        mainBinding.chooseImageButton.setOnClickListener { getContent.launch(ADD_RECIPE_IMAGE_LOC) }

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
        val checkedTypeID = bottomSheetBinding.radioGroup.checkedRadioButtonId
        val radioButtonId = bottomSheetBinding.root.findViewById<MaterialRadioButton>(checkedTypeID).id

        val recipe = Recipe(
            _id = generateUniqueId(),
            name = mainBinding.recipeTitleEditText.text?.trim().toString(),
            mins = bottomSheetBinding.timePicker.value,
            imageUrl = webpCompressedImageFile.path,
            favorite = bottomSheetBinding.switchFavBtn.isChecked,
            ingredients = bottomSheetBinding.recipeIngredientEditText.text?.trim()?.lines()!!.size,
            userCreated = true,
            collection = bottomSheetBinding.switchColBtn.isChecked,
            userDirection = mainBinding.recipeDescEditText.text?.trim().toString(),
            userIngredient = bottomSheetBinding.recipeIngredientEditText.text?.trim().toString(),
            type = typeOptionSelected(radioButtonId)
        )
        viewModel.saveRecipe(tempImageUri, webpCompressedImageFile, recipe)

        clearScreen()
        bottomSheet!!.dismissWithAnimation = true
        bottomSheet!!.dismiss()
        viewModel.errorMsg.observe(viewLifecycleOwner) {
            if (it == SUCCESS) {
                showToast("Recipe Updated")
            }
        }
    }

    private fun setButtonSheetOnClickListeners() {
        bottomSheetBinding.switchColBtn.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                bottomSheetBinding.switchFavBtn.isChecked = false
            }
        }

        bottomSheetBinding.switchFavBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                bottomSheetBinding.switchColBtn.isChecked = true
            }
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
        val ingredients = bottomSheetBinding.recipeIngredientEditText.text.trim().toString()
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

            ingredients.isBlank() -> {
                bottomSheetBinding.recipeIngredientEditText.error = "Empty"
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


    //Clear all the fields after a recipe is saved
    private fun clearScreen() {
        mainBinding.recipeDescEditText.text.clear()
        mainBinding.recipeTitleEditText.text?.clear()
        mainBinding.recipeImageView.setImageDrawable(null)

        bottomSheetBinding.recipeIngredientEditText.text.clear()
        bottomSheetBinding.switchColBtn.isChecked = false
        bottomSheetBinding.switchFavBtn.isChecked = false
        bottomSheetBinding.radioGroup.clearCheck()
        bottomSheetBinding.timePicker.value = 15
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val RECIPE_EDIT_JSON = "com.keeghan.reciplan2.AddFragment.recipe_edit_json"
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


