package com.example.strapi.Class

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ProductsServices {
    @POST("auth/local")
    fun GetToken (@Body() credentials: Credentials) : Call<TokenInfo>

    @GET("products?_sort=id:desc")
    fun getProducts( @Query("_limit") limit: Int = 20) : Call<List<Product>>

    @FormUrlEncoded
    @POST("products")
    fun createProducts(
        @Field("name") name: String,
        @Field("model") model: String,
        @Field("price") price: Float,
        @Field("description") description: String
    ):Call<Product>

    @PUT("products/{id}")
    fun updateProducts(@Path( "id") id: Int, @Body product: Product) : Call<Product>

    @DELETE("products/{id}")
    fun deleteProducts( @Path("id") id: Int) : Call<Unit>
}

public class  RetrofitHelper {
    companion object {
        fun getInstance(baseUrl : String) : ProductsServices {

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient())
                .build()

            return retrofit.create(ProductsServices::class.java)
        }

        fun getAuthenticatedInstance(baseUrl: String, token: String) : ProductsServices {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor{ chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build()

                    chain.proceed(newRequest)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return  retrofit.create(ProductsServices::class.java)
        }
    }
}

data class  Credentials (
    val identifier : String,
    val password : String
)

public class TokenInfo {
    lateinit var jwt : String
}

@Parcelize
data class Product (
    val id: Int,
    val name: String,
    val price: Float,
    val model: String,
    val description: String
) : Parcelable