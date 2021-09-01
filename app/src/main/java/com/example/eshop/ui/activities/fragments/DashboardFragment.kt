package com.example.eshop.ui.activities.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eshop.R
import com.example.eshop.firestore.FirestoreClass
import com.example.eshop.models.Product
import com.example.eshop.ui.activities.activities.CartListActivity
import com.example.eshop.ui.activities.activities.DashboardActivity
import com.example.eshop.ui.activities.activities.ProductDetailsActivity
import com.example.eshop.ui.activities.activities.SettingsActivity
import com.example.eshop.ui.activities.adapter.DashboardAdapter
import com.example.eshop.utils.Constants
import kotlinx.android.synthetic.main.fragment_dashboard.*
import timber.log.Timber

class DashboardFragment : BaseFragment() {

    private lateinit var productList:ArrayList<Product>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        右上角的 menu，如果要在Fragment新增的化就必須要有
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        getDashboardItemsList()
        super.onResume()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        dashboardViewModel =
//                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//        })
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_setting -> {
                startActivity(Intent(requireActivity(), SettingsActivity::class.java))
                return true
            }
            R.id.action_going_cart -> {
                startActivity(Intent(requireActivity(),CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>){
        Timber.d("enter dashboard successful")
        hideDialog()
        productList = dashboardItemsList
        if (productList.size > 0){

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE
            setAdapter()
        }else{
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE

        }
    }

    private fun getDashboardItemsList(){
        showDialog(requireActivity().resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this)
    }

    private fun setAdapter(){
        val mRecyclerview = rv_dashboard_items
        mRecyclerview.setHasFixedSize(true)
        val linearLayoutManager = GridLayoutManager(activity,2)
        mRecyclerview.layoutManager = linearLayoutManager
        val dashboardAdapter =  DashboardAdapter(requireActivity(),productList)
        mRecyclerview.adapter = dashboardAdapter
        dashboardAdapter.setOnClickListener(object: DashboardAdapter.OnClickListener {
            override fun onClick(position: Int, product: Product) {
                val intent = Intent(Intent(context,ProductDetailsActivity::class.java))
                intent.putExtra(Constants.PRODUCT,product)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,product.user_id)
                startActivity(intent)
            }

        })
    }

    fun turnToDetailsActivity(productId: String){



    }
}