package com.agenthun.rxpaging.ui

import android.arch.lifecycle.ViewModel
import com.agenthun.rxpaging.repository.GithubRepository

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/5/31 23:12.
 */
class ReposViewModel(private val repository: GithubRepository) : ViewModel() {
    fun showSearchResult(query: String) = repository.search(query)
}