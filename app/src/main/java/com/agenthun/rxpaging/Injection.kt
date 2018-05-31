package com.agenthun.rxpaging

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.agenthun.rxpaging.api.GithubService
import com.agenthun.rxpaging.db.RepoDb
import com.agenthun.rxpaging.repository.GithubRepository
import com.agenthun.rxpaging.ui.ReposViewModel

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/6/1 00:39.
 */
object Injection {
    fun provideDb(context: Context): RepoDb {
        return RepoDb.getInstance(context)
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val db = provideDb(context)
        return ViewModelFactory(GithubRepository(db, GithubService.create()))
    }
}

class ViewModelFactory(private val repository: GithubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReposViewModel::class.java)) {
            return ReposViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}