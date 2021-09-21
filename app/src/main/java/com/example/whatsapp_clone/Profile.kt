package com.example.whatsapp_clone

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class Profile : Fragment() {
    private lateinit var profileNameShow: TextView
    private lateinit var profileEmailShow: TextView
    private lateinit var profileStatusShow: TextView
    private lateinit var profilePicture: CircleImageView
    private lateinit var profilePictureAdd: ImageView
    private lateinit var profileNameEdit: TextInputLayout
    private lateinit var profileEmailEdit: TextInputLayout
    private lateinit var profileStatusEdit: TextInputLayout
    private lateinit var editName: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editStatus: TextInputEditText
    private lateinit var profileUpdate: Button
    private lateinit var profileSave: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var db: DocumentReference
    private lateinit var userid: String
    private lateinit var image: ByteArray
    private lateinit var storageReference: StorageReference
    val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        uploadImage(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        userid = auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference.child("$userid/profilePhoto")
        profileNameEdit = view.findViewById(R.id.profile_name)
        profileNameShow = view.findViewById(R.id.txtProfileName)
        profileEmailEdit = view.findViewById(R.id.profile_email)
        profileEmailShow = view.findViewById(R.id.txtProfileEmail)
        profilePicture = view.findViewById(R.id.imgProfileImage)
        profileStatusShow = view.findViewById(R.id.txtProfileStatus)
        profileStatusEdit = view.findViewById(R.id.profile_status)
        profilePictureAdd = view.findViewById(R.id.imgAddProfileImage)
        editName = view.findViewById(R.id.etProfileName)
        editEmail = view.findViewById(R.id.etProfileEmail)
        editStatus = view.findViewById(R.id.etProfileStatus)
        profileUpdate = view.findViewById(R.id.btUpdateProfile)
        profileSave = view.findViewById(R.id.btSaveProfile)
        progressBar = view.findViewById(R.id.profileProgressBar)
        profileUpdate.visibility = View.VISIBLE
        db = fstore.collection("users").document(userid)
        db.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("Error", "Unable to fetch data")
            } else {
                profileNameShow.text = value?.getString("userName")
                profileEmailShow.text = value?.getString("userEmail")
                profileStatusShow.text = value?.getString("userStatus")
                Picasso.get().load(value?.getString("userProfilePhoto")).error(R.drawable.profile)
                    .into(profilePicture)
            }

        }
        profileUpdate.setOnClickListener {
            profileNameShow.visibility = View.GONE
            profileEmailShow.visibility = View.GONE
            profileStatusShow.visibility = View.GONE
            profileNameEdit.visibility = View.VISIBLE
            profileEmailEdit.visibility = View.VISIBLE
            profileStatusEdit.visibility = View.VISIBLE
            profileSave.visibility = View.VISIBLE
            profileUpdate.visibility = View.GONE
            editName.text =
                Editable.Factory.getInstance().newEditable(profileNameShow.text.toString())
            editEmail.text =
                Editable.Factory.getInstance().newEditable(profileEmailShow.text.toString())
            editStatus.text =
                Editable.Factory.getInstance().newEditable(profileStatusShow.text.toString())
        }
        profileSave.setOnClickListener {
            profileNameShow.visibility = View.VISIBLE
            profileEmailShow.visibility = View.VISIBLE
            profileStatusShow.visibility = View.VISIBLE
            profileUpdate.visibility = View.VISIBLE
            profileNameEdit.visibility = View.GONE
            profileEmailEdit.visibility = View.GONE
            profileStatusEdit.visibility = View.GONE
            profileSave.visibility = View.GONE
            val obj = mutableMapOf<String, String>()
            obj["userName"] = editName.text.toString()
            obj["userEmail"] = editEmail.text.toString()
            obj["userStatus"] = editStatus.text.toString()
            db.set(obj).addOnSuccessListener {
                Log.d("Success", "Data Successfully Updated")
            }
        }
        profilePictureAdd.setOnClickListener {
            capturePhoto()
        }
        return view
    }

    private fun capturePhoto() {
        register.launch(null)
    }

    private fun uploadImage(it: Bitmap?) {
        val baos = ByteArrayOutputStream()
        it?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        image = baos.toByteArray()
        storageReference.putBytes(image).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                val obj = mutableMapOf<String, String>()
                obj["userProfilePhoto"] = it.toString()
                db.update(obj as Map<String, Any>).addOnSuccessListener {
                    Log.d("onSucces", "ProfilePictureUploaded")
                }
            }
        }
    }
}