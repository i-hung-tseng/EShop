package com.example.eshop.activities

import android.app.Activity

import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import java.util.jar.Manifest

class UserProfileActivity : BaseActivity(),View.OnClickListener{

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        setUserDetail()
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
                    if (mSelectedImageFileUri != null)
                    FirestoreClass().updateImageToCloudStorage(this,mSelectedImageFileUri)
                    else{
                        updateUserProfileDetail()
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetail(){
        val userHashMap = HashMap<String,Any>()
        val mobile = et_user_profile_mobile_number.text.toString()
        val gender = if (rb_male.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }

        if (mUserProfileImageUri.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageUri
        }


        if (mobile.isNotEmpty()){
            userHashMap[Constants.MOBILE] = mobile.toLong()
        }
        userHashMap[Constants.GENDER] = gender
//        showDialog(resources.getString(R.string.please_wait))
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
        startActivity(Intent(this@UserProfileActivity,MainActivity::class.java))
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
//        hideDialog()
        mUserProfileImageUri = imageUri
        updateUserProfileDetail()
    }

    fun setUserDetail(){



        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS) ){
            intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)?.let {
                mUserDetails = it
            }
        }





        et_user_profile_first_name.apply {
            isEnabled = false
            setText(mUserDetails.firstName)
        }

        et_user_profile_last_name.apply {
            isEnabled = false
            setText(mUserDetails.lastName)
        }

        et_user_profile_email.apply {
            isEnabled = false
            setText(mUserDetails.email)
        }

        mUserDetails.mobile?.let {
            et_user_profile_mobile_number.apply {
                isEnabled = false
                setText(it.toString())
            }
        }

        mUserDetails.gender?.let {
            if (it == Constants.MALE){
                rg_gender.check(R.id.rb_male)
            }else{
                rg_gender.check(R.id.rb_female)
            }
        }
        mUserDetails.image?.let {
            v_people.visibility = View.GONE
            GlideLoader(this).loadUserPicture(Uri.parse(it),iv_user_photo)
        }
    }
}
