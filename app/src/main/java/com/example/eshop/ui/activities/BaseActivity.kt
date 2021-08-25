package com.example.eshop.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.eshop.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: Dialog
    private var doubleBackToExitPressedOnce = false




    fun showErrorSnackBar(message: String, errorMessage: Boolean){

        val snackBar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage){
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarError))
        }else{
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarSuccess))

        }
        snackBar.show()
    }

    fun showDialog(text: String){
        Log.d("Testing","enter showDialog")
        mProgressDialog = Dialog(this)
        mProgressDialog.apply {
            setContentView(R.layout.dialog_progress)
            tv_progress_text.text = text
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun hideDialog(){
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit(){
        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this@BaseActivity,resources.getString(R.string.please_click_back_again_to_exit),Toast.LENGTH_SHORT).show()


        Handler(Looper.getMainLooper()).postDelayed(
            { doubleBackToExitPressedOnce = false
            },
            2000
        )
    }
}