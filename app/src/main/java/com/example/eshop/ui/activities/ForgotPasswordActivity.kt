package com.example.eshop.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.example.eshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var auth:FirebaseAuth
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

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




        btn_submit.setOnClickListener{

            if (!TextUtils.isEmpty(et_forgot_email.text.toString().trim())){
                showDialog(resources.getString(R.string.please_wait))
                email = et_forgot_email.text.toString().trim()
                CoroutineScope(Dispatchers.IO).launch {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener {
                        if (it.isSuccessful){
                            showErrorSnackBar(resources.getString(R.string.email_sent_successfully),false)
                            hideDialog()
                        }else{
                            showErrorSnackBar(it.exception.toString(),true)
                        }
                    }
                }
            }else{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)

            }

        }

        setupActionBar()

    }
    private fun setupActionBar(){
        val toolBar = toolbar_forgot_password_activity
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolBar.setNavigationOnClickListener{
            onBackPressed()
        }
    }
}