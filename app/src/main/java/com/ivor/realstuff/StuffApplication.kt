package com.ivor.realstuff

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

class StuffApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .build()
    }
}