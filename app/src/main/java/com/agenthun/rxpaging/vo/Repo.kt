package com.agenthun.rxpaging.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @project RxPaging
 * @authors Henry Hu
 * @date    2018/5/31 18:12.
 */
@Entity(tableName = "repos")
data class Repo(
        @PrimaryKey @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("full_name") val fullName: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("html_url") val url: String,
        @SerializedName("stargazers_count") val stars: Int,
        @SerializedName("forks_count") val forks: Int,
        @SerializedName("language") val language: String?
)