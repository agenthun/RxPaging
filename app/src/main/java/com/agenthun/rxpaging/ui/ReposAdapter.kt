package com.agenthun.rxpaging.ui

import android.arch.paging.PagedListAdapter
import android.content.Intent
import android.net.Uri
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.agenthun.rxpaging.R
import com.agenthun.rxpaging.vo.NetworkState
import com.agenthun.rxpaging.vo.Repo
import com.agenthun.rxpaging.vo.Status

/**
 * @project RxPaging
 * @authors agenthun
 * @date    2018/6/1 00:12.
 */
class ReposAdapter(private val retryCallback: () -> Unit)
    : PagedListAdapter<Repo, RecyclerView.ViewHolder>(POST_COMPARATOR) {
    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.listitem_repo -> RepoViewHolder.create(parent)
            R.layout.listitem_network_state -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.listitem_repo -> (holder as RepoViewHolder).bind(getItem(position)!!)
            R.layout.listitem_network_state -> (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.listitem_repo
        } else {
            R.layout.listitem_network_state
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo?, newItem: Repo?): Boolean {
                return oldItem?.id == newItem?.id
            }

            override fun areContentsTheSame(oldItem: Repo?, newItem: Repo?): Boolean {
                return oldItem == newItem
            }

        }
    }

    class RepoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.repo_name)
        private val description: TextView = view.findViewById(R.id.repo_description)
        private val stars: TextView = view.findViewById(R.id.repo_stars)
        private val language: TextView = view.findViewById(R.id.repo_language)
        private val forks: TextView = view.findViewById(R.id.repo_forks)

        private var repo: Repo? = null

        init {
            view.setOnClickListener {
                repo?.url?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                }
            }
        }

        fun bind(repo: Repo) {
            this.repo = repo
            name.text = repo.fullName

            var descriptionVisibility = View.GONE
            if (repo.description != null) {
                description.text = repo.description
                descriptionVisibility = View.VISIBLE
            }
            description.visibility = descriptionVisibility

            stars.text = repo.stars.toString()
            forks.text = repo.forks.toString()

            var languageVisibility = View.GONE
            if (!repo.language.isNullOrEmpty()) {
                val resources = this.itemView.context.resources
                language.text = resources.getString(R.string.language, repo.language)
                languageVisibility = View.VISIBLE
            }
            language.visibility = languageVisibility
        }

        companion object {
            fun create(parent: ViewGroup): RepoViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.listitem_repo, parent, false)
                return RepoViewHolder(view)
            }
        }
    }

    class NetworkStateItemViewHolder(view: View,
                                     private val retryCallback: () -> Unit) : RecyclerView.ViewHolder(view) {
        private val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        private val retry = view.findViewById<Button>(R.id.retryButton)
        private val errorMsg = view.findViewById<TextView>(R.id.errorMsg)

        init {
            retry.setOnClickListener {
                retryCallback()
            }
        }

        fun bind(networkState: NetworkState?) {
            progressBar.visibility = toVisbility(networkState?.status == Status.RUNNING)
            retry.visibility = toVisbility(networkState?.status == Status.FAILED)
            errorMsg.visibility = toVisbility(networkState?.msg != null)
            errorMsg.text = networkState?.msg
        }

        companion object {
            fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.listitem_network_state, parent, false)
                return NetworkStateItemViewHolder(view, retryCallback)
            }

            fun toVisbility(constraint: Boolean): Int {
                return if (constraint) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }
}