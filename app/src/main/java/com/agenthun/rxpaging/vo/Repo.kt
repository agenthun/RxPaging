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
        @PrimaryKey @field:SerializedName("id") val id: Long,
        @field:SerializedName("name") val name: String,
        @field:SerializedName("fullName") val fullName: String,
        @field:SerializedName("description") val description: String?,
        @field:SerializedName("url") val url: String,
        @field:SerializedName("stars") val stars: Int,
        @field:SerializedName("forks") val forks: Int,
        @field:SerializedName("language") val language: String?
)