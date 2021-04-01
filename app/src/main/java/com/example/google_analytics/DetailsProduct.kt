package com.example.google_analytics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_details.*
import java.util.*

class DetailsProduct : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var startTime: Long = 0
    var endTime: Long = 0
    var totalTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        startTime = Calendar.getInstance().timeInMillis
        db= Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("product details Element")

            val name1 = intent.getStringExtra("name1")
            val image1 = intent.getStringExtra("image1")
            val price1 = intent.getDoubleExtra("price1",0.0)
            val description1 = intent.getStringExtra("description1")

            Picasso.get().load(image1).into(product_image)
            product_name.text = name1

            product_description_element.text = description1


        back_to_products_element.setOnClickListener {
            endTime = Calendar.getInstance().timeInMillis
            totalTime = endTime - startTime
            val minutes: Long = totalTime / 1000 / 60
            val seconds = (totalTime / 1000 % 60)
            timeSpendInScreenElement("$minutes m $seconds s","123456","ProductDetailsElement")
            onBackPressed()
        }

}

    private fun trackScreen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
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

}