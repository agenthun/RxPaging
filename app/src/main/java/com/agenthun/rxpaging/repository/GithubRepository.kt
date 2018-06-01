package com.agenthun.rxpaging.repository

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
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

    fun search(query: String,
               itemsPerPage: Int = GithubService.ITEMS_PERPAGE,
               loadCallback: (NetworkState) -> Unit): Flowable<PagedList<Repo>> {
        val dataSourceFactory = db.reposDao().reposByName("%${query.replace(' ', '%')}%")
        val boundaryCallback = GithubBoundaryCallback(db, service, query, itemsPerPage, loadCallback)
        return RxPagedListBuilder(dataSourceFactory, itemsPerPage)
                .setBoundaryCallback(boundaryCallback)
                .buildFlowable(BackpressureStrategy.LATEST)
    }
}