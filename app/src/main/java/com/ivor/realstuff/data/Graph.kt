package com.ivor.realstuff.data

import com.ivor.realstuff.util.HomeCategory


// TODO: 2021/7/13 refact by Hilt
object Graph {
    val imagesRepository by lazy {
        StuffRepository(
            HomeCategory.Image.category,
            HomeCategory.Image.type
        )
    }
    val androidRepository by lazy {
        StuffRepository(
            HomeCategory.ANDROID.category,
            HomeCategory.ANDROID.type
        )
    }
    val iosRepository by lazy { StuffRepository(HomeCategory.IOS.category, HomeCategory.IOS.type) }
    val webRepository by lazy { StuffRepository(HomeCategory.WEB.category, HomeCategory.WEB.type) }

    fun repositoryFromTab(tab: HomeCategory): StuffRepository {
        return when (tab) {
            HomeCategory.Image -> imagesRepository
            HomeCategory.ANDROID -> androidRepository
            HomeCategory.IOS -> iosRepository
            HomeCategory.WEB -> webRepository
        }
    }
}