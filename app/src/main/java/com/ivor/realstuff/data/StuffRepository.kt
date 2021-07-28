package com.ivor.realstuff.data

import android.util.Log
import com.ivor.realstuff.model.HttpResult
import com.ivor.realstuff.model.Stuff
import com.ivor.realstuff.util.DEFAULT_BATCH_COUNT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StuffRepository(
    private val category: String,
    private val type: String,
    private val batchCount: Int = DEFAULT_BATCH_COUNT,
) {
    private val stuffs = MutableStateFlow<List<Stuff>>(emptyList())
    val stuffsFlow: StateFlow<List<Stuff>> = stuffs
    private var page = 1

    suspend fun refresh(): List<Stuff> {
        page = 1
        return fetch {
            stuffs.value = it
        }
    }

    suspend fun fetch(): List<Stuff> = fetch {
        stuffs.value = stuffs.value + it
    }

    private suspend fun fetch(onData: (List<Stuff>) -> Unit): List<Stuff> {
        val response = apiCall {
            GankApiManager.api.categoryStuffPaged(
                category,
                type,
                page,
                batchCount,
            )
        }
        Log.i("TAG", "fetch $page: $response")
        if (response is HttpResult.Success) {
            onData(response.data.data)
            page++
        } else {
            // TODO: 2021/7/13 handle exception
            onData(emptyList())
        }
        return stuffs.value
    }
}