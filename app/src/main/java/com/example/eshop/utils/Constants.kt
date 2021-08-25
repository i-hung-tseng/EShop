package com.example.eshop.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import timber.log.Timber

object Constants {

    //collection 名稱
    const val USERS: String = "users"

    //這個 sharedPreference的 名稱
    const val MYSHOPPAL_PREFERENCES: String = "MyShopPalPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "Extra_user_details"
    const val READ_STORAGE_PERMISSION: Int = 0
    const val PICK_IMAGE_REQUEST_CODE: Int = 1

    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    // 拿到副檔案名稱
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        Timber.d("getFileExtension type : ${activity.contentResolver.getType(uri!!)}")
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))

    }
}