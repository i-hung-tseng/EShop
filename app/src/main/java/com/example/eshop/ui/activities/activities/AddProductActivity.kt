package com.example.eshop.ui.activities.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Product
import com.example.eshop.utils.Constants
import com.example.eshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    lateinit var mSelectedUri: Uri
    private var mUpdateUri: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(
                this@AddProductActivity,R.drawable.app_gradient_color_background
        ))
        setupActionBar()
        iv_camera.setOnClickListener(this)
        btn_add_product_submit.setOnClickListener(this)
//        et_add_product_description.movementMethod =  ScrollingMovementMethod()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_addProduct_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }

        toolbar_addProduct_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updateProductDetails(){
        showDialog(resources.getString(R.string.please_wait))
        val userName = this.getSharedPreferences(Constants.MYSHOPPAL_PREFERENCES,Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME,"")?:"visitor"
        val userId = FirestoreClass().getCurrentUserId()
        val productTitle = et_add_product_title.text.toString().trim()
        val productPrice = et_add_product_price.text.toString().trim().toInt()
        val productDescription = et_add_product_description.text.toString().trim()
        val productQuantity = et_add_product_quantity.text.toString().trim().toInt()
        val product = Product(userId,userName,productTitle,productPrice,productDescription,productQuantity,mUpdateUri)
        FirestoreClass().updateProductDetails(this,product)
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_camera -> {
                    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    Constants.showImageChooser(this)
                }else{
                    permissionPhoto()
                }}

                R.id.btn_add_product_submit -> {
                    if (validateUserProfileDetails()){
                        updateProductDetails()
                    }

                }
            }
        }
    }

    private fun permissionPhoto() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION)
    }

    private fun validateUserProfileDetails(): Boolean{
        return when{

            mUpdateUri == "" -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_picture),true)
                false
            }


            TextUtils.isEmpty(et_add_product_title.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title),true)
                false
            }
            TextUtils.isEmpty(et_add_product_price.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price),true)
                false
            }
            TextUtils.isEmpty(et_add_product_description.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description),true)
                false
            }
            TextUtils.isEmpty(et_add_product_quantity.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity),true)
                false
            }
            else -> true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
        if (data != null){

            try {
                iv_camera.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_edit_24,null))
                mSelectedUri = data!!.data!!
                GlideLoader(this).loadUserPicture(mSelectedUri,iv_product)
                FirestoreClass().updateImageToCloudStorage(this,mSelectedUri,Constants.PRODUCT_IMAGE)
            }catch (e: IOException){
                e.printStackTrace()
                Toast.makeText(this,resources.getString(R.string.image_selection_failed),Toast.LENGTH_SHORT).show()
            }

            }

        }
    }

    fun imageUploadSuccess(imageUri: String){
        mUpdateUri = imageUri
    }

    fun uploadProductSuccess(){
        hideDialog()
        showErrorSnackBar(resources.getString(R.string.msg_product_update_successful),false)
        finish()
    }


}