package com.example.eshop.ui.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Address
import com.example.eshop.models.CartItem
import com.example.eshop.models.Order
import com.example.eshop.models.Product
import com.example.eshop.ui.activities.adapter.CartItemAdapter
import com.example.eshop.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_checkout_main.*

class CheckoutActivity : BaseActivity() {

    private var mAddress: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_main)

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddress = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
            setupUI()
        }
        getProductList()

        btn_place_order.setOnClickListener{
            placeAndOrder()
        }
        setToolbar()
    }


    private fun setToolbar(){
        setSupportActionBar(toolbar_checkout_activity)
        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }
        toolbar_checkout_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    private fun setupUI(){
        tv_checkout_address_type.text = mAddress?.type
        tv_checkout_full_name.text = mAddress?.name
        tv_checkout_address.text = mAddress?.address
        tv_checkout_additional_note.text = mAddress?.additionalNote
        tv_mobile_number.text = mAddress?.mobileNumber
        if (mAddress?.otherDetails!!.isNotEmpty()){
            tv_checkout_other_details.text = mAddress?.otherDetails
        }
    }

    private fun getProductList(){
        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this)
    }

    fun getProductFromFireStoreSuccessful(productList: ArrayList<Product>){
        mProductList = productList
        getCartItemsList()
    }


    private fun getCartItemsList(){
        FirestoreClass().getCartList(this)
    }

    private fun placeAndOrder(){
        showDialog(resources.getString(R.string.please_wait))
        if (mAddress != null){

                mOrderDetails = Order(
                FirestoreClass().getCurrentUserId(),
                mCartItemsList,
                mAddress!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0",
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )
            FirestoreClass().placeOrder(this,mOrderDetails)


        }

    }



    fun getCartItemsFromFireStoreSuccessful(cartItemsList: ArrayList<CartItem>){
        hideDialog()
        for ( product in mProductList){
            for (cart in cartItemsList){
                if(product.product_id == cart.product_id){
                    cart.stock_quantity = product.quantity
                }
            }
        }
        mCartItemsList = cartItemsList
        rv_cart_list_items.layoutManager = LinearLayoutManager(this)
        rv_cart_list_items.setHasFixedSize(true)
        val cartListAdapter = CartItemAdapter(this,mCartItemsList,false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList){
            val availableQuantity = item.stock_quantity
            if (availableQuantity > 0){
                val price = item.price
                val quantity = item.cart_quantity.toDouble()
                mSubTotal += (price!! * quantity)
            }
        }
        tv_checkout_sub_total.text = "$${mSubTotal}"
        tv_checkout_shipping_charge.text = "$10.0"
        if (mSubTotal > 0){
            ll_checkout_place_order.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "$${mTotalAmount}"
        }else{
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    fun orderPlaceSuccessful(){
        FirestoreClass().updateAllDetails(this,mCartItemsList, mOrderDetails)
    }

    fun updatedAllDetailsSuccessful(){
        hideDialog()
        Toast.makeText(this,resources.getString(R.string.mgg_your_order_placed_successfully),Toast.LENGTH_SHORT).show()

        val intent = Intent(this,DashboardActivity::class.java)
        // TODO: 2021/9/6 看起來是把 activity finish掉
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }




}