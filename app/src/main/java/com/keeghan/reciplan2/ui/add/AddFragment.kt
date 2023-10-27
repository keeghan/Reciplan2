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
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.FragmentAddBinding
import com.yalantis.ucrop.UCrop
import java.io.File


class AddFragment : Fragment() {

    private lateinit var viewModel: AddViewModel
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var tempImageUri: Uri
    private lateinit var webpCompressedImageFile: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this)[AddViewModel::class.java]

        //Compress image and save it iin fileDir
        binding.saveButton.setOnClickListener {
            saveRecipeIfValid()
        }

        //Launch activity for result for image
        binding.chooseImageButton.setOnClickListener {
            getContent.launch(ADD_RECIPE_IMAGE_LOC)
        }

        return view
    }

    //Launch Activity for Result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        //define uri(name and location of un_cropped file)
        tempImageUri = Uri.fromFile(
            File(requireActivity().filesDir, "${binding.recipeTitleEditText.text?.trim()}${System.nanoTime()}.jpg")
        )

        //define uri(name and location of cropped file)
        webpCompressedImageFile = File(
            requireActivity().filesDir,
            "${binding.recipeTitleEditText.text?.trim()}${System.nanoTime()}_compressed.webp"
        )

        //crop image and save in croppedImageUri
        if (uri != null) {
            UCrop.of(uri, tempImageUri)
                .withAspectRatio(1F, 1F)
                .withMaxResultSize(400, 400)
                .start(requireActivity())
        }

        Glide.with(requireContext()).load(tempImageUri)
            .placeholder(R.drawable.ic_launcher_background)
            .centerCrop()
            .into(binding.recipeImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 450
        const val ADD_RECIPE_IMAGE_LOC = "image/*"
    }

    //Save Recipe and reset screen
    private fun saveRecipeIfValid() {
        if (::tempImageUri.isInitialized) {
            val title = binding.recipeTitleEditText.text?.trim().toString()
            val description = binding.recipeDescriptionEditText.text.trim().toString()
            if (title.isNotBlank() && description.isNotBlank()) {
                val recipe = Recipe(
                    name = "aaaaa", direction = 4, ingredients = 3, mins = 3, imageUrl = webpCompressedImageFile.path,
                    favorite = true, userCreated = true, collection = false, type = "breakfast"
                )
                 viewModel.saveRecipe(tempImageUri, webpCompressedImageFile, recipe)
                clearForm()
                showToast("Recipe saved")
            } else {
                showToast("Recipe name and description should not be empty")
            }
        } else {
            showToast("Choose a recipe Image")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun clearForm() {
        binding.recipeDescriptionEditText.text.clear()
        binding.recipeTitleEditText.text?.clear()
        binding.recipeImageView.setImageDrawable(null)
    }

}


