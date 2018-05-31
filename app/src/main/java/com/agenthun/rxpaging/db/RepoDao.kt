package com.agenthun.rxpaging.db

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.agenthun.rxpaging.vo.Repo
import io.reactivex.Flowable

/**
 * @project RxPaging
 * @authors Henry Hu
 * @date    2018/5/31 18:17.
 */
@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<Repo>)

    //    @Query("SELECT * FROM repos WHERE (name LIKE :queryString) OR (description LIKE " + ":queryString) ORDER BY stars DESC, name ASC")
    @Query("SELECT * FROM repos WHERE name LIKE :queryString ORDER BY stars DESC")
    fun reposByName(queryString: String): DataSource.Factory<Int, Repo>
}