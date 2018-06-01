package com.agenthun.rxpaging.repository

import android.arch.paging.PagedList
import android.util.Log
import com.agenthun.rxpaging.api.GithubService
import com.agenthun.rxpaging.db.RepoDb
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
        private val service: GithubService,
        private val query: String,
        private val itemsPerPage: Int) : PagedList.BoundaryCallback<Repo>() {
    private var currPage = 1
    private var maxPage = 1
    private var isRequestInProgress = false

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        Log.d(TAG, "onZeroItemsLoaded")
        search(currPage)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        super.onItemAtEndLoaded(itemAtEnd)
        Log.d(TAG, "onItemAtEndLoaded")
        if (maxPage != 1 && currPage < maxPage)
            search(currPage)
    }

    private fun search(page: Int) {
        if (isRequestInProgress) return
        isRequestInProgress = true
        Log.d(TAG, "search, page=$page")

        val apiQuery = query + GithubService.IN_QUALIFIER
        service.searchRepos(apiQuery, page, itemsPerPage)
                .filter {
                    isRequestInProgress = false
                    return@filter it.items.isNotEmpty()
                }
                .flatMap {
                    db.reposDao().insert(it.items)
                    return@flatMap Flowable.just(it)
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            maxPage = it.totalPage()
                            val hasNextPage = it.hasNextPage(currPage)
                            Log.d(TAG, "search, success, maxPage: $maxPage, currPage: $currPage, hasNextPage: $hasNextPage")
                            if (hasNextPage) {
                                currPage++
                            }
                        },
                        {
                            Log.e(TAG, "search, error: ${it.message}")
                        }
                )
    }
}