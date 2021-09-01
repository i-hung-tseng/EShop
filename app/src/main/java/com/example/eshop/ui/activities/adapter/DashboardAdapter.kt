package com.example.eshop.ui.activities.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eshop.R
import com.example.eshop.models.Product
import com.example.eshop.ui.activities.activities.DashboardActivity
import com.example.eshop.ui.activities.activities.ProductDetailsActivity
import com.example.eshop.utils.Constants
import com.example.eshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*
import timber.log.Timber

 class DashboardAdapter(private val context: Context, private val list: ArrayList<Product>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return mViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dashboard_layout,parent,false))
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        when(holder){
            is mViewHolder -> {
                GlideLoader(context).loadProductPicture(model.photo,holder.itemView.iv_dashboard_item_image)
                holder.itemView.tv_dashboard_item_title.text = model.title
                holder.itemView.tv_dashboard_item_price.text = model.price.toString()
//                holder.itemView.setOnClickListener {
//                    val intent = Intent(Intent(context,ProductDetailsActivity::class.java))
//                    intent.putExtra(Constants.PRODUCT,model)
//                    context.startActivity(intent)
//                }
                holder.itemView.setOnClickListener {
                    if(onClickListener != null){
                        onClickListener!!.onClick(position,model)
                    }
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    class mViewHolder(view:View):RecyclerView.ViewHolder(view)

    interface OnClickListener{
        fun onClick(position: Int, product: Product)
    }
}