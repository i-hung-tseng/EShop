package com.example.eshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.eshop.models.Product
import com.example.eshop.models.User
import com.example.eshop.ui.activities.activities.*
import com.example.eshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import timber.log.Timber


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

//    已經創完帳號後，在這邊merge新的資訊
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

    //把user傳回LoginActivity
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
                    is SettingsActivity -> {
                        if (user != null) {
                            activity.userDetailsSuccess(user)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is LoginActivity -> {
                        activity.hideDialog()
//                        activity.showErrorSnackBar(e.message.toString(),true)
                    }
                    is SettingsActivity -> {
                        activity.hideDialog()
//                        activity.showErrorSnackBar(e.message.toString(),true)
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

    //這步驟是上傳至 firebase 的 storage而已，
    fun updateImageToCloudStorage(activity: Activity,imageFileUri: Uri?,inputType: String){



        Timber.d("Testing imageFileUri:$imageFileUri")
        //裡面的 pathString =  User_Profile_Image1629873910127.jpg
        //先給一個 path位置，再給current秒數，再給副檔案名稱
        val sRef: StorageReference =  FirebaseStorage.getInstance().reference.child(
            inputType + System.currentTimeMillis() + "."
                + Constants.getFileExtension(
                activity,
                imageFileUri
                )
        )
        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Timber.d("Testing"+ taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl


                .addOnSuccessListener { uri->
                    Timber.d("Testing Downloadable image URL $uri")
                    when(activity){
                        is UserProfileActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
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

    fun updateProductDetails(activity: AddProductActivity, product: Product){
        mFireStore.collection(Constants.PRODUCT).add(product).addOnSuccessListener {

            mFireStore.collection(Constants.PRODUCT).document(it.id)
                    .update(Constants.PRODUCT_ID,it.id)
                    activity.uploadProductSuccess()
        }
                .addOnFailureListener {
                    activity.showErrorSnackBar(it.message.toString(),true)
                }
    }

//    fun updateProductImage(activity: AddProductActivity, uri: Uri){
//        val sRef = FirebaseStorage.getInstance().reference.child(Constants.PRODUCT_IMAGE + System.currentTimeMillis() + Constants.getFileExtension(activity,uri))
//        sRef.putFile(uri).addOnSuccessListener {
//            it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
//                activity.imageUploadSuccess(it.toString())
//            }
//        }
//                .addOnFailureListener {
//                    activity.showErrorSnackBar(it.message.toString(),true)
//                }
//
//
//    }

}
