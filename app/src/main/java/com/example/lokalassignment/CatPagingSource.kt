package com.example.lokalassignment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.lang.NullPointerException

class CatPagingSource(
    private val catApi: CatApi,
) : PagingSource<Int, Cat>() {


    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {

        return try {
            val position = params.key ?: 1
            val cats = catApi.getCats(
                page = position
            )


            if (cats != null)
                LoadResult.Page(
                    data = cats,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = position + 1
                )
            else
                LoadResult.Error(NullPointerException())

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}