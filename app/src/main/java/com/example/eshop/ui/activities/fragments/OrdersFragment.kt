package com.example.eshop.ui.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Order
import com.example.eshop.ui.activities.adapter.OrderAdapter
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : BaseFragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        return root
    }

    override fun onResume() {
        super.onResume()
        getMyOrderList()
    }

    fun populateOrdersListInUI(ordersList: ArrayList<Order>){
        hideDialog()

        if (ordersList.size > 0){
            rv_my_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE
            setupAdapter(ordersList)
        }else{
            rv_my_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }
    }

    private fun getMyOrderList(){
        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getMyOrderList(this)

    }

    private fun setupAdapter(ordersList: ArrayList<Order>){
        val linerLayoutManager = LinearLayoutManager(requireActivity())
        rv_my_order_items.layoutManager = linerLayoutManager
        rv_my_order_items.adapter = OrderAdapter(requireActivity(),ordersList)
        rv_my_order_items.setHasFixedSize(true)
    }


}