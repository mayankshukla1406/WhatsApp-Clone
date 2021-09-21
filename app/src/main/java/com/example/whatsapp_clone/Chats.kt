package com.example.whatsapp_clone

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class Chats : Fragment() {
    private lateinit var chatsRecyclerView: RecyclerView
    private lateinit var chatsLayoutManager: RecyclerView.LayoutManager
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var fstore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val chatsInfo = arrayListOf<ChatModal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        chatsRecyclerView = view.findViewById(R.id.chatContentRecyclerView)
        chatsLayoutManager = LinearLayoutManager(context as Activity)
        chatsAdapter = ChatsAdapter(context as Activity, chatsInfo)
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        fstore.collection("chats").whereArrayContainsAny(
            "uids",
            arrayListOf(auth.currentUser!!.uid)
        ).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d("", "")
            } else {
                if (!snapshot?.isEmpty!!) {
                    chatsInfo.clear()
                    val list = snapshot.documents
                    for (doc in list) {
                        fstore.collection("chats").document(doc.id).collection("message")
                            .orderBy("messageId", Query.Direction.DESCENDING)
                            .addSnapshotListener { messagesnapshot, exception ->
                                if (exception != null) {
                                    Log.d("error", "Some Error Occured")
                                } else {
                                    if (!messagesnapshot!!.isEmpty) {
                                        Log.d("messageSnapshot", doc.id)
                                        val id = messagesnapshot.documents[0]
                                        val message = id.getString("message").toString()
                                        val receiver = id.getString("messageReceiver").toString()
                                        val sender = id.getString("messageSender").toString()
                                        Log.d("messageDocument",receiver )
                                        if(receiver==auth.currentUser!!.uid) {
                                            val obj =
                                                ChatModal(
                                                    sender,
                                                    message,
                                                    "https://latestoutfits.files.wordpress.com/2018/03/tony-stark-infinity-war-hoodie.jpg?w=638",
                                                    doc.id
                                                )
                                            chatsInfo.add(obj)
                                        }
                                        else
                                        {
                                            val obj =
                                                ChatModal(
                                                    receiver,
                                                    message,
                                                    "https://latestoutfits.files.wordpress.com/2018/03/tony-stark-infinity-war-hoodie.jpg?w=638",
                                                    doc.id
                                                )
                                            chatsInfo.add(obj)
                                        }
                                        chatsRecyclerView.adapter = chatsAdapter
                                        chatsRecyclerView.layoutManager = chatsLayoutManager
                                        chatsRecyclerView.addItemDecoration(
                                            DividerItemDecoration(
                                                chatsRecyclerView.context,
                                                (chatsLayoutManager as LinearLayoutManager).orientation
                                            )
                                        )
                                    }
                                }
                            }
                    }
                }
            }
        }
        return view
    }
}