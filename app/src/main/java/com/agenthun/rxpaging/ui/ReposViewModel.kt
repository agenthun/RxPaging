package com.agenthun.rxpaging.ui

import android.arch.lifecycle.ViewModel
import com.agenthun.rxpaging.repository.GithubRepository
import com.agenthun.rxpaging.vo.NetworkState

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/5/31 23:12.
 */
class ReposViewModel(private val repository: GithubRepository) : ViewModel() {
    fun showSearchResult(query: String, loadCallback: (NetworkState, Boolean) -> Unit) =
            repository.search(query = query, loadCallback = loadCallback)

    fun refreshSearch() = repository.refresh()

    fun retry() = repository.retry()
}