package com.example.eshop.ui.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.CartItem
import com.example.eshop.models.Product
import com.example.eshop.utils.Constants
import com.example.eshop.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_product_details.*
import timber.log.Timber

class ProductDetailsActivity : BaseActivity(),View.OnClickListener {

    lateinit var product: Product
    lateinit var currentId: String





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)


        FirebaseAuth.getInstance().currentUser?.uid?.let {
            currentId = it
        }




        supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(
            this@ProductDetailsActivity,R.drawable.app_gradient_color_background
        ))


        if (intent.hasExtra(Constants.PRODUCT)){
            product = intent.getParcelableExtra<Product>(Constants.PRODUCT)!!
            val productUserId = product.user_id
            if (productUserId == currentId){
                btn_add_to_cart.visibility = View.GONE
                btn_go_to_cart.visibility = View.GONE
            }else{
                btn_add_to_cart.visibility = View.VISIBLE
            }
            setProductDetails()
        }
//
//        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
//            val userId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)
//            Timber.d("Extra has userId:$userId currentId$currentId")
//            if (userId == currentId){
//                Timber.d("userId == currentId")
//                btn_add_to_cart.visibility = View.GONE
//            }else{
//                Timber.d("userId != currentId")
//                btn_add_to_cart.visibility = View.VISIBLE
//            }
//        }

        setToolbar()


        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }

    override fun onResume() {
        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().checkExistsInCart(this,product.product_id)
        super.onResume()
    }

    private fun setProductDetails(){
        GlideLoader(this).loadProductPicture(product.photo,iv_product_detail_image)
        tv_product_details_title.text = product.title
        Timber.d("title = ${product.title}")
        tv_product_details_price.text = product.price.toString()
        Timber.d("price = ${product.price}")
        tv_product_details_description.text = product.description
        tv_product_details_available_quantity.text = product.quantity.toString()
    }

    fun addCartItemToFireStoreSuccessful(){
        hideDialog()
        Toast.makeText(this,resources.getString(R.string.successful_message_item_added_to_cart),Toast.LENGTH_SHORT).show()
        btn_go_to_cart.visibility = View.VISIBLE
        btn_add_to_cart.visibility = View.GONE

        startActivity(Intent(this,DashboardActivity::class.java))
    }


    private fun setToolbar(){
        setSupportActionBar(toolbar_product_details_activity)
        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }
        toolbar_product_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun addCartItemToFireStore(){
        val cartItem = CartItem(
                currentId,
                product.product_id,
                product.title,
                product.price,
                product.photo,
                Constants.DEFAULT_CART_QUANTITY

        )
        FirestoreClass().addProductToCart(this,cartItem)


    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_add_to_cart -> {
                this.showDialog(resources.getString(R.string.please_wait))
                addCartItemToFireStore()
            }
            R.id.btn_go_to_cart ->{
                startActivity(Intent(this,CartListActivity::class.java))
            }
        }
    }

    fun productExistsInCart(){
        hideDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

}