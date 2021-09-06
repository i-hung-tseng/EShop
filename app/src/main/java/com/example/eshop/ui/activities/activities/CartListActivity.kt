package com.example.eshop.ui.activities.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.CartItem
import com.example.eshop.models.Product
import com.example.eshop.ui.activities.adapter.CartItemAdapter
import com.example.eshop.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_product_details.*
import timber.log.Timber

class CartListActivity :BaseActivity() {

    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartItemList: ArrayList<CartItem>
    private var mCartItemEachQuantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

//        getCartListFromFirestore()
        setToolbar()

        btn_checkout.setOnClickListener{
            val intent = Intent(this@CartListActivity,AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
            startActivity(intent)
        }


    }

    private fun setToolbar(){
        setSupportActionBar(toolbar_cart_list_activity)
        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }
        toolbar_cart_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
//        getCartListFromFirestore()
        getProductList()
        super.onResume()
    }

    fun itemRemovedSuccessful(){
        hideDialog()
        Toast.makeText(this,resources.getString(R.string.msg_item_removed_successfully),Toast.LENGTH_SHORT).show()
        getCartListFromFirestore()
    }

    fun getCartListFromFirestore(){
//        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this)
    }

    fun getCartListSuccessful(list:ArrayList<CartItem>){

        Timber.d("enter getCartList")

        hideDialog()
        for(product in mProductsList){
            for (cartItem in list){
                if (product.product_id == cartItem.product_id){
                    //購物車的庫存等於商品的數量
                    cartItem.stock_quantity = product.quantity
                    if (product.quantity == 0){
//                        如果商品的數量等於0，購物車的要求數量等於商品數量等於0
                        cartItem.cart_quantity = product.quantity
                    }
                }
            }
        }
        mCartItemList = list

                if (mCartItemList.size >0){
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE
            calculatorMoneyAndDisplayToUI(mCartItemList)
//            當確定 item 的大小不會改變 recyclerivew 的大小時候，就可以set True
            rv_cart_items_list.setHasFixedSize(true)
            setAdapter()
        }






    }

    fun getProductListSuccessful(list:ArrayList<Product>){
        Timber.d("enter getProductSuccessful")
        mProductsList = list
        hideDialog()
//        mProductsList = list
        getCartListFromFirestore()
    }

    private fun getProductList(){
        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this)
    }

    private fun setAdapter(){
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_cart_items_list.adapter = CartItemAdapter(this,mCartItemList,true)
        rv_cart_items_list.layoutManager = linearLayoutManager
    }

    private fun calculatorMoneyAndDisplayToUI(list: ArrayList<CartItem>){

        var subTotal:Double = 0.0
        for (i in list){
            val availableQuantity = i.stock_quantity
            if (availableQuantity > 0){
                val quantity = i.cart_quantity?.toDouble()
                val price = i.price
                subTotal += (quantity!! * price!!)
            }

        }
        tv_sub_total.text = "$$subTotal"
        tv_shipping_charge.text = "$10.0"
        if (subTotal >0){
//            ll_checkout.visibility = View.VISIBLE

            val total = subTotal + 10
            tv_total_amount.text = "$$total"
        }
        else{
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    fun itemUpdateSuccess(){
        hideDialog()
        getCartListFromFirestore()
    }


}