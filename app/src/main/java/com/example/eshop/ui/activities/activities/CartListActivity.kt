package com.example.eshop.ui.activities.activities

import android.os.Bundle
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.CartItem
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_product_details.*
import timber.log.Timber

class CartListActivity :BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar_cart_list_activity)
        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }
        toolbar_product_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
        getCartListFromFirestore()
        super.onResume()
    }

    fun getCartListFromFirestore(){
        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this)
    }

    fun getCartListSuccessful(list:ArrayList<CartItem>){
        hideDialog()
        Timber.d("cartList list :$list")
    }
}