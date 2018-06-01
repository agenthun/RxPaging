package com.agenthun.rxpaging.repository

import android.arch.paging.PagedList
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
class GithubBoundaryCallback(
        private val db: RepoDb,
        private val service: GithubService,
        private val query: String,
        private val itemsPerPage: Int) : PagedList.BoundaryCallback<Repo>() {
    private var currPage: Int = 1
    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        if (currPage == 1)
            search(currPage)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        super.onItemAtEndLoaded(itemAtEnd)
        search(currPage)
    }

    private fun search(page: Int) {
        val apiQuery = query + GithubService.IN_QUALIFIER
        service.searchRepos(apiQuery, page, itemsPerPage)
                .flatMap {
                    db.reposDao().insert(it.items)
                    currPage++
                    return@flatMap Flowable.just(it)
                }.subscribeOn(Schedulers.io()).subscribe()
    }
}