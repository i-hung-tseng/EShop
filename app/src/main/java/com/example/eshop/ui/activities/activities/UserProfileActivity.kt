package com.example.eshop.ui.activities.activities

import android.app.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.User
import com.example.eshop.utils.Constants
import com.example.eshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import timber.log.Timber
import java.io.IOException

class UserProfileActivity : BaseActivity(),View.OnClickListener{

    private lateinit var mUserDetails: User
    //以下這個是 onActivityResult的 Uri 等於 data.data
    private var mSelectedImageFileUri: Uri? = null
    //這個是換算成可下載的 Uri
    private var mUserProfileImageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!}

            setUserDetailsToEdText()

            if (mUserDetails.profileCompleted){
                tv_user_profile_title.text = resources.getString(R.string.title_complete_profile)

            }else{
                setupActionBar()
                tv_user_profile_title.text = resources.getString(R.string.title_edit_your_profile)

            }
        iv_user_photo.setOnClickListener(this)
        btn_user_profile_submit.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when(v.id){


            R.id.iv_user_photo -> {

                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                    Constants.showImageChooser(this)
                }else{
                    permissionPhoto()
                }
            }
            R.id.btn_user_profile_submit -> {


                if (validateUserProfileDetails()){

                    showDialog(resources.getString(R.string.please_wait))
                    //mSelectedImageFileUri是onActivityResult拿到的 data.data
                    if (mSelectedImageFileUri != null){
                        Timber.d("Testing mSelectedImageFileUri != null")
                        FirestoreClass().updateImageToCloudStorage(this,mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE)
                    } else{
                        updateUserProfileDetail()
                        Timber.d("Testing mSelectedImageFileUri is null")
                    }
                }
            }
        }
    }
    private fun updateUserProfileDetail(){
        val userHashMap = HashMap<String,Any>()

        val firstName = et_user_profile_first_name.text.toString().trim()
        if (firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = et_user_profile_last_name.text.toString().trim()
        if(lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobile = et_user_profile_mobile_number.text.toString()
        val gender = if (rb_male.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }

        if (mUserProfileImageUri.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageUri
        }


        if (mobile.isNotEmpty() && mobile != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = mobile.toLong()
        }
        userHashMap[Constants.GENDER] = gender
//        showDialog(resources.getString(R.string.please_wait))

        userHashMap[Constants.COMPLETE_PROFILE] = true

        FirestoreClass().updateUserProfileDetails(this,userHashMap)
    }

    private fun permissionPhoto(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
            ),Constants.READ_STORAGE_PERMISSION
        )
    }

    fun userProfileUpdateSuccess(){
        hideDialog()
        Toast.makeText(this,resources.getString(R.string.msg_profile_update_success),Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION){
            if(grantResults.isNotEmpty()&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                showErrorSnackBar(resources.getString(R.string.storage_granted),false)
                Timber.d("enter onRequest")
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,resources.getString(R.string.storage_denied),Toast.LENGTH_SHORT).show()
            }
        }
    }

    // TODO: 2021/8/23 這邊要看 onActivitiyResult 之後怎麼用

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data != null){
                    try {
                        mSelectedImageFileUri = data.data
                        if (mSelectedImageFileUri != null) {
                            Timber.d("enter onActivityResult and uri:$mSelectedImageFileUri")
                            GlideLoader(this).loadUserPicture(mSelectedImageFileUri,iv_user_photo)
                        }
                        v_people . visibility = View . GONE

                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this,resources.getString(R.string.image_selection_failed),Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }
    private fun validateUserProfileDetails(): Boolean{
        return when{
            TextUtils.isEmpty(et_user_profile_mobile_number.text.toString().trim { it <= ' '}) -> {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
            false
            }else -> {
                    true
                }
        }

    }

    fun imageUploadSuccess(imageUri: String){
        mUserProfileImageUri = imageUri
        updateUserProfileDetail()
    }


    private fun setupActionBar() {
        val toolbar =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_user_profile_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            //顯示返回按鈕
            actionBar.setDisplayHomeAsUpEnabled(true)
            //設置返回按鈕圖案
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }
        //返回事件
        toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    private fun setUserDetailsToEdText(){
        et_user_profile_first_name.setText(mUserDetails.firstName)
        et_user_profile_last_name.setText(mUserDetails.lastName)
        et_user_profile_email.setText(mUserDetails.email)
        et_user_profile_email.isEnabled = true
        et_user_profile_first_name.isEnabled = true
        et_user_profile_last_name.isEnabled = true
        if (mUserDetails.mobile != 0L){
            et_user_profile_mobile_number.setText(mUserDetails.mobile.toString())
        }
        if (mUserDetails.gender == Constants.MALE){
            rb_male.isChecked = true
        }else{
            rb_female.isChecked = true
        }

        mUserDetails.image?.let {
            v_people.visibility = View.GONE
            GlideLoader(this).loadUserPicture(Uri.parse(it),iv_user_photo) }
    }
}
