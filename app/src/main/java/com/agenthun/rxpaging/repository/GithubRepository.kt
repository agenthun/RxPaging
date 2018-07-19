package com.agenthun.rxpaging.repository

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import android.util.Log
import com.agenthun.rxpaging.api.GithubService
import com.agenthun.rxpaging.db.RepoDb
import com.agenthun.rxpaging.vo.NetworkState
import com.agenthun.rxpaging.vo.Repo
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/5/31 22:28.
 */

class GithubRepository(
        private val db: RepoDb,
        private val service: GithubService) {

    private val boundaryCallback = GithubBoundaryCallback(db, service)
    private var lastList: PagedList<Repo>? = null

    fun search(query: String,
               itemsPerPage: Int = GithubService.ITEMS_PERPAGE,
               loadCallback: (NetworkState, Boolean) -> Unit): Flowable<PagedList<Repo>> {
        val dataSourceFactory = db.reposDao().reposByName("%${query.replace(' ', '%')}%")
        boundaryCallback.query = query
        boundaryCallback.itemsPerPage = itemsPerPage
        boundaryCallback.loadCallback = loadCallback
        return RxPagedListBuilder(dataSourceFactory, itemsPerPage)
                .setBoundaryCallback(boundaryCallback)
                .buildFlowable(BackpressureStrategy.LATEST)
                .flatMap {
                    if (lastList != null) {
                        val isSame = lastList!!.containsAll(it)
                        Log.i("GithubRepository", "isSame = $isSame")
                    }
                    lastList = it
                    return@flatMap Flowable.just(it)
                }
    }

    fun refresh() {
        boundaryCallback.currPage = 1
        boundaryCallback.onZeroItemsLoaded()
    }

    fun retry() {
        boundaryCallback.doOnItemAtEndLoaded()
    }
}