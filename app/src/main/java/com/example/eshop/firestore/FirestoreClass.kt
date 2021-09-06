package com.example.eshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eshop.R
import com.example.eshop.models.*
import com.example.eshop.ui.activities.activities.*
import com.example.eshop.ui.activities.fragments.*
import com.example.eshop.utils.Constants
import com.google.common.math.Quantiles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import timber.log.Timber


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    //    已經創完帳號後，在這邊merge新的資訊
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideDialog()
                Timber.d(e.message)
            }
    }

    fun getCurrentUserId(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    //把user傳回LoginActivity
    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            //應該是這邊的 getCurrentUserId會拿到 現在的 CurrentUserId
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                val sharePreferences = activity.getSharedPreferences(
                    Constants.MYSHOPPAL_PREFERENCES, Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharePreferences.edit()
                //put Key & value
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user?.firstName}${user?.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        if (user != null) {
                            activity.userLoggedInSuccess(user)
                        }
                    }
                    is SettingsActivity -> {
                        if (user != null) {
                            activity.userDetailsSuccess(user)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideDialog()
//                        activity.showErrorSnackBar(e.message.toString(),true)
                    }
                    is SettingsActivity -> {
                        activity.hideDialog()
//                        activity.showErrorSnackBar(e.message.toString(),true)
                    }
                }
            }
    }

    fun updateUserProfileDetails(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()

                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideDialog()
                    }
                }
            }


    }

    //這步驟是上傳至 firebase 的 storage而已，
    fun updateImageToCloudStorage(activity: Activity, imageFileUri: Uri?, inputType: String) {


        Timber.d("Testing imageFileUri:$imageFileUri")
        //裡面的 pathString =  User_Profile_Image1629873910127.jpg
        //先給一個 path位置，再給current秒數，再給副檔案名稱
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            inputType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )
        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Timber.d("Testing" + taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl


                .addOnSuccessListener { uri ->
                    Timber.d("Testing Downloadable image URL $uri")
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener { exception ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideDialog()
                    }
                }
                Timber.d(exception.message)
            }

    }

    fun updateProductDetails(activity: AddProductActivity, product: Product) {
        mFireStore.collection(Constants.PRODUCT).add(product).addOnSuccessListener {

            mFireStore.collection(Constants.PRODUCT).document(it.id)
                .update(Constants.PRODUCT_ID, it.id)
            activity.uploadProductSuccess()
        }
            .addOnFailureListener {
                activity.showErrorSnackBar(it.message.toString(), true)
            }
    }

    fun getProductsList(fragment: Fragment) {

        mFireStore.collection(Constants.PRODUCT)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {

                val productList = ArrayList<Product>()
                for (document in it.documents) {
                    val product = document.toObject(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                        Timber.d("enter getProductList:$product")
                    }
                }
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.getProductListSuccessfulFromFireStore(productList)
                    }
                }
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCT)
            .get()
            .addOnSuccessListener { document ->

                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    if (product != null) {
                        product.product_id = i.id
                        productsList.add(product)
                    }
                }
                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                fragment.hideDialog()
                Timber.d("Error while getting dashboard items list $e")
            }
    }

    fun deleteProduct(fragment:ProductsFragment,productID: String){
        mFireStore.collection(Constants.PRODUCT)
                .document(productID)
                .delete()
                .addOnSuccessListener {
                    fragment.deletedProductSuccessfulFromFireStore()
                }
                .addOnFailureListener { e->
                    fragment.hideDialog()
                    Timber.d("Error while deleting product item $e")
                }
    }

    fun addProductToCart(activity: ProductDetailsActivity,addCartItem: CartItem){

             mFireStore.collection(Constants.CART_ITEM)
            .add(addCartItem)
            .addOnSuccessListener {
                activity.addCartItemToFireStoreSuccessful()
                it.update("id",it.id)
            }
            .addOnFailureListener {
                activity.hideDialog()
                Timber.d("Error while add CartItem to FireStore cause $it")

            }


    }

    fun checkExistsInCart(activity: ProductDetailsActivity,productID: String){
        mFireStore.collection(Constants.CART_ITEM)
                .whereEqualTo(Constants.USER_ID,getCurrentUserId())
                .whereEqualTo(Constants.PRODUCT_ID,productID)
                .get()
                .addOnSuccessListener {
                    if (it.documents.size > 0){
                        activity.productExistsInCart()
                    }else{
                        activity.hideDialog()
                    }
                }
                .addOnFailureListener {

                }
    }

    fun getCartList(activity:Activity){
        mFireStore.collection(Constants.CART_ITEM)
            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val list: ArrayList<CartItem> = ArrayList()
                for (i in it.documents){
                    val cartItem = i.toObject(CartItem::class.java)
                    cartItem?.let {
                        list.add(it)
                    }
                }
                when(activity){
                    is  CartListActivity ->{
                        activity.getCartListSuccessful(list)
                    }
                    is CheckoutActivity -> {
                        activity.getCartItemsFromFireStoreSuccessful(list)
                    }

                }

            }
            .addOnFailureListener {
                when(activity){
                    is CartListActivity -> {
                        activity.hideDialog()
                        Timber.d("Error while getting cart item list cause $it")
                    }
                    is CheckoutActivity -> {
                        activity.hideDialog()
                        Timber.d("Error while getting cart item list cause $it")
                    }
                }
            }
    }

    fun getAllProductList(activity:Activity){
        mFireStore.collection(Constants.PRODUCT)
                .get()
                .addOnSuccessListener { document ->

                    val productList: ArrayList<Product> = ArrayList()
                    for(i in document.documents){
                        val product = i.toObject(Product::class.java)
                        product?.let {
                            productList.add(it)
                        }
                    }

                    when(activity){
                        is  CartListActivity ->{

                            activity.getProductListSuccessful(productList)
                        }
                        is CheckoutActivity -> {
                            activity.getProductFromFireStoreSuccessful(productList)
                        }
                    }
                }
                .addOnFailureListener {
                    when(activity){
                        is CartListActivity -> {
                            activity.hideDialog()
                            Timber.d("Error while getting all product list cause $it")
                        }
                    }

                }
    }

    fun removeItemFromCart(context: Context, cart_id: String){
        Timber.d("removeItem cart_id: $cart_id")
        mFireStore.collection(Constants.CART_ITEM)
                .document(cart_id)
                .delete()
                .addOnSuccessListener {
                    when(context){
                        is CartListActivity ->{
                            context.itemRemovedSuccessful()
                        }
                    }
                }
                .addOnFailureListener {
                    e->
                    when(context){
                        is  CartListActivity -> {
                            context.hideDialog()
                        }
                    }
                    Timber.d("Error while removing the item from the cart list")
                }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHasMap: HashMap<String,Any>){
        mFireStore.collection(Constants.CART_ITEM)
                .document(cart_id)
                .update(itemHasMap)
                .addOnSuccessListener {
                    when(context){
                        is CartListActivity ->{
                            context.itemUpdateSuccess()
                        }
                    }
                }
                .addOnFailureListener { e->

                    when(context) {
                        is CartListActivity -> {
                            context.hideDialog()
                        }
                    }

                }
    }


    fun addAddressToFireStore(activity: Activity,addressModel: Address){
        mFireStore.collection(Constants.ADDRESS)
            .add(addressModel)
            .addOnSuccessListener {

                val mHashMap = HashMap<String,Any>()
                mHashMap[Constants.ID] = it.id
                it.update(mHashMap)
                Timber.d("address id hashmap: $mHashMap id:${it.id}")
                Timber.d("address model : ${it.get()}")

                when(activity){
                    is AddEditAddressActivity ->{
                        activity.updateAddressToFirestoreSuccessful()
                    }
                }
            }
            .addOnFailureListener {
                when(activity){
                    is AddEditAddressActivity ->{
                        activity.hideDialog()
                    }
                }
            }
    }

    fun getAddressList(activity: AddressListActivity){
        mFireStore.collection(Constants.ADDRESS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener{ document ->

                val addressList: ArrayList<Address> = ArrayList<Address>()
                for (i in document.documents){
                    val address = i.toObject(Address::class.java)
                    address?.let {
                        addressList.add(it)
                    }
                }
                activity.getAddressListFromFirestoreSuccessful(addressList)
             }
            .addOnFailureListener {
                activity.hideDialog()
                Timber.d("Error while getting the address")
            }
    }


    fun deleteAddress(activity: AddressListActivity,addressId: String){
        mFireStore.collection(Constants.ADDRESS)
                .document(addressId)
                .delete()
                .addOnSuccessListener {
                    activity.deleteAddressSuccessful()

                }
                .addOnFailureListener {
                    activity.hideDialog()
                    Timber.d("Error while deleting address cause $it")
                }
    }


    fun updateAddress(activity: AddEditAddressActivity,addressInfo: Address, addressId: String){

        mFireStore.collection(Constants.ADDRESS)
                .document(addressId)
                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.updateAddressToFirestoreSuccessful()
                    Timber.d("update address in firestore")
                }
                .addOnFailureListener {
                    activity.hideDialog()
                    Timber.d("Error while updating Address to Firestore casue $it")
                }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order){

        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {

                val mHashMap = HashMap<String,Any>()
                activity.orderPlaceSuccessful()

            }
            .addOnFailureListener {
                activity.hideDialog()
                Timber.d("Error while placing an order")
            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order){
        val writeBatch = mFireStore.batch()

        for (cartItem in cartList){


//            val productHashMap = HashMap<String,Any>()
//            productHashMap[Constants.STOCK_QUANTITY] =
//                    (cartItem.stock_quantity) - (cartItem.cart_quantity)

            val soldProduct = SoldProduct(
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price.toString(),
                cartItem.cart_quantity.toString(),
                cartItem.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )





            val documentRef = mFireStore.collection(Constants.SOLD_PRODUCTS)
                    .document(cartItem.product_id)
            writeBatch.set(documentRef,soldProduct)
        }
        for (carItem in cartList){
            val documentRef = mFireStore.collection(Constants.CART_ITEM)
                    .document(carItem.id)
            writeBatch.delete(documentRef)
        }

        writeBatch.commit()
                .addOnSuccessListener {
                activity.updatedAllDetailsSuccessful()

        }
                .addOnFailureListener {
                    activity.hideDialog()
                    Timber.d("Error while updating all the details after order placed")
                }

    }

    fun getSoldProductList(fragment: SoldProductsFragment){
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val list = ArrayList<SoldProduct>()
                for (i in it.documents){

                    val soldProduct = i.toObject(SoldProduct::class.java)
                    soldProduct?.id = i.id
                    if (soldProduct != null) {
                        list.add(soldProduct)
                    }
                }
                fragment.successSoldProductList(list)
            }
            .addOnFailureListener {
                fragment.hideDialog()
                Timber.d("Error while getting the list of sold products.")
            }
    }





    fun getMyOrderList(fragment: OrdersFragment){
        mFireStore.collection(Constants.ORDERS)
                .whereEqualTo(Constants.USER_ID,getCurrentUserId())
                .get()
                .addOnSuccessListener {
                    val orderList = ArrayList<Order>()
                    for (item in it.documents){
                        val model = item.toObject(Order::class.java)
                        if (model != null) {
                            orderList.add(model)
                            model.id = item.id
                            Timber.d("enter getMyOrder ${model.id} and ${item.id}")
                        }
                    }
                    fragment.populateOrdersListInUI(orderList)

                }
                .addOnFailureListener {
                    fragment.hideDialog()
                    Timber.d("Error while getting the orders list cause $it")
                }
    }


}
