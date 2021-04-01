package com.example.google_analytics

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.google_analytics.Adapter.ProductAdapter
import com.example.google_analytics.modle.MyProducts
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_products.*
import java.util.*

class MainProductsActivity : AppCompatActivity(), ProductAdapter.onProductsItemClickListener {

    lateinit var db: FirebaseFirestore
    var categoryImageElement:String?=null
    private var progressDialog: ProgressDialog?=null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var startTime: Long = 0
    var endTime: Long = 0
    var totalTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        startTime = Calendar.getInstance().timeInMillis

        db = Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("product screen Element")

        showDialog()
            categoryImageElement=intent.getStringExtra("categoryImage")
            val catName=intent.getStringExtra("categoryName")
            Picasso.get().load(categoryImageElement).into(category_image)
            txt_category_name.text=catName
            getProductsAccordingToCategory("$catName")

        category_back_element.setOnClickListener {
            endTime = Calendar.getInstance().timeInMillis
            totalTime = endTime - startTime

            val minutes: Long = totalTime / 1000 / 60
            val seconds = (totalTime / 1000 % 60)
            timeSpendInScreenElement("$minutes m $seconds s","aya","ProductActivity")

            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

    }

    private fun getProductsAccordingToCategory(catName:String){
        val dataProduct = mutableListOf<MyProducts>()

        db.collection("MyProducts").whereEqualTo("categoryName",catName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("aya", "${document.id} -> ${document.get("name")}")
                        val id = document.id
                        val data = document.data
                        val name = data["name"] as String?
                        val image = data["image"] as String?
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        dataProduct.add(
                            MyProducts(id,image,name,description,categoryName)
                        )
                    }

                    recyle_product.layoutManager = GridLayoutManager(this,3)
                    recyle_product.setHasFixedSize(true)
                    val productAdapter = ProductAdapter(this, dataProduct,this)
                    recyle_product.adapter = productAdapter

                }
                hideDialog()
            }
    }

    private fun showDialog() {

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading Products Element ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog(){
        if(progressDialog!!.isShowing){

            progressDialog!!.dismiss()
        }
    }

    private fun trackScreen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    private fun selectContent(id:String, name:String, contentType:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun timeSpendInScreenElement(time: String, userId:String, pageName:String){

        val time= hashMapOf("time" to time,"userId" to userId,"pageName" to pageName)
        db.collection("Time")
                .add(time)
                .addOnSuccessListener {documentReference ->
                    Log.e("aya","time added successfully")
                }
                .addOnFailureListener {exception ->
                    Log.e("aya", exception.message.toString())
                }
    }

    override fun onItemClick(data: MyProducts, position: Int) {
        selectContent(data.id!!,data.name!!,data.image!!)

        endTime = Calendar.getInstance().timeInMillis
        totalTime = endTime - startTime

        val minutes: Long = totalTime / 1000 / 60
        val seconds = (totalTime / 1000 % 60)
        timeSpendInScreenElement("$minutes m $seconds s","123456","ProductActivity")

        var i = Intent(this,DetailsProduct::class.java)
        i.putExtra("id",data.id)
        i.putExtra("name",data.name)
        i.putExtra("image",data.image)

        i.putExtra("description",data.description)
        i.putExtra("category",data.categoryNameElement)
        startActivity(i)
    }

}