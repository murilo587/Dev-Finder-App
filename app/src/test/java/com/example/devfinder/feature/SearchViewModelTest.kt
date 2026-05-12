package com.example.devfinder.feature

import app.cash.turbine.test
import com.example.devfinder.core.data.model.SearchUserItem
import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.data.repository.GithubRepositoryImpl
import com.example.devfinder.feature.search.SearchIntent
import com.example.devfinder.feature.search.SearchUiState
import com.example.devfinder.feature.search.SearchViewModel
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private lateinit var mockRepository: GithubRepositoryImpl

    @BeforeEach
    fun setup() {
        mockRepository = mockk()
        viewModel = SearchViewModel(mockRepository)
    }
    @Test
    fun `initial state should be Idle`() {
        viewModel.uiState.value shouldBe SearchUiState.Idle
    }
    @Test
    fun `when OnQueryChanged intent is sent, query value updates`() {
        val newQuery = "kotlin"
        viewModel.handleIntent(SearchIntent.OnQueryChanged(newQuery))

        viewModel.query.value shouldBe newQuery
    }
    @Test
    fun `when query is empty, state should be Idle`() {
        viewModel.handleIntent(SearchIntent.OnQueryChanged("kotlin"))
        viewModel.handleIntent(SearchIntent.OnQueryChanged(""))

        viewModel.uiState.value shouldBe SearchUiState.Idle
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `successful search should emit Idle, Loading, then Success state`() = runTest {
        val mockUsers = listOf(
            SearchUserItem(id = 1, login = "user1", htmlUrl = "https://github.com/user1", avatarUrl = "picture.png"),
            SearchUserItem(id = 2, login = "user2", htmlUrl = "https://github.com/user2", avatarUrl = "picture.png")
        )
        val mockResponse = UserListResponse(
            totalCount = 2,
            incompleteResults = false,
            items = mockUsers
        )

        coEvery { mockRepository.getUsers("kotlin") } returns Result.success(mockResponse)

        viewModel.uiState.test {
            awaitItem() shouldBe SearchUiState.Idle
            viewModel.handleIntent(SearchIntent.OnQueryChanged("kotlin"))
            advanceTimeBy(800)
            advanceUntilIdle()
            awaitItem() shouldBe SearchUiState.Loading
            val successState = awaitItem()
            successState.shouldBeInstanceOf<SearchUiState.Success>()
            successState.users shouldBe mockResponse
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { mockRepository.getUsers("kotlin") }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `failed search should emit Idle, Loading, Error state`() = runTest {
        val errorMessage = "Network connection failed"
        coEvery { mockRepository.getUsers("kotlin") } returns Result.failure(Exception(errorMessage))

        viewModel.uiState.test {
            awaitItem() shouldBe SearchUiState.Idle
            viewModel.handleIntent(SearchIntent.OnQueryChanged("kotlin"))
            advanceTimeBy(800)
            advanceUntilIdle()
            awaitItem() shouldBe SearchUiState.Loading
            val errorState = awaitItem()
            errorState.shouldBeInstanceOf<SearchUiState.Error>()
            errorState.message.contains(errorMessage) shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `query with less than 3 characters should not trigger search`() = runTest {
        coEvery { mockRepository.getUsers(any()) } returns Result.success(UserListResponse(totalCount = 0, incompleteResults = false, items = emptyList()))

        viewModel.uiState.test {
            awaitItem() shouldBe SearchUiState.Idle
            viewModel.handleIntent(SearchIntent.OnQueryChanged("ko"))
            advanceTimeBy(800)
            advanceUntilIdle()

            coVerify(exactly = 0) { mockRepository.getUsers(any()) }
            viewModel.uiState.value shouldBe SearchUiState.Idle
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `debounce should delay search by 800ms`() = runTest {
        // Arrange
        coEvery { mockRepository.getUsers(any()) } returns
                Result.success(UserListResponse(0, false, emptyList()))

        viewModel.uiState.test {
            awaitItem() shouldBe SearchUiState.Idle

            viewModel.handleIntent(SearchIntent.OnQueryChanged("kotlin"))

            advanceTimeBy(799)
            expectNoEvents()

            advanceTimeBy(1)
            advanceUntilIdle()

            awaitItem() shouldBe SearchUiState.Loading
            awaitItem().shouldBeInstanceOf<SearchUiState.Success>()

            cancelAndIgnoreRemainingEvents()
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `search should not throw exceptions`() = runTest {
        coEvery { mockRepository.getUsers(any()) } returns
                Result.success(UserListResponse(0, false, emptyList()))

        shouldNotThrow<Exception> {
            viewModel.handleIntent(SearchIntent.OnQueryChanged("kotlin"))
            advanceTimeBy(800)
            advanceUntilIdle()
        }
    }
}