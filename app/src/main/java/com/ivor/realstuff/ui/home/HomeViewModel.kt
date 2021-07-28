package com.ivor.realstuff.ui.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivor.realstuff.data.Graph
import com.ivor.realstuff.data.StuffRepository
import com.ivor.realstuff.model.Stuff
import com.ivor.realstuff.util.HomeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState>
        get() = _state
    private val scrollStates = List(HomeCategory.values().size) {
        if (it == HomeCategory.Image.ordinal) {
            ScrollState(0)
        } else {
            LazyListState()
        }
    }
    val scrollState: ScrollableState
        get() = scrollStates[_state.value.tab.ordinal]

    fun fetch() {
        if (_state.value.loading) return

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val stuffs = repositoryFromTab().fetch()

            if (isActive) {
                _state.value = _state.value.copy(loading = false, stuffs = stuffs)
            } else {
                _state.value = _state.value.copy(loading = false)
            }
        }
    }

    fun refresh(tab: HomeCategory = _state.value.tab) {
        if (_state.value.loading) return
        viewModelScope.launch {
            _state.value = _state.value.copy(tab = tab, loading = true)
            val stuffs = repositoryFromTab(tab = tab).refresh()
            if (isActive) {
                _state.value = _state.value.copy(loading = false, stuffs = stuffs)
            } else {
                _state.value = _state.value.copy(loading = false)
            }
        }
    }

    fun updateTab(tab: HomeCategory) {
        val repository = repositoryFromTab(tab = tab)
        val stuffs = repository.stuffsFlow.value
        _state.value = _state.value.copy(tab = tab, stuffs = stuffs)
    }

    private fun repositoryFromTab(tab: HomeCategory = _state.value.tab): StuffRepository {
        return when (tab) {
            HomeCategory.Image -> Graph.imagesRepository
            HomeCategory.ANDROID -> Graph.androidRepository
            HomeCategory.IOS -> Graph.iosRepository
            HomeCategory.WEB -> Graph.webRepository
        }
    }
}

data class HomeViewState(
    val tab: HomeCategory = HomeCategory.values().first(),
    val loading: Boolean = false,
    val stuffs: List<Stuff> = emptyList()
)
