package com.example.eshop.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.User
import com.example.eshop.utils.Constants
import com.example.eshop.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*
import timber.log.Timber

class SettingsActivity : BaseActivity(),View.OnClickListener {

    private lateinit var mUserDetails: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupActionBar()
        btn_logout.setOnClickListener(this)
        tv_edit.setOnClickListener(this)
    }


    private fun setupActionBar(){
        setSupportActionBar(toolbar_setting_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }

        toolbar_setting_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun getUserDetails(){
        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User){
        mUserDetails = user
        hideDialog()
        GlideLoader(this).loadUserPicture(Uri.parse(user.image),iv_user_photo)
        v_settings_people.visibility = View.GONE
        tv_name.text = "${user.firstName}" + "${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = user.mobile.toString()
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Settings onResume")
        getUserDetails()
    }

    override fun onClick(v: View?) {

        if (v != null){
            when(v.id){

                R.id.btn_logout ->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.tv_edit -> {
                    val intent = Intent(this,UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                    startActivity(intent)
                }
            }
        }

    }

}