package com.mact.GetStepGo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(){
    private lateinit var database : DatabaseReference
    private lateinit var user : FirebaseAuth

    override fun onBackPressed() {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val url = getString(R.string.firebase_db_location)
        user = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance(url).getReference("users")
        if(user.currentUser!=null){
            user.currentUser?.let {
                Log.d("userCurrent",it.email.toString())
                tvUsername.text = it.email
            }
        }
        btnToDashboard.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
        }
        btnLogout.setOnClickListener{
            Log.d("userCurrentAtProfile", user.currentUser?.email.toString()+" has Logged Out")
            user.signOut()
            Intent(this,LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }
        }

    }
}