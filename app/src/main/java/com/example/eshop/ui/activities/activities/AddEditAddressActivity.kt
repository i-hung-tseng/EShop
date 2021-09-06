package com.example.eshop.ui.activities.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Address
import com.example.eshop.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_address_list.*
import timber.log.Timber

class AddEditAddressActivity : BaseActivity() {

    private var mAddressDetails:Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)


        btn_submit_address.setOnClickListener{
            saveAddressToFirestore()
        }

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        setupActionBar()

        rg_type.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb_other){
                til_other_details.visibility = View.VISIBLE
            }else{
                til_other_details.visibility = View.GONE
            }
        }

        if (mAddressDetails != null){
            if (mAddressDetails!!.id.isNotEmpty()){
                tv_edit_address_title.text = resources.getString(R.string.title_edit_address)
                btn_submit_address.text = resources.getString(R.string.btn_lbl_update)

                et_full_name.setText(mAddressDetails?.name)
                et_phone_number.setText(mAddressDetails?.mobileNumber)
                et_address.setText(mAddressDetails?.address)
                et_zip_code.setText(mAddressDetails?.zipCode)
                et_additional_note.setText(mAddressDetails?.additionalNote)
                when(mAddressDetails?.type){
                    Constants.HOME ->{
                        rb_home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office.isChecked = true
                    }else -> {
                        rb_other.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        et_other_details.setText(mAddressDetails?.otherDetails)
                    }

                }

            }
        }
    }

    private fun setupActionBar(){

        setSupportActionBar(toolbar_add_edit_address_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            Timber.d("action bar != null")
        }
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun saveAddressToFirestore(){
        val fullName: String = et_full_name.text.toString().trim()
        val phoneNumber: String = et_phone_number.text.toString().trim()
        val address: String = et_address.text.toString().trim()
        val zipCode: String = et_zip_code.text.toString().trim()
        val additionalNote: String = et_additional_note.text.toString().trim()
        val otherDetails: String = et_other_details.text.toString().trim()

        if (validateData()){

            showDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                rb_home.isChecked -> {
                    Constants.HOME
                }
                rb_office.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }
            val id = if(mAddressDetails != null){
                mAddressDetails!!.id
            }else{
                ""
            }
            val addressModel = Address(
                FirestoreClass().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails,
                    id
            )
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
                FirestoreClass().updateAddress(this,addressModel,mAddressDetails!!.id)
                Timber.d("update address activity mAddressDetails.id no null  ${mAddressDetails!!.id}")
            }else{
                FirestoreClass().addAddressToFireStore(this,addressModel)
                Timber.d("update address activity mAddressDetails.id  null ")
            }

        }

    }

    fun updateAddressToFirestoreSuccessful(){
        hideDialog()

        val notifySuccessMessage: String = if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
            resources.getString(R.string.msg_your_address_updated_successfully)
        }else{
            resources.getString(R.string.msg_address_add_successful)
        }



        Toast.makeText(this,notifySuccessMessage,Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        startActivity(Intent(this,AddressListActivity::class.java))
    }


    private fun validateData() : Boolean{

        return when{
            TextUtils.isEmpty(et_full_name.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name),true)
                false
            }
            TextUtils.isEmpty(et_phone_number.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_phone_number),true)
                false
            }
            TextUtils.isEmpty(et_address.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address),true)
                false
            }
            TextUtils.isEmpty(et_zip_code.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code),true)
                false
            }
            rb_other.isChecked && TextUtils.isEmpty(
                et_zip_code.text.toString().trim{ it <= ' '}
            ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_other_details),true)
                false
            }
            else->{
                true
            }
        }

    }
}
