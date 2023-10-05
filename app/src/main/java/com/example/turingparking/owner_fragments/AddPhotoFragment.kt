package com.example.turingparking.owner_fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.turingparking.R

/**
 * A simple [Fragment] subclass.
 * Use the [AddPhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddPhotoFragment : Fragment() {

    private val viewModel: AddViewModel by activityViewModels()
    private lateinit var photoImage: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_add_photo, container, false)
        photoImage =fragmentView.findViewById<ImageView>(R.id.editParkingImage) as ImageView
        setPlaceHolder()

        val cancelButton = fragmentView.findViewById<ImageButton>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            setPlaceHolder()
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                photoImage.setImageURI(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        val photoButton = fragmentView.findViewById<ImageButton>(R.id.photo_button)
        photoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val cameraButton = fragmentView.findViewById<ImageButton>(R.id.camera_button)
        cameraButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val nextButton = fragmentView.findViewById<View>(R.id.next_button) as Button
        nextButton.setOnClickListener {
            viewModel.saveImage(photoImage.drawable.toBitmap())
            fragmentView.findNavController().navigate(R.id.nav_add_hours)
        }

        return fragmentView
    }



    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun setPlaceHolder(){
        photoImage.setImageResource(R.drawable.parking_image_placeholder)
    }

    companion object {
        private const val TAG = "AddPhotoFragment"
    }
}