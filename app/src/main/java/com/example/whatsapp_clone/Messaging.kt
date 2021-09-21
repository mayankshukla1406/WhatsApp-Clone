package com.example.whatsapp_clone

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

import java.util.*


class Messaging : Fragment() {
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton
    private lateinit var fstore: FirebaseFirestore
    private lateinit var fauth: FirebaseAuth
    private lateinit var messageLayoutManager: RecyclerView.LayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var db: DocumentReference
    private lateinit var userid: String
    private lateinit var friendID : String
    private lateinit var chatroomid : String
    private lateinit var chatID : String
    private lateinit var db1 : DocumentReference
    private lateinit var chatroomUID : String
    private val messageInfo = arrayListOf<MessageModal>()
    private var register : ListenerRegistration? = null
    private var register1 : ListenerRegistration? =null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messaging, container, false)
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView)
        sendMessageButton = view.findViewById(R.id.btSendMessage)
        sendMessageEditText = view.findViewById(R.id.etSendMessage)
        val values = arguments
        if(values!=null)
        {
            friendID = values!!.getString("friendName").toString()
            chatroomid = values.getString("documentID").toString()
            initialization(chatroomid)
        }
        val contactBundle = arguments
        if(contactBundle!=null)
        {
            friendID = values!!.getString("friendUID").toString()
            chatroomid = values.getString("chatRoomID").toString()
            Log.d("logContactbundle",friendID)
            Log.d("logContactbundle",chatroomid)
            fetchChatRoomUID()
        }
        sendMessageButton.setOnClickListener{
            fetchMessageID()
        }
        return view
    }
    private fun fetchChatRoomUID()
    {
        fstore.collection("chats").whereEqualTo("chatroomid",chatroomid).get().addOnSuccessListener { query->
            if(!query.isEmpty)
            {
                chatroomUID = query.documents[0].id
                Log.d("chatroomid",chatroomUID)
                initialization(chatroomUID)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchingMessages(idMessages:String) {
        register1 = fstore.collection("chats").document(idMessages)
            .collection("message")
            .orderBy("messageId",Query.Direction.ASCENDING).addSnapshotListener{chatSnapshot,exception->
                if(exception!=null)
                {
                    Log.d("","")
                }
                else
                {
                    messageInfo.clear()
                    if(!chatSnapshot?.isEmpty!!)
                    {
                        val listChat = chatSnapshot.documents
                        for(chat in listChat)
                        {
                            val chatobj = MessageModal(
                                chat.getString("messageSender").toString(),
                                chat.getString("message").toString(),
                                chat.getString("messageTime").toString()
                            )
                            messageInfo.add(chatobj)
                        }
                        messageRecyclerView.scrollToPosition(messageInfo.size -1)
                        messageAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun fetchMessageID() {
        db = fstore.collection("chats").document(chatroomUID).collection("count").document("chatid")
        sendMessageButton.setOnClickListener {
            register = db.addSnapshotListener{value,error->
                if(error!=null)
                {
                    Log.d("","")
                }
                else
                {
                    chatID = value?.getString("chatid").toString()
                    sendMessage()
                }
            }
        }
    }

    private fun initialization(id : String) {
        fstore = FirebaseFirestore.getInstance()
        fauth = FirebaseAuth.getInstance()
        userid = fauth.currentUser?.uid.toString()
        messageLayoutManager = LinearLayoutManager(context)
        recyclerViewBuild(id)

    }

    private fun recyclerViewBuild(id:String) {
        messageAdapter = MessageAdapter(context as Activity , messageInfo)
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = messageLayoutManager
        fetchingMessages(id)

    }

    private fun sendMessage() {
        register!!.remove()
        val message = sendMessageEditText.text.toString()
        if (TextUtils.isEmpty(message)) {
            sendMessageEditText.error = "Enter some Message to Send"
        } else {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timeStamp = "$hour:$minute"
            val messageObject = mutableMapOf<String, Any>().also {
                it["message"] = message
                it["messageId"] = chatID
                it["messageSender"] = userid
                it["messageReceiver"] = friendID
                it["messageTime"] = timeStamp
            }
            db1 = fstore.collection("chats").document(chatroomUID).collection("message").document()
                db1.set(messageObject).addOnSuccessListener {
                Log.d("onSuccess", "Successfully Send Message")
            }
            val countid = mutableMapOf<String,String>()
            countid["chatid"] = (chatID.toInt()+1).toString()
            db.set(countid).addOnSuccessListener {
                Log.d("onSuccess", "Successfully upDATED count messages")
            }
        }
    }

    override fun onDestroy() {
        register1!!.remove()
        super.onDestroy()
    }
}