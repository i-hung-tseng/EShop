

package com.example.eshop.ui.activities.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Product
import com.example.eshop.ui.activities.activities.ProductDetailsActivity
import com.example.eshop.ui.activities.fragments.ProductsFragment
import com.example.eshop.utils.Constants
import com.example.eshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*
import timber.log.Timber

class ProductAdapter(private val fragment: ProductsFragment,private val context: Context, private var list: ArrayList<Product>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.photo,holder.itemView.iv_list_item_product)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "$${model.price}"
            holder.itemView.ib_delete_product.setOnClickListener{
            fragment.deleteProduct(model.product_id)
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(Intent(context, ProductDetailsActivity::class.java))
                intent.putExtra(Constants.PRODUCT,model)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,model.user_id)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size

    }

    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)


}