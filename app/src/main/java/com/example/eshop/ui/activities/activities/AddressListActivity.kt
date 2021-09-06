package com.example.eshop.ui.activities.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Address
import com.example.eshop.ui.activities.adapter.AddressAdapter
import com.example.eshop.utils.Constants
import com.example.eshop.utils.SwipeToDeleteCallback
import com.example.eshop.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*
import timber.log.Timber

class AddressListActivity : BaseActivity() {


    private  var mSelectAddress: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        tv_add_address.setOnClickListener {
            val intent = Intent(this,AddEditAddressActivity::class.java)
            startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        getAddressList()
        setupActionBar()
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS,false)
        }

        if (mSelectAddress){
            tv_title.text = resources.getString(R.string.title_select_address)

        }

    }

//    override fun onResume() {
//        super.onResume()
//        getAddressList()
//
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            getAddressList()
        }
    }




    private fun setupActionBar(){
        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }

        toolbar_address_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getAddressListFromFirestoreSuccessful(addressList: ArrayList<Address>){
        hideDialog()
        Timber.d("successful and list :${addressList.size}")
        if (addressList.size >0){
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE
            setAdapter(addressList)


        }
    }

    private fun getAddressList(){
        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressList(this)
    }

    private fun setAdapter(addressList: ArrayList<Address>){
        val linearLayout = LinearLayoutManager(this)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        rv_address_list.layoutManager = linearLayout
        rv_address_list.adapter = AddressAdapter(this,addressList,mSelectAddress)

        if (!mSelectAddress){

            val editSwipeHandler = object: SwipeToEditCallback(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    Timber.d("update address enter editSwipeHandler ")
                    val adapter = rv_address_list.adapter as AddressAdapter
                    adapter.notifyEditItem(this@AddressListActivity,viewHolder.adapterPosition)
                }
            }
            val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
            editItemTouchHelper.attachToRecyclerView(rv_address_list)

            val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    Timber.d("update address enter deletedSwipeHandler ")
                    showDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().deleteAddress(this@AddressListActivity,addressList[viewHolder.adapterPosition].id)

                }

            }
            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(rv_address_list)

        }

        rv_address_list.setHasFixedSize(true)

    }

    fun deleteAddressSuccessful(){
        hideDialog()
        Toast.makeText(this,resources.getString(R.string.msg_your_address_delete_successfully),Toast.LENGTH_SHORT).show()
        getAddressList()
    }

}