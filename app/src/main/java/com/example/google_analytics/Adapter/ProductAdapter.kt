package com.example.google_analytics.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.google_analytics.modle.MyProducts
import com.example.google_analytics.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.products_item_admin.view.*

class ProductAdapter (var activity: Context?, var data: MutableList<MyProducts>, var clickListener: ProductAdapter.onProductsItemClickListener) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productImage  =itemView.product_photo
        val name=itemView.product_name
        val price=itemView.product_price

        fun initialize(data: MyProducts, action:onProductsItemClickListener){

            Picasso.get().load(data.image).into(productImage)
            name.text = data.name
           // price.text = data.price.toString()

            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.products_item_admin,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ProductAdapter.MyViewHolder, position: Int) {
        holder.initialize(data.get(position), clickListener)
    }
    interface onProductsItemClickListener{
        fun onItemClick(data:MyProducts, position: Int)
    }
}