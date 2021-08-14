package com.ivor.realstuff.data

import com.ivor.realstuff.util.DEFAULT_BATCH_IMAGES_COUNT
import com.ivor.realstuff.util.GankApi
import com.ivor.realstuff.util.HomeCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StuffsRepository @Inject constructor(private val gankApi: GankApi) {
    val imagesRepository by lazy {
        StuffRepository(
            HomeCategory.Image.category,
            HomeCategory.Image.type,
            gankApi,
            DEFAULT_BATCH_IMAGES_COUNT,
        )
    }
    val androidRepository by lazy {
        StuffRepository(HomeCategory.ANDROID.category, HomeCategory.ANDROID.type, gankApi)
    }
    val iosRepository by lazy {
        StuffRepository(HomeCategory.IOS.category, HomeCategory.IOS.type, gankApi)
    }
    val webRepository by lazy {
        StuffRepository(HomeCategory.WEB.category, HomeCategory.WEB.type, gankApi)
    }

    fun repositoryFromTab(tab: HomeCategory): StuffRepository {
        return when (tab) {
            HomeCategory.Image -> imagesRepository
            HomeCategory.ANDROID -> androidRepository
            HomeCategory.IOS -> iosRepository
            HomeCategory.WEB -> webRepository
        }
    }
}