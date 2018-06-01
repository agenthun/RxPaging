package com.agenthun.rxpaging.repository

import android.arch.paging.PagedList
import android.util.Log
import com.agenthun.rxpaging.api.GithubService
import com.agenthun.rxpaging.db.RepoDb
import com.agenthun.rxpaging.vo.NetworkState
import com.agenthun.rxpaging.vo.Repo
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/5/31 23:34.
 */
private const val TAG = "GithubBoundaryCallback"

class GithubBoundaryCallback(
        private val db: RepoDb,
        private val service: GithubService) : PagedList.BoundaryCallback<Repo>() {

    lateinit var query: String
    var itemsPerPage = GithubService.ITEMS_PERPAGE
    var currPage = 1
    private var hasNextPage = true
    private var isRequestInProgress = false
    lateinit var loadCallback: (NetworkState, Boolean) -> Unit

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        Log.d(TAG, "onZeroItemsLoaded")
        search()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        super.onItemAtEndLoaded(itemAtEnd)
        Log.d(TAG, "onItemAtEndLoaded")
        doOnItemAtEndLoaded()
    }

    fun doOnItemAtEndLoaded() {
        if (hasNextPage)
            search()
    }

    private fun search() {
        if (isRequestInProgress) return
        isRequestInProgress = true
        Log.d(TAG, "search start, page=$currPage")

        val apiQuery = query + GithubService.IN_QUALIFIER
        loadCallback(NetworkState.LOADING, currPage == 1)
        service.searchRepos(apiQuery, currPage, itemsPerPage)
                .filter {
                    return@filter it.items.isNotEmpty()
                }
                .flatMap {
                    db.reposDao().insert(it.items)
                    return@flatMap Flowable.just(it)
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            hasNextPage = it.hasNextPage(currPage)
                            Log.d(TAG, "search, success, totalPage: ${it.totalPage()}, currPage: $currPage, hasNextPage: $hasNextPage")
                            if (hasNextPage) {
                                currPage++
                            }
                            loadCallback(NetworkState.LOADED, currPage == 1)
                            isRequestInProgress = false
                        },
                        {
                            Log.e(TAG, "search, error: ${it.message}")
                            loadCallback(NetworkState.error(it.message), currPage == 1)
                            isRequestInProgress = false
                        }
                )
    }
}