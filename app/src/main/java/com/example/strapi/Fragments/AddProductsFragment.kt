package com.example.strapi.Fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.example.strapi.Class.Product
import com.example.strapi.Class.ProductsServices
import com.example.strapi.Class.RetrofitHelper
import com.example.strapi.R
import kotlinx.android.synthetic.main.fragment_add_products.*
import kotlinx.android.synthetic.main.fragment_add_products.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductsFragment : Fragment() {

    var productService : ProductsServices? = null
    private lateinit var bundle : Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.listProductsFragment)
        }

    }


    fun addProduct() {
        val name = nameTextView.text.toString()
        val model = modelTextView.text.toString()
        val description = descriptionTextView.text.toString()
        val price = priceTextView.text.toString().toFloat()

        val accessToken : String = getAccessToken()!!

        productService = RetrofitHelper.getAuthenticatedInstance(getString(R.string.base_url), accessToken)
        productService!!.createProducts(name, model, price, description).enqueue(object : Callback<Product> {

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(activity, "Error" + t.message, Toast.LENGTH_SHORT ).show()
            }

            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                findNavController().navigate(R.id.listProductsFragment)
                Toast.makeText(activity, "Guardado satisfactoriamente", Toast.LENGTH_SHORT ).show()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_products, container, false)

        view.addProductButton.setOnClickListener {
            if(inputValidate(nameTextView.text.toString(), modelTextView.text.toString(),
                    priceTextView.text.toString(), descriptionTextView.text.toString())) {
                addProduct()
                view.removeCallbacks {
                    return@removeCallbacks
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun inputValidate(name: String, model: String, price: String, description: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                nameTextView.error = "Este campo es requerido"
                false
            }

            TextUtils.isEmpty(model) -> {
                modelTextView.error = "Este campo es requerido"
                false
            }

            TextUtils.isEmpty(price) -> {
                priceTextView.error = "Este campo es requerido"
                false
            }

            TextUtils.isEmpty(description) -> {
                descriptionTextView.error = "Este campo es requerido"
                false
            }
            else -> true
        }
    }

    private fun getAccessToken(): String? {
        return this.requireActivity().getSharedPreferences(LoginFragment.DEFAULT_PREDERENCES_KEY, Context.MODE_PRIVATE)
            .getString(getString(R.string.access_token_key), "")
    }
}