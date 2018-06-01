package com.agenthun.rxpaging.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.agenthun.rxpaging.Injection
import com.agenthun.rxpaging.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ReposViewModel
    private lateinit var adapter: ReposAdapter
    private val disposable = CompositeDisposable()

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

        adapter = ReposAdapter({
            Log.i(TAG, "retry")
        })
        recyclerView.adapter = adapter
    }

    private fun updateRepoListFromInput() {
        searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
                adapter.submitList(null)
                disposable.add(viewModel.showSearchResult(it.toString(), { runOnUiThread { adapter.setNetworkState(it) } }).subscribe(
                        {
                            Log.d(TAG, "it=$it")
                            adapter.submitList(it)
                        },
                        { error ->
                            Log.e(TAG, "error: ${error.message}")
                        }
                ))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
