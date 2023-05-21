package com.example.lokalassignment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.lokalassignment.databinding.CatItemBinding
import com.example.lokalassignment.databinding.LoaderItemBinding

class CatsAdapter(private val listener: ClickListener) :
    PagingDataAdapter<Cat, CatViewHolder>(diffCallback = diffUtilCallback) {
    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val binding = CatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatViewHolder(binding, listener)
    }
}

val diffUtilCallback = object : DiffUtil.ItemCallback<Cat>() {
    override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
        return newItem.id == oldItem.id
    }

    override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean {
        return newItem == oldItem
    }

}

class CatViewHolder(private val binding: CatItemBinding, private val listener: ClickListener) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(cat: Cat) {
        binding.apply {
            Glide.with(ivCat.context)
                .load(cat.url)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.cat)
                .into(ivCat)

            expandedGroup.isVisible = cat.descVisible

            tvBreedName.text = cat.breeds.first().name

            tvDesc.text = cat.breeds.first().description

            root.setOnClickListener {
                cat.descVisible = !cat.descVisible
                listener.onClick(binding)
            }

            ivWiki.setOnClickListener {
                listener.openWikiPedia(cat.breeds.first().wikipediaUrl)
            }

        }
    }
}


class LoaderAdapter : LoadStateAdapter<LoaderViewHolder>() {
    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        return LoaderViewHolder(
            LoaderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

}

class LoaderViewHolder(private val binding: LoaderItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        binding.pbLoader.isVisible = loadState is LoadState.Loading
    }
}


interface ClickListener {
    fun onClick(item: CatItemBinding)
    fun openWikiPedia(url: String)
}