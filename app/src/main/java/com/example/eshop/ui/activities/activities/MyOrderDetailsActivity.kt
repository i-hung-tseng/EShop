package com.example.eshop.ui.activities.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eshop.R
import com.example.eshop.models.Order
import com.example.eshop.ui.activities.adapter.CartItemAdapter
import com.example.eshop.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_my_order_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {

    lateinit var mOrderDetails:Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order_details)

        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)){
            mOrderDetails = intent.getParcelableExtra(Constants.EXTRA_MY_ORDER_DETAILS)!!
            setupUI()
        }
        setupActionBar()


    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_my_order_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }

    }

    private fun setupUI(){

        tv_order_details_id.text = mOrderDetails.title

//        setTime
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calender: Calendar = Calendar.getInstance()
        calender.timeInMillis = mOrderDetails.order_datetime
        val orderDateTime = formatter.format(calender.time)
        tv_order_details_date.text = orderDateTime





        tv_my_order_details_address_type.text = mOrderDetails.address.type
        tv_my_order_details_full_name.text = mOrderDetails.address.name
        tv_my_order_details_address.text = "${mOrderDetails.address.address},${mOrderDetails.address.zipCode}"
        tv_my_order_details_additional_note.text = mOrderDetails.address.additionalNote

        if (mOrderDetails.address.otherDetails.isNotEmpty()){
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = mOrderDetails.address.otherDetails
        }else{
            tv_my_order_details_other_details.visibility = View.GONE
        }

        tv_my_order_details_mobile_number.text = mOrderDetails.address.mobileNumber
        tv_order_details_sub_total.text = mOrderDetails.sub_total_amount
        tv_order_details_shipping_charge.text = mOrderDetails.shipping_charge
        tv_order_details_total_amount.text = mOrderDetails.total_amount



        checkPlacedTime()
        setupAdapter()



    }

    private fun checkPlacedTime(){
        val diffInMillSeconds: Long = System.currentTimeMillis() - mOrderDetails.order_datetime
        val diffInHour: Long = TimeUnit.MILLISECONDS.toHours(diffInMillSeconds)

        when{
            diffInHour < 1 ->{
                tv_order_status.text = resources.getString(R.string.order_status_pending)
                tv_order_status.setTextColor(ContextCompat.getColor(this,R.color.colorAccent))
            }
            diffInHour < 2 ->{
                tv_order_status.text = resources.getString(R.string.order_status_in_process)
                tv_order_status.setTextColor(ContextCompat.getColor(this,R.color.colorOrderStatusInProcess))
            }
            else -> {
                tv_order_status.text = resources.getString(R.string.order_status_delivered)
                tv_order_status.setTextColor(ContextCompat.getColor(this,R.color.colorOrderStatusDelivered))
            }
        }





    }

    private fun setupAdapter(){
        val linearLayoutManager = LinearLayoutManager(this)
        rv_my_order_items_list.layoutManager = linearLayoutManager
        rv_my_order_items_list.adapter = CartItemAdapter(this, mOrderDetails.items, false)
        rv_my_order_items_list.setHasFixedSize(true)

    }


}