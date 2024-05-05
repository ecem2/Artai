package com.adentech.artai.extensions


import androidx.lifecycle.*
import com.adentech.artai.core.common.Event
import com.adentech.artai.core.common.Resource
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (T) -> Unit) {
    liveData.observe(this, Observer {
        it?.let { t -> observer(t) }
    })
}

fun <T> LifecycleOwner.observeEvent(liveData: LiveData<Event<T>>, observer: (T) -> Unit) {
    liveData.observe(this, Observer {
        it.getContentIfNotHandled()?.let { t -> observer(t) }
    })
}

fun <T> LifecycleOwner.collect(sharedFlow: SharedFlow<T>, observer: (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            sharedFlow.collect { t -> observer(t) }
        }
    }
}