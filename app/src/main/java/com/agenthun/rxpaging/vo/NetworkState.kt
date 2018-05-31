package com.agenthun.rxpaging.vo

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/6/1 00:20.
 */
enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

data class NetworkState(
        val status: Status,
        val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}