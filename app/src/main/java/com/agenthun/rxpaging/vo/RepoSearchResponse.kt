package com.agenthun.rxpaging.vo

import com.agenthun.rxpaging.api.GithubService
import com.google.gson.annotations.SerializedName

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/5/31 22:21.
 */
data class RepoSearchResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Repo> = emptyList()
) {
    fun totalPage() = Math.ceil(total * 1.0 / GithubService.ITEMS_PERPAGE).toInt()

    fun hasNextPage(currPage: Int) = currPage < totalPage()
}