package com.ivor.realstuff.ui.image

import androidx.lifecycle.ViewModel
import com.ivor.realstuff.data.StuffsRepository
import com.ivor.realstuff.model.Stuff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val stuffsRepository: StuffsRepository) :
    ViewModel() {
    private val repository = stuffsRepository.imagesRepository

    fun queryImageIndex(id: String): Flow<Int> = repository.queryStuffIndex(id)

    val imagesFlow: Flow<List<Stuff>> = repository.stuffsFlow

    private val showDescription = MutableStateFlow(true)
    val showDescriptionFlow = showDescription

    fun toggleShowDescription() {
        showDescription.value = !showDescription.value
    }
}