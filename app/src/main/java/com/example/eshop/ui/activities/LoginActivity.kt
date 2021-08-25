package com.example.eshop.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.User
import com.example.eshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*

class LoginActivity : BaseActivity(), View.OnClickListener{

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        tv_register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_forgot_password.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        if (v != null){
            when(v.id) {
                R.id.btn_login -> {
                    loginAccount()
                }
                R.id.tv_forgot_password -> {
                    startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                }
                R.id.tv_register -> {  startActivity(Intent(this, RegisterActivity::class.java)) }
            }
        }
    }

    private fun validateRegisterDetails(): Boolean{
        return when{
            TextUtils.isEmpty(et_login_email.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_login_password.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            else -> true
        }
    }

    private fun loginAccount(){
        if (validateRegisterDetails()){
            showDialog(resources.getString(R.string.please_wait))
            val email = et_login_email.text.toString().trim()
            val password = et_login_password.text.toString().trim()
            CoroutineScope(Dispatchers.IO).launch {
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if (it.isSuccessful){
                        FirestoreClass().getUserDetails(this@LoginActivity)
//                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
//                        finish()
                        showErrorSnackBar(resources.getString(R.string.login_successful),false)
                    }else{
                        //直接用Task去呼叫 exception
                            hideDialog()
                        showErrorSnackBar(it.exception?.message.toString(),true)
                    }
                }
            }
        }
    }
    //這個是在 FirestoreClass呼叫，並傳來的 user
    fun userLoggedInSuccess(user: User){
        hideDialog()
        if(user.profileCompleted){
            val intent: Intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else{
            val intent: Intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }
        finish()
    }
}