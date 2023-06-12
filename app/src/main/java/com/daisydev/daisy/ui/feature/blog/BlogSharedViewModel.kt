package com.daisydev.daisy.ui.feature.blog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daisydev.daisy.models.BlogEntry

/**
 * ViewModel compartido para la pantalla de Blog
 * @property selected BlogEntry el cual se comparte entre BlogScreen y EntryBlog
 */
class BlogSharedViewModel {
    // El que se comparte entre BlogScreen y EntryBlog
    private val _selected = MutableLiveData<BlogEntry>()
    val selected: LiveData<BlogEntry> = _selected

    // Función para establecer un blogEntry
    fun setSelectBlogEntry(blogEntry: BlogEntry) {
        _selected.value = blogEntry
    }
}