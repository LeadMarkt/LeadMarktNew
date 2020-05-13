package com.leadmarkt.leadmarkt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class StoreProductAdapter(private val favTitleArray: ArrayList<String>,private val favPriceArray: ArrayList<String>,private val favImageArray: ArrayList<String>):
    RecyclerView.Adapter<StoreProductAdapter.FavViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.store_product_recycler_view_row,parent,false)

        return FavViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return favTitleArray.size
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {

        holder.recyclerFavTitle?.text = favTitleArray[position]
        holder.recyclerFavPrice?.text = favPriceArray[position]
        Picasso.get().load(favImageArray[position]).into(holder.recyclerFavImage)
    }

    class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var recyclerFavTitle: TextView? = null
        var recyclerFavPrice: TextView? = null
       var recyclerFavImage: ImageView? = null

        init {
            recyclerFavTitle = itemView.findViewById(R.id.recyclerFavTitle)
            recyclerFavPrice = itemView.findViewById(R.id.recyclerFavPrice)
            recyclerFavImage = itemView.findViewById(R.id.recyclerFavImage)



        }
    }
}