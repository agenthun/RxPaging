package com.agenthun.rxpaging.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.agenthun.rxpaging.Injection
import com.agenthun.rxpaging.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ReposViewModel
    private lateinit var adapter: ReposAdapter
    private val disposable = CompositeDisposable()
    private var inSearch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[ReposViewModel::class.java]

        searchRepo.setOnEditorActionListener({ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        })
        searchRepo.setOnKeyListener({ _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        })

        swipeRefresh.setOnRefreshListener {
            Log.i(TAG, "refresh")
            searchRepo.text.trim().let {
                if (it.isNotEmpty() && inSearch) {
                    viewModel.refreshSearch()
                } else {
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        adapter = ReposAdapter({
            Log.i(TAG, "retry")
            viewModel.retry()
        })
        recyclerView.adapter = adapter
    }

    private fun updateRepoListFromInput() {
        searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                inSearch = true
                recyclerView.scrollToPosition(0)
                adapter.submitList(null)
                disposable.add(viewModel.showSearchResult(
                        it.toString(),
                        { networkState, forceRefresh ->
                            runOnUiThread {
                                if (forceRefresh) {
                                    swipeRefresh.isRefreshing = networkState.isRunning
                                } else {
                                    swipeRefresh.isRefreshing = false
                                    adapter.setNetworkState(networkState)
                                }
                                if (networkState.isFailed) {
                                    Toast.makeText(this, networkState.msg
                                            ?: getString(R.string.error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                        .subscribe(
                                {
                                    Log.d(TAG, "it=$it")
                                    adapter.submitList(it)
                                },
                                { error ->
                                    Log.e(TAG, "error: ${error.message}")
                                    Toast.makeText(this, error.message
                                            ?: getString(R.string.error), Toast.LENGTH_SHORT).show()
                                }
                        ))
            } else {
                inSearch = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
