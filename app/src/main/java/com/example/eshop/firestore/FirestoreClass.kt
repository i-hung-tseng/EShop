package com.example.eshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.eshop.activities.LoginActivity
import com.example.eshop.activities.RegisterActivity
import com.example.eshop.activities.UserProfileActivity
import com.example.eshop.models.User
import com.example.eshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import timber.log.Timber


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User){

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener{ e ->
                activity.hideDialog()
                Timber.d(e.message)
            }
    }

    fun getCurrentUserId(): String{

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun getUserDetails(activity: Activity){

        mFireStore.collection(Constants.USERS)
            //應該是這邊的 getCurrentUserId會拿到 現在的 CurrentUserId
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
            val user = it.toObject(User::class.java)
                val sharePreferences = activity.getSharedPreferences(
                    Constants.MYSHOPPAL_PREFERENCES,Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharePreferences.edit()
                //put Key & value
                editor.putString(Constants.LOGGED_IN_USERNAME,"${user?.firstName}${user?.lastName}")
                editor.apply()

                when(activity){
                    is LoginActivity -> {
                        if (user != null) {
                            activity.userLoggedInSuccess(user)
                        }
                    }
                }
            }
    }

    fun updateUserProfileDetails(activity: Activity,userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener{
                when(activity){
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()

                    }
                }
            }
            .addOnFailureListener{
                when(activity){
                    is UserProfileActivity -> {
                        activity.hideDialog()
                    }
                }
            }


    }

    fun updateImageToCloudStorage(activity: Activity,imageFileUri: Uri?){
        val sRef: StorageReference =  FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                + Constants.getFileExtension(
                activity,
                imageFileUri
                )
        )
        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Timber.d(taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri->
                    Timber.d("Downloadable image URL ${uri.toString()}")
                    when(activity){
                        is UserProfileActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener{ exception ->
                when(activity){
                    is UserProfileActivity -> {
                        activity.hideDialog()
                    }
                }
                Timber.d(exception.message)
            }

    }
}
