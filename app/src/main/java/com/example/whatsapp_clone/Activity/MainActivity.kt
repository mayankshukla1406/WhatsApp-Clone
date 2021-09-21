package com.example.whatsapp_clone.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.whatsapp_clone.Calls
import com.example.whatsapp_clone.Chats
import com.example.whatsapp_clone.R
import com.example.whatsapp_clone.Status
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var appPagerAdapter: AppPagerAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var showContacts: FloatingActionButton
    private val titles = arrayListOf("Chats", "Status", "Calls")
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbarMain)
        tabLayout = findViewById(R.id.tabLayoutMain)
        viewPager2 = findViewById(R.id.viewPager2Main)
        showContacts = findViewById(R.id.btContacts)
        auth = FirebaseAuth.getInstance()
        toolbar.title = "WhatsappClone"
        setSupportActionBar(toolbar)
        appPagerAdapter = AppPagerAdapter(this)
        viewPager2.adapter = appPagerAdapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()
        showContacts.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("OptionName", "friends")
            startActivity(intent)
        }
    }
    class AppPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Chats()
                1 -> Status()
                2 -> Calls()
                else -> Chats()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("OptionName", "profile")
                startActivity(intent)
            }
            R.id.about -> {
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("OptionName", "about")
                startActivity(intent)
            }
            R.id.logout -> {
                auth.signOut()
                val intent = Intent(this, AuthenticationActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.searchContacts->{
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("OptionName", "search")
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}