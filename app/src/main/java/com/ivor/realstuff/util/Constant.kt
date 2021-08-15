package com.ivor.realstuff.util

const val DEFAULT_BATCH_COUNT = 20
const val DEFAULT_BATCH_IMAGES_COUNT = 10

enum class HomeCategory(val title: String, val category: String, val type: String) {
    ANDROID("Android", "GanHuo", "Android"),
    IOS("iOS", "GanHuo", "iOS"),
    WEB("前端", "GanHuo", "frontend"),
    Image("妹纸", "Girl", "Girl"),
}

