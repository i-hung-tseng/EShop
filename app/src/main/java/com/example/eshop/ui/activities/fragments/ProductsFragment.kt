package com.example.eshop.ui.activities.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eshop.R
import com.example.eshop.ui.activities.adapter.ProductAdapter
import com.example.eshop.databinding.FragmentProductsBinding
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Product
import com.example.eshop.ui.activities.activities.AddProductActivity
import kotlinx.android.synthetic.main.fragment_products.*
import timber.log.Timber

class ProductsFragment : BaseFragment() {

    private lateinit var productList: ArrayList<Product>
    private lateinit var binding: FragmentProductsBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductsBinding.inflate(inflater)

//        getProductList()

        
        setHasOptionsMenu(true)
//        setAdapter()

        return binding.root
    }

    override fun onResume() {
        getProductList()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add -> {
                startActivity(Intent(requireActivity(), AddProductActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getProductList(){
        showDialog(requireActivity().resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this)
    }

    fun getProductListSuccessfulFromFireStore(list: ArrayList<Product>){
        Timber.d("enter getProductList ")
        hideDialog()
        productList = list
        if (productList.size > 0){
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE
            setAdapter()
        }else{
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    fun deleteProduct(productId: String){
        showAlertDialogToDeleteProduct(productId)
    }


    fun deletedProductSuccessfulFromFireStore(){
        hideDialog()
        Toast.makeText(requireActivity(),resources.getString(R.string.msg_product_delete_successful),Toast.LENGTH_SHORT).show()
        getProductList()
    }



    private fun setAdapter(){

        val mRecycleView = binding.rvMyProductItems
        mRecycleView.setHasFixedSize(true)
        val linearManager = LinearLayoutManager(requireActivity())
        linearManager.orientation = LinearLayoutManager.VERTICAL
        mRecycleView.layoutManager = linearManager
        val productAdapter = ProductAdapter(this,requireActivity(),productList)
        mRecycleView.adapter = productAdapter
        productAdapter.notifyDataSetChanged()


    }
    private fun showAlertDialogToDeleteProduct(productId: String){
        val builder = AlertDialog.Builder(requireActivity())
        builder.apply {
            setTitle(R.string.delete_dialog_title)
            setMessage(resources.getString(R.string.delete_dialog_message))
            setIcon(android.R.drawable.ic_dialog_alert)
            setPositiveButton(resources.getString(R.string.yes)){ dialogInterface,_ ->
                showDialog(resources.getString(R.string.please_wait))
                FirestoreClass().deleteProduct(this@ProductsFragment,productId)
                dialogInterface.dismiss()
            }
            setNegativeButton(resources.getString(R.string.no)){ dialogInterface,_ ->
                dialogInterface.dismiss()
            }

        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

}