package com.example.eshop.ui.activities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.CartItem
import com.example.eshop.ui.activities.activities.CartListActivity
import com.example.eshop.utils.Constants
import com.example.eshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_cart_layout.view.*

class CartItemAdapter(private val context: Context,private val list:ArrayList<CartItem>,private val updateCartItems: Boolean):RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return mViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart_layout,parent,false))

    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        when (holder) {
            is mViewHolder -> {
                GlideLoader(context).loadProductPicture(model.image, holder.itemView.iv_cart_item_image)
                holder.itemView.tv_cart_item_title.text = model.title
                holder.itemView.tv_cart_item_price.text = "$${model.price.toString()}"
                holder.itemView.tv_cart_quantity.text = model.cart_quantity.toString()

                if (model.cart_quantity == 0) {
                    holder.itemView.ib_remove_cart_item.visibility = View.GONE
                    holder.itemView.ib_add_cart_item.visibility = View.GONE

                    if (updateCartItems){
                        holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                    }else{
                        holder.itemView.ib_delete_cart_item.visibility = View.GONE
                    }

                    holder.itemView.tv_cart_quantity.text = context.resources.getString(R.string.lbl_out_of_stock)
                    holder.itemView.tv_cart_quantity.setTextColor(ContextCompat.getColor(context, R.color.colorSnackBarError))
                } else {
                    holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_add_cart_item.visibility = View.VISIBLE

                    if (updateCartItems){
                        holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                        holder.itemView.ib_add_cart_item.visibility = View.VISIBLE
                        holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                    }else{
                        holder.itemView.ib_remove_cart_item.visibility = View.GONE
                        holder.itemView.ib_add_cart_item.visibility = View.GONE
                        holder.itemView.ib_delete_cart_item.visibility = View.GONE


                    }

                    holder.itemView.tv_cart_quantity.setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryText))
                }

                holder.itemView.ib_delete_cart_item.setOnClickListener {
                    when (context) {
                        is CartListActivity -> {
                            context.showDialog(context.resources.getString(R.string.please_wait))
                        }
                    }
                    FirestoreClass().removeItemFromCart(context, model.id)
                }

                holder.itemView.ib_remove_cart_item.setOnClickListener {
                    when (context) {
                        is CartListActivity -> {
                         if (model.cart_quantity == 1){
                             context.showDialog(context.resources.getString(R.string.please_wait))
                             FirestoreClass().removeItemFromCart(context,model.id)
                         }else{
                             val cartQuantity: Int = model.cart_quantity
                             val itemHashMap = HashMap<String,Any>()
                             itemHashMap[Constants.CART_QUANTITY] = cartQuantity -1
                             FirestoreClass().updateMyCart(context,model.id,itemHashMap)
                         }

                        }
                    }
                }

                holder.itemView.ib_add_cart_item.setOnClickListener {
                    when (context) {
                        is CartListActivity -> {
                            val cartQuantity:Int = model.cart_quantity
                            if (cartQuantity < model.stock_quantity){
                                val itemHashMap = HashMap<String,Any>()
                                itemHashMap[Constants.CART_QUANTITY] = cartQuantity +1
                                FirestoreClass().updateMyCart(context,model.id,itemHashMap)
                            }else{
                                context.showErrorSnackBar(context.resources.getString(R.string.msg_for_available_stock,model.stock_quantity.toString()),true)
                            }

                        }

                    }
                }
            }
        }
    }



    override fun getItemCount(): Int {
      return list.size
    }

    class mViewHolder(view:View):RecyclerView.ViewHolder(view)
}