package com.ivor.realstuff.model

import com.google.gson.annotations.SerializedName
import java.io.IOException

sealed class HttpResult<out T> {
    data class Exception(val e: IOException) : HttpResult<Nothing>()
    data class Success<out T>(val data: T) :
        HttpResult<T>()

    data class Failed(val status: Int, val msg: String? = null) : HttpResult<Nothing>()
}

data class Stuff(
    @SerializedName("_id")
    val id: String,
    val author: String,
    val category: String,
    val createdAt: String,
    val desc: String,
    val images: List<String>,
    val likeCounts: Int,
    val publishedAt: String,
    val stars: Int,
    val title: String,
    val type: String,
    val url: String,
    val views: Int
)

data class PagedResult<T>(
    val `data`: T,
    val page: Int,
    @SerializedName("page_count")
    val pageCount: Int,
    val status: Int,
    @SerializedName("total_counts")
    val totalCounts: Int
)