package com.mact.GetStepGo


import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*


class SingUpActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var userDataDatabase : DatabaseReference
    private lateinit var user : FirebaseAuth

    override fun onBackPressed() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        user = FirebaseAuth.getInstance()
        ibtnToDashboard.setOnClickListener {

            if (validateFName() && validateLName() && validateEmail() && validateDOB() && validateWeight() && validateHeight() && validatePassword()) {
                registerUser()

            }
        }
        tvbtnToSignUp.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }

        }

        rbtnMale.setOnClickListener {
            ivGender.setImageResource(R.drawable.male)
        }
        rbtnFemale.setOnClickListener {
            ivGender.setImageResource(R.drawable.female)
        }

    }
    private fun showDialog(activity: Activity?, msg: String?) {
        val dialog = Dialog(activity!!, R.style.AppTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loadingscreen)
        dialog.show()
//        val timer = Timer()
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                dialog.dismiss()
//                timer.cancel()
//            }
//        }, 4000)
    }
    private fun registerUser (){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        user.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(SingUpActivity()){ task->
                if (task.isSuccessful){
                    showDialog(this,"SignUp Successful")
                    beginRegistration()
                }
                else{
                    Toast.makeText(
                        this,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.addOnFailureListener (SingUpActivity() ){
                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun beginRegistration(){
        val url = getString(R.string.firebase_db_location)
        val firstName = etFirstName.text.toString().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
        val lastName =etLastName.text.toString().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
        val name = "$firstName $lastName"
        val email = etEmail.text.toString()
        val dob = etDOB.text.toString()
        val weight = etWeight.text.toString()
        val height = etHeight.text.toString()
        val userName = emailToUserName(email)
        val password = etPassword.text.toString()
        val checkedGenderRadioButtonId = rgGender.checkedRadioButtonId
        val gender = findViewById<RadioButton>(checkedGenderRadioButtonId).text
        val currentDate = getDate()
        user.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent Successfully to "+ user.currentUser?.email.toString())
                    database = FirebaseDatabase.getInstance(url).getReference("users")
                    val users = Users(firstName, lastName, email, weight, height, password)
                    userDataDatabase = FirebaseDatabase.getInstance(url).getReference("userData")
                    val userData = UserData(email,0,0F,0F,0,0,0F,0F,0,currentDate)
                    userDataDatabase.child(userName).setValue(userData).addOnSuccessListener {
                        Log.d("UserData","Successfully Initialized Userdata")
                    }.addOnFailureListener {
                        Log.d("UserData","Failed to Initialize Userdata")
                    }
                    database.child(userName).setValue(users).addOnSuccessListener {
                        etFirstName.text.clear()
                        etLastName.text.clear()
                        etEmail.text.clear()
                        etDOB.text.clear()
                        etWeight.text.clear()
                        etHeight.text.clear()
                        etPassword.text.clear()
                        Log.d(
                            "MyActivity",
                            "$firstName $lastName @($email) , $gender" + " born on $dob has height ${height}cm & Weight ${weight}kg , Registered as an User"
                        )
                        Log.d("userCurrent", "${user.currentUser}")
                        Toast.makeText(
                            this,
                            "$firstName, Verify your Email to Continue",
                            Toast.LENGTH_LONG
                        ).show()
                        Handler().postDelayed({
                            user.signOut()
                            Intent(this, LoginActivity::class.java).also {
                                startActivity(it)
                                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                                finish()
                            }
                        }, 1500) // 1500 is the delayed time in milliseconds.
                    }.addOnFailureListener {
                        Log.d("SignUp", "Failed TO Signup")
                        Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                    }

                }
            }

    }
    private fun getDate() : String{
        val c =Calendar.getInstance()
        var day = c.get(Calendar.DAY_OF_MONTH).toString()
        var month =  (c.get(Calendar.MONTH) + 1).toString()
        if(day.length<2){
            day = "0$day"
        }
        if(month.length<2){
            month = "0$month"
        }
        val year = c.get(Calendar.YEAR).toString()
        val date = "$day$month$year"
        return date
    }
    private fun validateFName() : Boolean{
        val first = etFirstName.text.toString().trim()
        if (first.isEmpty()){
            etFirstName.error = "Enter Your First Name"
           return false
        }
        else
            etFirstName.error=null
            return true
    }
    private fun validateLName() : Boolean{
        val last = etLastName.text.toString().trim()
        if (last.isEmpty()){
                etLastName.error = "Enter Your Last Name"
                return false
        }
        else
            etLastName.error=null
            return true
    }
    private fun validateDOB() : Boolean{
        val dob = etDOB.text.toString().trim()
        val dobRegex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}\$".toRegex()
        if (dob.isEmpty()){
                etDOB.error = "Enter Your DATE OF BIRTH"
                return false
        }else if(!dob.matches(dobRegex)){
            etDOB.error = "Invalid D.O.B"
            return false
        }
        else
            etLastName.error=null
            return true
    }
    private fun validateEmail() : Boolean{
        val email = etEmail.text.toString().trim()
        val emailRegex : Regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$".toRegex()
        if (email.isEmpty()){
            etEmail.error = "Enter Your Email"
                return false
        }
        else if(!email.matches(emailRegex)){
            etEmail.error = "Invalid Email Address"
            return false
        }
        else
        {
            etEmail.error=null
            return true
        }
    }
    private fun validatePassword() : Boolean{
        val password = etPassword.text.toString().trim()
        val passwordRegex : Regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,16}\$".toRegex()
//        Regex Conditions:
//        Min 1 uppercase letter.
//        Min 1 lowercase letter.
//        Min 1 special character.
//        Min 1 number.
//        Min 8 characters.
//        Max 30 characters.

        if (password.isEmpty()){
            etPassword.error = "Enter Your Password"
                return false
        }
        else if(!password.matches(passwordRegex)) {
            etPassword.error = "Must be 8-16 characters Long, Containing an UpperCase, Lowercase and Number"
            return false
        }
        else{

            etPassword.error = null
            return true
        }
    }
    private fun validateWeight() : Boolean{
        val emojiCode = "1F605" //Enter Code without u prefix
        val weight = etWeight.text.toString().trim()
        if (weight.isEmpty()){
            etWeight.error = "Enter Your Weight"
            return false
        } else if(weight.toInt()>635)
        {
            etWeight.error = "You must be Joking Right ${getEmojiByUnicode(emojiCode)}.\nPls Enter Your Weight Correctly "
        }
        else
            etWeight.error=null
        return true
    }
    private fun validateHeight() : Boolean{
        val emojiCode = "1F64B" //Enter Code without u prefix
        val height = etHeight.text.toString().trim()
        if (height.isEmpty()){
            etHeight.error = "Enter Your Height"
                return false
        }else if(height.toInt()>250)
        {
            etHeight.error = "Hey Tall Guy ${getEmojiByUnicode(emojiCode)}.\nPls Enter Your Height Correctly "
        }
        else
            etHeight.error=null
            return true
    }
    private fun getEmojiByUnicode(reactionCode: String): String {
        val code = reactionCode.toInt(16)
        return String(Character.toChars(code))
    }
    private fun emailToUserName(email : String ): String{
//        var count = 0
//        for (i in email){
//            if(i=='@'){
//                break
//            }
//            count++
//        }
//        var userName= email.slice(0 until count)
        var userName = email
        val regex = Regex("[^A-Za-z0-9]")
        userName = regex.replace(userName, "")
        return userName
    }
}

