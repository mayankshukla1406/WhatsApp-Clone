package com.example.whatsapp_clone

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp_clone.Activity.MenuActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class Contacts : Fragment() {
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactLayoutManager: RecyclerView.LayoutManager
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var fstore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userid : String
    private val contactInfo = arrayListOf<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView)
        contactLayoutManager = LinearLayoutManager(context as Activity)
        auth = FirebaseAuth.getInstance()
        userid = auth.currentUser!!.uid
        fstore = FirebaseFirestore.getInstance()
        fstore.collection("users").document(userid).collection("friends").get().addOnSuccessListener {
            if (!it.isEmpty) {
                contactInfo.clear()
                val list = it.documents
                for (doc in list) {
                    val friendsID = doc.id
                    val chatRoomID = doc.getString("chatroomid")
                    fstore.collection("users").document(friendsID).addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.d("", "")
                        }
                        else
                        {
                            val obj = User(
                                friendsID,
                                value!!.getString("userName").toString(),
                                value.getString("userEmail").toString(),
                                value.getString("userStatus").toString(),
                                value.getString("userProfilePhoto").toString(),
                                chatRoomID.toString()
                            )
                            contactInfo.add(obj)
                            contactsAdapter = ContactsAdapter(context as Activity, contactInfo)
                            contactsRecyclerView.adapter = contactsAdapter
                            contactsRecyclerView.layoutManager = contactLayoutManager
                            contactsRecyclerView.addItemDecoration(
                                DividerItemDecoration(
                                    contactsRecyclerView.context,
                                    (contactLayoutManager as LinearLayoutManager).orientation
                                )
                            )
                        }
                        }
                    }
            }
        }
        return view
    }
}