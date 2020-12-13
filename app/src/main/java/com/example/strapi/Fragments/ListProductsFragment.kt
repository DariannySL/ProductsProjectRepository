package com.example.strapi.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.strapi.*
import com.example.strapi.Class.Product
import com.example.strapi.Class.ProductsServices
import com.example.strapi.Class.RetrofitHelper
import com.example.strapi.Class.RowsAdapter
import com.example.strapi.Fragments.LoginFragment.Companion.DEFAULT_PREDERENCES_KEY
import kotlinx.android.synthetic.main.fragment_list_products.view.*
import kotlinx.android.synthetic.main.recycler_products_row.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListProductsFragment : Fragment() {

    var productService : ProductsServices? = null
    private lateinit var adapter : RowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val accessToken : String = getAccessToken()!!

        productService = RetrofitHelper.getAuthenticatedInstance(getString(R.string.base_url), accessToken)
        productService!!.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(activity, "Error" + t.message, Toast.LENGTH_SHORT ).show()
            }

            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                adapter.getData(response.body()!!.toList() as ArrayList<Product>)
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        adapter = RowsAdapter(this)

        var view = inflater.inflate(R.layout.fragment_list_products, container, false)

        view.recyclerView.layoutManager = LinearLayoutManager(this.requireActivity())
        view.recyclerView.adapter = adapter

        view.addCircleButton.setOnClickListener {
            findNavController().navigate(R.id.addProductsFragment)
        }

        return view
    }

    private fun getAccessToken(): String? {
        return this.requireActivity().getSharedPreferences(DEFAULT_PREDERENCES_KEY, Context.MODE_PRIVATE)
            .getString(getString(R.string.access_token_key), "")
    }
}