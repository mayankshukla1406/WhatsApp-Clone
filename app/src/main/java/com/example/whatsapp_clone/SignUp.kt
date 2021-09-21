package com.example.whatsapp_clone

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class SignUp : Fragment() {
    private lateinit var enterEmail: TextInputEditText
    private lateinit var enterPassword: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var fauth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var db: DocumentReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        enterEmail = view.findViewById(R.id.etSignUpEmail)
        enterPassword = view.findViewById(R.id.etSignUpPassword)
        confirmPassword = view.findViewById(R.id.etSignUpConfirmPassword)
        progressBar = view.findViewById(R.id.signUpProgressBar)
        signUpButton = view.findViewById(R.id.btSignUp)
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        signUpButton.setOnClickListener {
            val email = enterEmail.text.toString()
            val password = enterPassword.text.toString()
            val confirmPass = confirmPassword.text.toString()
            if (TextUtils.isEmpty(email)) {
                enterEmail.error = " Email is Required To Create Account"
            } else if (TextUtils.isEmpty(password)) {
                enterPassword.error = "Password is required to Create Account"
            } else
                if (password.length < 6) {
                    enterPassword.error = "Password must be greater than 6 characters in size"
                } else
                    if (password != confirmPass) {
                        confirmPassword.error = "Both Passwords not Matched"
                    } else {
                        progressBar.visibility = View.VISIBLE
                        createAccount(email, password)
                    }
        }
        return view
    }

    private fun createAccount(em: String, pass: String) {
        fauth.createUserWithEmailAndPassword(em, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("whatHapeen", "What Happen")
                val userinfo = fauth.currentUser?.uid
                db = fstore.collection("users").document(userinfo.toString())
                val obj = mutableMapOf<String, String>()
                obj["userEmail"] = em
                obj["userPassword"] = pass
                obj["userStatus"] = ""
                obj["userName"] = ""
                db.set(obj).addOnSuccessListener {
                    Log.d("whatHapeen", "What Happen2")
                    Log.d("onSucess", "User Created Successfully")
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}