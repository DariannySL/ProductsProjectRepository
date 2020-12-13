package com.example.strapi.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.strapi.Class.Product
import com.example.strapi.Class.ProductsServices
import com.example.strapi.Class.RetrofitHelper
import com.example.strapi.Class.RowsAdapter
import com.example.strapi.R
import kotlinx.android.synthetic.main.fragment_add_products.*
import kotlinx.android.synthetic.main.fragment_update_products.*
import kotlinx.android.synthetic.main.fragment_update_products.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProductsFragment : Fragment() {

    var productService : ProductsServices? = null
    private lateinit var adapter : RowsAdapter
    private val args : UpdateProductsFragmentArgs by navArgs()
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.listProductsFragment)
        }

    }

    fun updateProduct() {
        val name = nameUpdateTextView.text.toString()
        val model = modelUpdateTextView.text.toString()
        val description = descriptionUpdateTextView.text.toString()
        val price = priceUpdateTextView.text.toString().toFloat()
        val product = Product(product.id, name, price, model, description)

        val accessToken : String = getAccessToken()!!

        productService = RetrofitHelper.getAuthenticatedInstance(getString(R.string.base_url), accessToken)
        productService!!.updateProducts(product.id, product).enqueue(object : Callback<Product> {

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(activity, "Error" + t.message, Toast.LENGTH_SHORT ).show()
            }

            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                findNavController().navigate(R.id.listProductsFragment)
                Toast.makeText(activity, "Actualizado satisfactoriamente", Toast.LENGTH_SHORT ).show()
            }

        })
    }

    fun deleteProduct() {

        val accessToken : String = getAccessToken()!!

        productService = RetrofitHelper.getAuthenticatedInstance(getString(R.string.base_url), accessToken)
        productService!!.deleteProducts(product.id).enqueue(object : Callback<Unit> {

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(activity, "Error" + t.message, Toast.LENGTH_SHORT ).show()
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                findNavController().navigate(R.id.listProductsFragment)
                Toast.makeText(activity, "Eliminado satisfactoriamente", Toast.LENGTH_SHORT ).show()
            }

        })
    }

    fun getDialog () {
        AlertDialog.Builder(this.activity)
            .setTitle(R.string.text_dialog)
            .setPositiveButton(R.string.delete
            ) { dialog, id ->
                deleteProduct()
                findNavController().navigate(R.id.listProductsFragment)
            }
            .setNegativeButton(R.string.cancel
            ) { dialog, id ->

            }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_update_products, container, false)

        product = args.products

        view.updateProductButton.setOnClickListener {
            updateProduct()
        }

        view.deleteProductButton.setOnClickListener {
            getDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameUpdateTextView.setText(product.name)
        modelUpdateTextView.setText(product.model)
        priceUpdateTextView.setText(product.price.toString())
        descriptionUpdateTextView.setText(product.description)
    }

    private fun getAccessToken(): String? {
        return this.requireActivity().getSharedPreferences(LoginFragment.DEFAULT_PREDERENCES_KEY, Context.MODE_PRIVATE)
            .getString(getString(R.string.access_token_key), "")
    }

}