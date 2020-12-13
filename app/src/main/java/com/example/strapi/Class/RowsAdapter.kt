package com.example.strapi.Class

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.strapi.Fragments.ListProductsFragment
import com.example.strapi.Fragments.ListProductsFragmentDirections
import com.example.strapi.R
import kotlinx.android.synthetic.main.fragment_add_products.view.*
import kotlinx.android.synthetic.main.recycler_products_row.view.*

class RowsAdapter(private val context: ListProductsFragment): RecyclerView.Adapter<RowsAdapter.MyHolder>(){

    private var rowsRecyclerView = emptyList<Product>()

    fun getData(data : ArrayList<Product>) {
        rowsRecyclerView = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            LayoutInflater.from(parent.context).
        inflate(R.layout.recycler_products_row, parent, false))
    }

    override fun getItemCount(): Int {
        return rowsRecyclerView.count()
    }

    inner class MyHolder(itemView: View, var product: Product? = null): RecyclerView.ViewHolder(itemView) {

        fun bindView( product: Product) {
            itemView.nameRecyclerView.text = product.name
            itemView.modelRecyclerView.text = product.model
            itemView.priceRecyclerView.text = product.price.toString()
        }

        init {
            itemView.setOnClickListener {
                product?.let {
                    val directions =  ListProductsFragmentDirections.actionListProductsFragmentToUpdateProductsFragment(it)
                    itemView.findNavController().navigate(directions)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val currentItem = rowsRecyclerView[position]
        holder.bindView(currentItem)
        holder.product = currentItem
    }

}