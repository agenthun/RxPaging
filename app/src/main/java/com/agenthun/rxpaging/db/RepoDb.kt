package com.agenthun.rxpaging.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.agenthun.rxpaging.vo.Repo

/**
 * @project RxPaging
 * @authors Henry Hu
 * @date    2018/5/31 18:17.
 */
@Database(
        entities = [Repo::class],
        version = 1,
        exportSchema = false
)
abstract class RepoDb : RoomDatabase() {
    abstract fun reposDao(): RepoDao

    companion object {
        private var INSTANCE: RepoDb? = null
        fun getInstance(context: Context): RepoDb =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        RepoDb::class.java, "Github.db")
                        .build()
    }
}