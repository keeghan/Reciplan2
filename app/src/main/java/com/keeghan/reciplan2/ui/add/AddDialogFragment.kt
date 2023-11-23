package com.keeghan.reciplan2.ui.add

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.radiobutton.MaterialRadioButton
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.EditRecipeDialogBinding
import com.keeghan.reciplan2.utils.Constants
import com.yalantis.ucrop.UCrop
import java.io.File

class AddDialogFragment(private val editRecipe: Recipe) : DialogFragment() {

    private lateinit var viewModel: AddViewModel
    private val binding by lazy { EditRecipeDialogBinding.inflate(layoutInflater) }

    private lateinit var tempImageUri: Uri
    private lateinit var webpCompressedImageFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AddViewModel::class.java]
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme_transparent);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = binding.root

        populateWithEditRecipe()
        setButtonSheetOnClickListeners()

        //Launch activity for result for image
        binding.chooseImageButton.setOnClickListener { getContent.launch(ADD_RECIPE_IMAGE_LOC) }

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = (resources.displayMetrics.widthPixels * 9) / 10
            val height = (resources.displayMetrics.heightPixels * 2) / 3
            dialog.window?.setLayout(width, height)
            dialog.window?.setWindowAnimations(R.style.DialogAnimation) // Add this line

        }
    }



    //Launch Activity for Result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        //define uri(name and location of un_cropped file)
        tempImageUri = Uri.fromFile(
            File(
                requireActivity().filesDir,
                "${binding.recipeTitleEditText.text?.trim()?.replace(Regex("\\s"), "")}${System.nanoTime()}.jpg"
            )
        )

        //define uri(name and location of cropped file)
        webpCompressedImageFile = File(
            requireActivity().filesDir,
            "${binding.recipeTitleEditText.text?.trim()?.replace(Regex("\\s"), "")}${System.nanoTime()}_c.webp"
        )

        //crop image and save in croppedImageUri
        if (uri != null) {
            UCrop.of(uri, tempImageUri).withAspectRatio(1F, 1F).withMaxResultSize(400, 400).start(requireActivity())
        }

        Glide.with(requireContext()).load(tempImageUri).into(binding.recipeImageView)
    }

    //populate fields from recipe passed from edit command
    private fun populateWithEditRecipe() {
        binding.textView.text = getString(R.string.editRecipe)
        binding.recipeTitleEditText.setText(editRecipe.name)
        binding.recipeDescEditText.setText(editRecipe.userDirection)
        Glide.with(this).load(editRecipe.imageUrl).into(binding.recipeImageView)

        with(binding) {
            timePicker.value = editRecipe.mins
            recipeIngredientEditText.setText(editRecipe.userIngredient)
            switchColBtn.isChecked = editRecipe.collection
            switchFavBtn.isChecked = editRecipe.favorite
            radioGroup.check(
                when (editRecipe.type) {
                    Constants.BREAKFAST -> optionBreakfast.id
                    Constants.LUNCH -> optionLunch.id
                    Constants.DINNER -> optionDinner.id
                    Constants.SNACK -> optionSnack.id
                    else -> throw IllegalStateException("Recipe Type invalid")
                }
            )
        }
    }


    //Update Recipe
    private fun updateRecipe() {
        val checkedTypeID = binding.radioGroup.checkedRadioButtonId
        val radioButtonId = binding.root.findViewById<MaterialRadioButton>(checkedTypeID).id
        val imageUrl =
            if (!::tempImageUri.isInitialized) editRecipe.imageUrl else webpCompressedImageFile.path

        val recipe = Recipe(
            _id = editRecipe._id,
            name = binding.recipeTitleEditText.text?.trim().toString(),
            mins = binding.timePicker.value,
            imageUrl = imageUrl,
            favorite = binding.switchFavBtn.isChecked,
            ingredients = binding.recipeIngredientEditText.text?.trim()?.lines()!!.size,
            userCreated = true,
            collection = binding.switchColBtn.isChecked,
            userDirection = binding.recipeDescEditText.text?.trim().toString(),
            userIngredient = binding.recipeIngredientEditText.text?.trim().toString(),
            type = typeOptionSelected(radioButtonId)
        )

        //if new recipe image is selected compress or else just update
        if (::tempImageUri.isInitialized)
            viewModel.updateRecipe(tempImageUri, webpCompressedImageFile, recipe, editRecipe.imageUrl)
        else viewModel.updateRecipe(recipe)

        viewModel.errorMsg.observe(viewLifecycleOwner) { if (it == Constants.SUCCESS) showToast(getString(R.string.recipe_updated)) }

        dismiss()
    }

    private fun setButtonSheetOnClickListeners() {
        binding.switchColBtn.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) binding.switchFavBtn.isChecked = false
        }

        binding.switchFavBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.switchColBtn.isChecked = true
        }
        //Click on save button to save Recipe
        binding.saveButton.setOnClickListener { if (isRecipeValid()) updateRecipe() }

        binding.timePicker.maxValue = 300
        binding.timePicker.minValue = 15
        binding.timePicker.wrapSelectorWheel = true
    }

    //Validate if Recipe Title and Description are filled and Image is selected
    private fun isRecipeValid(): Boolean {
        val title = binding.recipeTitleEditText.text?.trim().toString()
        val description = binding.recipeDescEditText.text.trim().toString()
        val ingredients = binding.recipeIngredientEditText.text.trim().toString()
        return when {
            title.isBlank() -> {
                binding.recipeTitleEditText.error = getString(R.string.empty)
                false
            }

            description.isBlank() -> {
                binding.recipeDescEditText.error = getString(R.string.empty)
                false
            }

            ingredients.isBlank() -> {
                binding.recipeIngredientEditText.error = getString(R.string.empty)
                false
            }

            else -> true
        }
    }

    private fun typeOptionSelected(radioBtnId: Int): String {
        return when (radioBtnId) {
            binding.optionBreakfast.id -> Constants.BREAKFAST
            binding.optionLunch.id -> Constants.LUNCH
            binding.optionDinner.id -> Constants.DINNER
            binding.optionSnack.id -> Constants.SNACK
            else -> Constants.SNACK
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ADD_RECIPE_IMAGE_LOC = "image/*"
    }
}

