package com.example.lokalassignment

import androidx.paging.PagingSource
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MainActivityUnitTest {

    @Mock
    lateinit var api: CatApi
    private lateinit var catPagingSource: CatPagingSource

    companion object {

        val catResponse = Cat(
            breeds = arrayListOf(
                Breed(
                    weight = Weight(imperial = "7 - 14", metric = "3 - 6"),
                    id = "hima",
                    name = "Himalayan",
                    cfaUrl = "http://cfa.org/Breeds/BreedsKthruR/NorwegianForestCat.aspx",
                    vetstreetUrl = "http://www.vetstreet.com/cats/himalayan",
                    vcahospitalsUrl = "https://vcahospitals.com/know-your-pet/cat-breeds/himalayan",
                    temperament = "Dependent, Gentle, Intelligent, Quiet, Social",
                    origin = "United States",
                    countryCodes = "US",
                    countryCode = "US",
                    description = "Calm and devoted, Himalayans make excellent companions, though they prefer a quieter home. They are playful in a sedate kind of way and enjoy having an assortment of toys. The Himalayan will stretch out next to you, sleep in your bed and even sit on your lap when she is in the mood.",
                    lifeSpan = "9 - 15",
                    indoor = 0,
                    lap = 1,
                    altNames = "Himalayan Persian, Colourpoint Persian, Longhaired Colourpoint, Himmy",
                    adaptability = 5,
                    affectionLevel = 5,
                    childFriendly = 2,
                    dogFriendly = 2,
                    energyLevel = 1,
                    grooming = 5,
                    healthIssues = 3,
                    intelligence = 3,
                    sheddingLevel = 4,
                    socialNeeds = 4,
                    strangerFriendly = 2,
                    vocalisation = 1,
                    experimental = 0,
                    hairless = 0,
                    natural = 0,
                    rare = 0,
                    rex = 0,
                    suppressedTail = 0,
                    shortLegs = 0,
                    wikipediaUrl = "https://en.wikipedia.org/wiki/Himalayan_(cat)",
                    hypoallergenic = 0,
                    referenceImageId = "CDhOtM-Ig",
                )
            ),
            id = "ZR9dCROV8",
            url = "https://cdn2.thecatapi.com/images/ZR9dCROV8.jpg",
            width = 774,
            height = 1024

        )
        val expectedCatsResponse =
            PagingSource.LoadResult.Page(
                data = listOf(catResponse),
                prevKey = null,
                nextKey = 2
            )

        val nextCatsResponse = PagingSource.LoadResult.Page(
            data = listOf(catResponse),
            prevKey = 1,
            nextKey = 3
        )

    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        catPagingSource = CatPagingSource(api)
    }

    @Test
    fun pagingSource_refresh_return_Success(): Unit = runTest {

        given(api.getCats(page = 1)).willReturn(arrayListOf(catResponse))

        assertEquals(
            expectedCatsResponse,
            catPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 1,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun pagingSource_append_return_Success() = runTest {

        given(api.getCats(page = 2)).willReturn(arrayListOf(catResponse))

        //then
        assertEquals(
            nextCatsResponse,
            catPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 2,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )

    }


    @Test
    fun pagingSource_load_failure_http_error(): Unit = runTest {
        val error = RuntimeException("404", Throwable())

        given(api.getCats(page = 1)).willThrow(error)

        val expectedResult = PagingSource.LoadResult.Error<Int, Cat>(error)

        assertEquals(
            expectedResult, catPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 1,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun pagingSource_load_failure_received_null(): Unit = runTest {

        given(api.getCats(page = 1)).willReturn(null)

        val expectedResult = PagingSource.LoadResult.Error<Int, Cat>(NullPointerException())

        assertEquals(
            expectedResult.toString(), catPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            ).toString()
        )
    }
}