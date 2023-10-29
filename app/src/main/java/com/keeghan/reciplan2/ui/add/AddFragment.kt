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
import com.keeghan.reciplan2.R.drawable
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.AddBottomSheetBinding
import com.keeghan.reciplan2.databinding.FragmentAddBinding
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.LUNCH
import com.keeghan.reciplan2.utils.Constants.SNACK
import com.yalantis.ucrop.UCrop
import java.io.File


class AddFragment : Fragment() {

    private lateinit var viewModel: AddViewModel
    private var _binding: FragmentAddBinding? = null
    private val fragmentAddBinding get() = _binding!!
    private lateinit var tempImageUri: Uri
    private lateinit var webpCompressedImageFile: File

    private var bottomSheet: BottomSheetDialog? = null
    private var bottomSheetBinding: AddBottomSheetBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        val view = fragmentAddBinding.root
        viewModel = ViewModelProvider(this)[AddViewModel::class.java]

        //Open bottomSheetDialog on pressing the next button
        fragmentAddBinding.nextButton.setOnClickListener {
            bottomSheet = BottomSheetDialog(requireContext())
            bottomSheetBinding = AddBottomSheetBinding.inflate(bottomSheet!!.layoutInflater)
            bottomSheet!!.setContentView(bottomSheetBinding!!.root)
            bottomSheet!!.behavior.peekHeight = -1
            bottomSheet!!.show()
            //set on clickListeners for bottomSheet
            setButtonSheetOnClickListeners()
        }


        //Launch activity for result for image
        fragmentAddBinding.chooseImageButton.setOnClickListener {
            getContent.launch(ADD_RECIPE_IMAGE_LOC)
        }

        return view
    }

    //Launch Activity for Result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        //define uri(name and location of un_cropped file)
        tempImageUri = Uri.fromFile(
            File(
                requireActivity().filesDir,
                "${fragmentAddBinding.recipeTitleEditText.text?.trim()}${System.nanoTime()}.jpg"
            )
        )

        //define uri(name and location of cropped file)
        webpCompressedImageFile = File(
            requireActivity().filesDir,
            "${fragmentAddBinding.recipeTitleEditText.text?.trim()}${System.nanoTime()}_compressed.webp"
        )

        //crop image and save in croppedImageUri
        if (uri != null) {
            UCrop.of(uri, tempImageUri).withAspectRatio(1F, 1F).withMaxResultSize(400, 400).start(requireActivity())
        }

        Glide.with(requireContext()).load(tempImageUri).placeholder(drawable.ic_launcher_background).centerCrop()
            .into(fragmentAddBinding.recipeImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomSheetBinding = null
    }

    //Save Recipe and reset screen
    private fun saveRecipe() {
        val checkedTypeID = bottomSheetBinding!!.radioGroup.checkedRadioButtonId
        val radioButtonId = bottomSheetBinding!!.root.findViewById<MaterialRadioButton>(checkedTypeID).id
        val type = typeOptionSelected(radioButtonId)

        val recipe = Recipe(
            name = fragmentAddBinding.recipeTitleEditText.text?.trim().toString(),
            mins = 30,
            imageUrl = webpCompressedImageFile.path,
            favorite = bottomSheetBinding!!.switchFavBtn.isChecked,
            userCreated = true,
            collection = bottomSheetBinding!!.switchColBtn.isChecked,
            userDirection = fragmentAddBinding.recipeDescEditText.text?.trim().toString(),
            type = type
        )

        viewModel.saveRecipe(tempImageUri, webpCompressedImageFile, recipe)
        clearForm()
        showToast("Recipe saved")
    }

    private fun setButtonSheetOnClickListeners() {
        bottomSheetBinding!!.switchColBtn.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                bottomSheetBinding!!.switchFavBtn.isChecked = false
            }
        }

        bottomSheetBinding!!.switchFavBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                bottomSheetBinding!!.switchColBtn.isChecked = true
            }
        }
        //Click on save button to save Recipe
        bottomSheetBinding?.saveButton?.setOnClickListener {
            if (isRecipeValid()) saveRecipe()
        }
    }

    //Validate if Recipe Title and Description are filled and Image is selected
    private fun isRecipeValid(): Boolean {
        val title = fragmentAddBinding.recipeTitleEditText.text?.trim().toString()
        val description = fragmentAddBinding.recipeDescEditText.text.trim().toString()
        return when {
            title.isBlank() -> {
                fragmentAddBinding.recipeTitleEditText.error = "Empty"
                false
            }

            description.isBlank() -> {
                fragmentAddBinding.recipeDescEditText.error = "Empty"
                false
            }

            !::tempImageUri.isInitialized -> {
                showToast("Choose a recipe image")
                false
            }
            //TODO: "error check for bottom sheet"
            else -> true
        }
    }

    private fun typeOptionSelected(radioBtnId: Int): String {
        return when (radioBtnId) {
            bottomSheetBinding!!.optionBreakfast.id -> BREAKFAST
            bottomSheetBinding!!.optionLunch.id -> LUNCH
            bottomSheetBinding!!.optionDinner.id -> DINNER
            bottomSheetBinding!!.optionSnack.id -> SNACK
            else -> SNACK
        }
    }


    //Clear all the fields after a recipe is saved
    private fun clearForm() {
        fragmentAddBinding.recipeDescEditText.text.clear()
        fragmentAddBinding.recipeTitleEditText.text?.clear()
        fragmentAddBinding.recipeImageView.setImageDrawable(null)

        bottomSheetBinding!!.recipeIngredientEditText.text.clear()
        bottomSheetBinding!!.switchColBtn.isChecked = false
        bottomSheetBinding!!.switchFavBtn.isChecked = false

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ADD_RECIPE_IMAGE_LOC = "image/*"
    }

}


