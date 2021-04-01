package com.example.google_analytics

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googleanalyticsassignment.Adapter.CategoriesAdapterElement
import com.example.googleanalyticsassignment.modle.MyCategory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() , CategoriesAdapterElement.onCategoryItemClickListener {

    lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog?=null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var startTime: Long = 0
    var endTime: Long = 0
    var totalTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startTime = Calendar.getInstance().timeInMillis

        db = Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("category screen Element")
        showDialog()
        getAllCategories()
    }

    override fun onItemClick(data: MyCategory, position: Int) {
        selectContent(data.id,data.nameCategoryElement!!,data.imageCategoryElement!!)
        endTime = Calendar.getInstance().timeInMillis
        totalTime = endTime - startTime

        val minutes: Long = totalTime / 1000 / 60
        val seconds = (totalTime / 1000 % 60)
        timeSpendInScreenElement("$minutes m $seconds s","aya","MainActivity")


        var i = Intent(this,MainProductsActivity::class.java)
        i.putExtra("id",data.id)
        i.putExtra("categoryImage",data.imageCategoryElement)
        i.putExtra("categoryName",data.nameCategoryElement)
        startActivity(i)

    }

    private fun getAllCategories(){
        val categoryList= mutableListOf<MyCategory>()
        db.collection("MyCategories")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("aya", "${document.id} -> ${document.get("category_name")} -> ${document.get("category_image")}")
                        val id = document.id
                        val data = document.data
                        val categoryName = data["category_name"] as String?
                        val categoryImage = data["category_image"] as String?
                        categoryList.add(MyCategory(id, categoryImage, categoryName))
                    }
                    recycle_category?.layoutManager =
                        LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    recycle_category.setHasFixedSize(true)
                    val categoriesAdapter = CategoriesAdapterElement(this, categoryList, this)
                    recycle_category.adapter = categoriesAdapter
                }
                hideDialog()
            }
    }

    private fun showDialog() {

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading Categories ...")
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
                Log.e("aya","time added successfully in your project")
            }
            .addOnFailureListener {exception ->
                Log.e("aya", exception.message.toString())
            }
    }




}

