package com.ivor.realstuff.ui.image

import androidx.lifecycle.ViewModel
import com.ivor.realstuff.data.Graph
import com.ivor.realstuff.model.Stuff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ImageViewModel : ViewModel() {
    private val repository = Graph.imagesRepository

    fun queryImageIndex(id: String): Flow<Int> = repository.queryStuffIndex(id)

    val imagesFlow: Flow<List<Stuff>> = repository.stuffsFlow

    private val showDescription = MutableStateFlow(true)
    val showDescriptionFlow = showDescription

    fun toggleShowDescription() {
        showDescription.value = !showDescription.value
    }
}