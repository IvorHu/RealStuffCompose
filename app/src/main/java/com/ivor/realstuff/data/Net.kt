package com.ivor.realstuff.data

import com.ivor.realstuff.model.HttpResult
import com.ivor.realstuff.model.PagedResult
import com.ivor.realstuff.model.Stuff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException


private const val BASE_URL = "https://gank.io/api/v2/"

interface GankApi {

    @GET("data/category/{category}/type/{type}/page/{page}/count/{count}")
    suspend fun categoryStuffPaged(
        @Path("category") category: String,
        @Path("type") type: String,
        @Path("page") page: Int,
        @Path("count") count: Int,
    ): Response<PagedResult<List<Stuff>>>

}

object GankApiManager {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api = retrofit.create(GankApi::class.java)
}

suspend fun <T> apiCall(
    call: suspend () -> Response<T>,
): HttpResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()!!
                return@withContext HttpResult.Success(body)
            } else {
                return@withContext HttpResult.Failed(
                    response.code(),
                    response.errorBody()?.string() ?: ""
                )
            }
        } catch (e: IOException) {
            return@withContext HttpResult.Exception(e)
        }
    }
}