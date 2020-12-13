package com.example.strapi.Fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.strapi.*
import com.example.strapi.Class.Credentials
import com.example.strapi.Class.RetrofitHelper
import com.example.strapi.Class.TokenInfo
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

val TAG: String = "APP_MESSAGE"

class LoginFragment : Fragment() {

    companion object {
        const val DEFAULT_PREDERENCES_KEY : String = "DEFAULT_PREFERENCES"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun getCredentials() {

        var apiClient = RetrofitHelper.getInstance(getString(R.string.base_url))

        var credentials = Credentials(
            emailTextView.text.toString(),
            passwordTextView.text.toString()
        )

        val getTokenRequest = apiClient.GetToken(credentials)

        getTokenRequest.enqueue(object : retrofit2.Callback<TokenInfo> {

            override fun onResponse(
                call: retrofit2.Call<TokenInfo>,
                response: retrofit2.Response<TokenInfo>
            ) {
                if (response.isSuccessful) {
                    activity!!.runOnUiThread {
                        run {

                            storeAccessToken(response.body()!!.jwt)

                            findNavController().navigate(R.id.listProductsFragment)

                        }
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<TokenInfo>, t: Throwable) {
                activity!!.runOnUiThread {
                    run {
                        Toast.makeText(activity, "ERROR" + t.message, Toast.LENGTH_SHORT ).show()
                    }
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.loginButton.setOnClickListener() {
            if(inputValidate(emailTextView.text.toString(), passwordTextView.text.toString())) {
                getCredentials()
            }
        }

        return view
    }

    private fun inputValidate(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                emailTextView.error = "Este campo es requerido"
                false
            }

            TextUtils.isEmpty(password) -> {
                passwordTextView.error = "Este campo es requerido"
                false
            }
            else -> true
        }
    }

    fun storeAccessToken(token: String) {
        val sharedPreferencesEdit = this.requireActivity().getSharedPreferences(DEFAULT_PREDERENCES_KEY, Context.MODE_PRIVATE).edit()
        sharedPreferencesEdit.putString(getString(R.string.access_token_key), token)
        sharedPreferencesEdit.commit()
        Log.i(TAG, "Token stored: " + token)
    }

    private fun getAccessToken(): String? {
        return this.requireActivity().getSharedPreferences(DEFAULT_PREDERENCES_KEY, Context.MODE_PRIVATE)
            .getString(getString(R.string.access_token_key), "")
    }
}