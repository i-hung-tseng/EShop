package com.example.eshop.ui.activities.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eshop.R
import com.example.eshop.models.SoldProduct
import com.example.eshop.ui.activities.activities.SoldProductDetailsActivity
import com.example.eshop.utils.Constants
import com.example.eshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

class SoldProductAdapter(private val context: Context, private val soldProductList: ArrayList<SoldProduct> ):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_layout,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = soldProductList[position]

        if (holder is MViewHolder){
            GlideLoader(context).loadProductPicture(model.image, holder.itemView.iv_list_item_product)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "$${model.price}"
            holder.itemView.ib_delete_product.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val intent = Intent(context,SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS,model)
                context.startActivity(intent)
            }


        }
    }

    override fun getItemCount(): Int = soldProductList.size

    class MViewHolder(view: View):RecyclerView.ViewHolder(view)
}