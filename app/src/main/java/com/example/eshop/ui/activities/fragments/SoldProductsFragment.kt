package com.example.eshop.ui.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.SoldProduct
import com.example.eshop.ui.activities.adapter.SoldProductAdapter
import kotlinx.android.synthetic.main.fragment_sold_products.*

class SoldProductsFragment : BaseFragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold_products, container, false)
    }

    override fun onResume() {
        super.onResume()
        getSoldProductList()

    }

    private fun getSoldProductList(){
        showDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getSoldProductList(this)
    }
    fun successSoldProductList(soldProductList: ArrayList<SoldProduct>){
        hideDialog()
        if (soldProductList.size > 0){
            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_products_found.visibility = View.GONE
            setupAdapter(soldProductList)
        }else{

            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_products_found.visibility = View.VISIBLE

        }

    }
    private fun setupAdapter(soldProductList: ArrayList<SoldProduct>){
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        rv_sold_product_items.layoutManager = linearLayoutManager
        rv_sold_product_items.adapter = SoldProductAdapter(requireActivity(),soldProductList)
        rv_sold_product_items.setHasFixedSize(true)
    }

}