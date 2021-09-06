package com.example.eshop.ui.activities.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Address
import com.example.eshop.ui.activities.activities.AddEditAddressActivity
import com.example.eshop.ui.activities.activities.AddressListActivity
import com.example.eshop.ui.activities.activities.CheckoutActivity
import com.example.eshop.utils.Constants
import kotlinx.android.synthetic.main.item_address_layout.view.*
import timber.log.Timber

class AddressAdapter(private val context: Context,private val addressList:ArrayList<Address>,private val selectAddress: Boolean):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return mViewHoder(LayoutInflater.from(context).inflate(R.layout.item_address_layout,parent,
            false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model = addressList[position]

       when(context){
           is AddressListActivity -> {
               holder.itemView.tv_address_full_name.text = model.name
               holder.itemView.tv_address_type.text = model.type
               holder.itemView.tv_address_details.text = model.address + "," + model.zipCode
               holder.itemView.tv_address_mobile_number.text = model.mobileNumber
               if (selectAddress){
                   holder.itemView.setOnClickListener{
                       val intent = Intent(context,CheckoutActivity::class.java)
                       intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS,model)
                       context.startActivity(intent)
                   }
               }
           }
       }
    }

    override fun getItemCount(): Int {
       return addressList.size
    }

    fun notifyEditItem(activity: Activity, position: Int){
        val intent = Intent(context,AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS,addressList[position])
        Timber.d("update address model:${addressList[position].id}")
        activity.startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }


    class mViewHoder(view:View):RecyclerView.ViewHolder(view)
}