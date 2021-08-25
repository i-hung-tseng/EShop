package com.example.eshop.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception

class RegisterActivity : BaseActivity(),View.OnClickListener {

    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        setupActionBar()

        btn_register.setOnClickListener(this)
        tv_login.setOnClickListener(this)

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
    }

    private fun registerAccount(){

        if (validateRegisterDetails()) {
            showDialog(resources.getString(R.string.please_wait))
            val email = findViewById<EditText>(R.id.et_email).text.toString().trim()
            val password = findViewById<EditText>(R.id.et_confirm_password).text.toString().trim()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    //先透過 email&password 來辦帳號
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser? = task.result?.user
                                if (firebaseUser != null){
                                    // TODO: 2021/8/20 check user had already created other details!
                                    val user = User(
                                            firebaseUser.uid,
                                            et_first_name.text.toString().trim(),
                                            et_last_name.text.toString().trim(),
                                            et_email.text.toString().trim(),
                                    )
                                    FirestoreClass().registerUser(this@RegisterActivity,user)
                                }

                                }
                            }

//                    FirebaseAuth.getInstance().signOut()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showErrorSnackBar(e.message.toString(), true)
                        return@withContext false
                    }
                }
                hideDialog()
            }
        } else {
            hideDialog()
            Timber.d("!valid")

        }
    }

    fun userRegistrationSuccess(){
            Toast.makeText(this@RegisterActivity,resources.getString(R.string.register_successful),Toast.LENGTH_SHORT).show()
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                findViewById<EditText>(R.id.et_first_name).text.toString().trim()
            ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_last_name).text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_email).text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_password).text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(
                findViewById<EditText>(R.id.et_confirm_password).text.toString().trim()
            ) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            findViewById<EditText>(R.id.et_password).text.toString()
                .trim() != findViewById<EditText>(R.id.et_confirm_password).text.toString()
                .trim() -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !findViewById<CheckBox>(R.id.cb_terms_and_condition).isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
//                showErrorSnackBar(resources.getString(R.string.register_successful),false)
                true
            }
        }
    }


    private fun setupActionBar() {
        val toolbar =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            //顯示返回按鈕
            actionBar.setDisplayHomeAsUpEnabled(true)
            //設置返回按鈕圖案
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        //返回事件
        toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.btn_register -> { registerAccount()}
                R.id.tv_login -> { onBackPressed()}
            }
        }
    }
}