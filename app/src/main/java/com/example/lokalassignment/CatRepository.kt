package com.example.lokalassignment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatRepository @Inject constructor(private val catApi: CatApi) {
    fun getCats() =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 80,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CatPagingSource(catApi)
            }
        ).flow
}