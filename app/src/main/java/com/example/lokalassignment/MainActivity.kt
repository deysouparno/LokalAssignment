package com.example.lokalassignment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lokalassignment.databinding.ActivityMainBinding
import com.example.lokalassignment.databinding.CatItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ClickListener {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var catsAdapter: CatsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        catsAdapter = CatsAdapter(this)

        binding.rvCats.apply {
            adapter = catsAdapter.withLoadStateFooter(LoaderAdapter())
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        binding.tvRetry.setOnClickListener {
            catsAdapter.refresh()
        }

        catsAdapter.addLoadStateListener { loadStates ->
            binding.pbMain.isVisible = loadStates.refresh is LoadState.Loading
            binding.groupError.isVisible =
                loadStates.refresh is LoadState.Error || (loadStates.refresh is LoadState.NotLoading && catsAdapter.itemCount == 0)
            if (loadStates.refresh is LoadState.Error) {
                binding.tvError.text =
                    (loadStates.refresh as LoadState.Error).error.localizedMessage
            } else if (loadStates.refresh is LoadState.NotLoading) {
                binding.tvError.text = "No Data"
            }
        }


        lifecycleScope.launch {

            viewModel.cats.collectLatest {
                catsAdapter.submitData(this@MainActivity.lifecycle, it)
            }

        }


    }

    override fun onClick(item: CatItemBinding) {
        item.expandedGroup.isVisible = !item.tvDesc.isVisible
    }

    override fun openWikiPedia(url: String) {
        val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(urlIntent)
    }

    fun refresh() {
        catsAdapter.refresh()
    }

    fun loadData(data: PagingData<Cat>) {
        catsAdapter.submitData(this.lifecycle, data)
    }

    fun getNetworkType(): String? {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return null
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return null
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(
                NetworkCapabilities.TRANSPORT_ETHERNET
            ) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Cellular"
            else -> null
        }
    }

}