package com.example.whatsapp_clone

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp_clone.Activity.MenuActivity
import com.squareup.picasso.Picasso

class ContactsAdapter(val context: Context, private val contactList: ArrayList<User>) :
    RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {
    class ContactsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val email: TextView = view.findViewById(R.id.txtEmail)
        val status: TextView = view.findViewById(R.id.txtStatus)
        val image: ImageView = view.findViewById(R.id.imgProfileImage)
        val userContent : CardView = view.findViewById(R.id.userContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val contactView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_contacts, parent, false)
        return ContactsViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val list = contactList[position]
        holder.name.text = list.profileName
        holder.email.text = list.profileEmail
        holder.status.text = list.profileStatus
        Picasso.get().load(list.profilePicture).error(R.drawable.profile).into(holder.image)
        holder.userContent.setOnClickListener {
            val intent = Intent(context, MenuActivity::class.java).also {
                it.putExtra("OptionName", "contactMessaging")
                it.putExtra("chatroomID", list.chatRoomId)
                it.putExtra("friendUID",list.profileUid)
                it.putExtra("friendName",list.profileName)
            }
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return contactList.size
    }

}