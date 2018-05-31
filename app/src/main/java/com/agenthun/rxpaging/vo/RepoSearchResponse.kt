package com.agenthun.rxpaging.vo

import com.google.gson.annotations.SerializedName

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/5/31 22:21.
 */
data class RepoSearchResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Repo> = emptyList(),
        val nextPage: Int? = null
)