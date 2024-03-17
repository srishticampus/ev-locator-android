package com.project.evlocator.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.activities.FaqActivity
import com.project.evlocator.activities.MainActivity
import com.project.evlocator.api.ApiUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class ProfileFragment : Fragment() {

    private lateinit var changeImage: LinearLayout
    private lateinit var profileImage: ImageView
    private var imageFile: File? = null
    private lateinit var nameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var phoneEt: EditText
    private lateinit var submitBt: TextView
    private lateinit var logOutBt: TextView
    private lateinit var faqBtn: TextView
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    var inputStream: InputStream? = null
    var imageUri: Uri? = null

    private val STORAGE_PERMISSION_CODE = 101
    private val RESULT_LOAD_PRO_IMAGE = 106


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(
            R.layout.fragment_profile,
            container, false
        )

        changeImage = view.findViewById(R.id.image_edit_profile)
        profileImage = view.findViewById(R.id.profile_picture)
        nameEt = view.findViewById(R.id.fullName_editProfile_et)
        emailEt = view.findViewById(R.id.emailId_editProfile_et)
        phoneEt = view.findViewById(R.id.phoneNumber_editProfile_et)
        submitBt = view.findViewById(R.id.submit_button_editProfile)
        logOutBt = view.findViewById(R.id.logout_button_editProfile)
        faqBtn = view.findViewById(R.id.faq_btn)

        sharedPreferencesManager = SharedPreferencesManager(view.context)
        val userId: String = sharedPreferencesManager.getUserId()

        viewProfile(userId)

        changeImage.setOnClickListener {

            pickMedia.launch("image/*")

        }

        submitBt.setOnClickListener {
            updateApiCall(userId)
        }
        logOutBt.setOnClickListener {
            sharedPreferencesManager.saveLoginStatus("loggedOut")
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        faqBtn.setOnClickListener {
            startActivity(Intent(context, FaqActivity::class.java))
        }


        return view
    }


    val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it!!
        profileImage.setImageURI(it)
    }


    fun updateApiCall(userId: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val filesDir = requireContext().filesDir
            val file = File(filesDir, "image.jpg")

            val inputStream = imageUri?.let { requireContext().contentResolver.openInputStream(it) }
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)

            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

            Log.i("pathhh", file.path.toString())

            val multipartBody: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.name, requestFile)

            val userid = RequestBody.create(MultipartBody.FORM, userId)
            val name = RequestBody.create(MultipartBody.FORM, nameEt.text.toString())
            val email = RequestBody.create(MultipartBody.FORM, emailEt.text.toString())
            val phone = RequestBody.create(MultipartBody.FORM, phoneEt.text.toString())

            val map = HashMap<String?, RequestBody?>()
            map["userid"] = userid
            map["name"] = name
            map["email"] = email
            map["phone"] = phone

            val response = ApiUtilities.getInstance().updateUserProfile(map, multipartBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val response = response.body()
                    if (response != null) {
                        if (response.status == true) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    fun viewProfile(userId: String) {

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val api = ApiUtilities.getInstance()
                val result = api.viewUserProfile(userId)
                //  Toast.makeText(context, userId, Toast.LENGTH_SHORT).show()
                result.body()?.let { root ->
                    if (root.status) {
                        withContext(Dispatchers.Main) {


                            nameEt.setText(root.userData[0].name)
                            phoneEt.setText(root.userData[0].phone)
                            emailEt.setText(root.userData[0].email)
                            Glide.with(requireContext()).load(root.userData[0].image)
                                .into(profileImage)
                        }

                    }
                } ?: Toast.makeText(
                    context,
                    "Server Error: ${result.message()}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (_: Exception) {

        }

    }

}