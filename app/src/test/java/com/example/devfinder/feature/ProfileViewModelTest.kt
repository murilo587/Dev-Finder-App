package com.example.devfinder.feature

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.devfinder.core.data.mapper.toDomain
import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.domain.model.User
import com.example.devfinder.feature.profile.ProfileIntent
import com.example.devfinder.feature.profile.ProfileUiState
import com.example.devfinder.feature.profile.ProfileViewModel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProfileViewModelTest {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var mockRepository: GithubRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        savedStateHandle = SavedStateHandle(
            mapOf(
                "username" to "kotlin",
                "isSaved" to false,
                "userId" to 1
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load user from remote when not saved locally`() = runTest {
        val mockUserResponse = UserResponse(
            id = 1,
            login = "kotlin",
            avatarUrl = "mockAvatarUrl",
            htmlUrl = "mockHtmlUrl",
            name = "kotlin",
            bio = "mockBio",
            followers = 1,
            following = 1,
            publicRepos = 1
        ).toDomain()
        coEvery { mockRepository.checkIsFavoriteDirect(1) } returns false
        coEvery { mockRepository.getFavoriteUserByName("kotlin") } returns null
        coEvery { mockRepository.getUser("kotlin") } returns Result.success(mockUserResponse)
        coEvery { mockRepository.isFavorite(1) } returns flowOf(false)

        viewModel = ProfileViewModel(mockRepository,testDispatcher, savedStateHandle)


        viewModel.uiState.test {
            awaitItem() shouldBe ProfileUiState.Idle
            awaitItem() shouldBe ProfileUiState.Loading
            awaitItem() shouldBe ProfileUiState.Success(mockUserResponse)
        }

        coVerify(exactly = 1) {
            mockRepository.getUser("kotlin")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should load user from database when saved locally`() = runTest {
        val mockUser = User(
            id = 1,
            login = "kotlin",
            avatarUrl = "mockAvatarUrl",
            htmlUrl = "mockHtmlUrl",
            bio = "mockBio",
        )
        coEvery { mockRepository.checkIsFavoriteDirect(1) } returns true
        coEvery { mockRepository.getFavoriteUserByName("kotlin") } returns mockUser
        coEvery { mockRepository.isFavorite(1) } returns flowOf(true)

        viewModel = ProfileViewModel(mockRepository,testDispatcher, savedStateHandle)

        viewModel.uiState.test {
            awaitItem() shouldBe ProfileUiState.Idle
            awaitItem() shouldBe ProfileUiState.Loading
            awaitItem() shouldBe ProfileUiState.Success(mockUser)
        }

        coVerify(exactly = 1) {
            mockRepository.getFavoriteUserByName("kotlin")
        }
    }

    @Test
    fun `should emit error when remote fetch fail`() = runTest {
        val exception = Exception("Unknown Error")
        coEvery { mockRepository.checkIsFavoriteDirect(1) } returns false
        coEvery { mockRepository.getFavoriteUserByName("kotlin") } returns null
        coEvery { mockRepository.getUser("kotlin") } returns Result.failure(exception)

        viewModel = ProfileViewModel(mockRepository,testDispatcher, savedStateHandle)

        viewModel.uiState.test {
            awaitItem() shouldBe ProfileUiState.Idle
            awaitItem() shouldBe ProfileUiState.Loading
            awaitItem() shouldBe ProfileUiState.Error("Unknown Error")
        }

        coVerify(exactly = 1) {
            mockRepository.getUser("kotlin")
        }
    }

    @Test
    fun `should observe if the user is saved`() = runTest {
        val mockUser = User(
            id = 1,
            login = "kotlin",
            avatarUrl = "mockAvatarUrl",
            htmlUrl = "mockHtmlUrl",
            bio = "mockBio",
        )
        coEvery { mockRepository.checkIsFavoriteDirect(1) } returns true
        coEvery { mockRepository.getFavoriteUserByName("kotlin") } returns mockUser
        coEvery { mockRepository.isFavorite(1) } returns flowOf(true)

        viewModel = ProfileViewModel(mockRepository,testDispatcher, savedStateHandle)

        viewModel.isFavorite.test {
            awaitItem() shouldBe false
            awaitItem() shouldBe true
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `toggleFavorite should save favorited user data in local database`() = runTest {
        val mockUserResponse = UserResponse(
            id = 1,
            login = "kotlin",
            avatarUrl = "mockAvatarUrl",
            htmlUrl = "mockHtmlUrl",
            name = "kotlin",
            bio = "mockBio",
            followers = 1,
            following = 1,
            publicRepos = 1
        ).toDomain()
        coEvery { mockRepository.checkIsFavoriteDirect(1) } returns false
        coEvery { mockRepository.getFavoriteUserByName("kotlin") } returns null
        coEvery { mockRepository.getUser("kotlin") } returns Result.success(mockUserResponse)
        coEvery { mockRepository.isFavorite(1) } returns flowOf(false)
        coEvery { mockRepository.saveFavorite(any()) } returns Unit
        coEvery { mockRepository.getRepos(any()) } returns Result.success(emptyList())
        coEvery { mockRepository.getStarredRepos(any()) } returns Result.success(emptyList())
        coEvery { mockRepository.saveRepositories(any(), any()) } returns Unit
        coEvery { mockRepository.saveStarredRepositories(any(), any()) } returns Unit


        viewModel = ProfileViewModel(mockRepository,testDispatcher, savedStateHandle)

        advanceUntilIdle()

        viewModel.handleIntent(ProfileIntent.ToggleFavorite(mockUserResponse))

        advanceUntilIdle()

        coVerify(exactly = 1) {
            mockRepository.saveFavorite(mockUserResponse)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `toggleFavorite should remove favorited user data in local database`() = runTest {
        val mockUser = User(
            id = 1,
            login = "kotlin",
            avatarUrl = "mockAvatarUrl",
            htmlUrl = "mockHtmlUrl",
            bio = "mockBio",
        )
        coEvery { mockRepository.checkIsFavoriteDirect(1) } returns true
        coEvery { mockRepository.getFavoriteUserByName("kotlin") } returns mockUser
        coEvery { mockRepository.isFavorite(1) } returns flowOf(true)
        coEvery { mockRepository.removeFavorite(any()) } returns Unit
        coEvery { mockRepository.getRepos(any()) } returns Result.success(emptyList())
        coEvery { mockRepository.removeRepositories(any()) } returns Unit
        coEvery { mockRepository.removeStarredRepositories(any()) } returns Unit

        viewModel = ProfileViewModel(mockRepository,testDispatcher, savedStateHandle)
        advanceUntilIdle()
        viewModel.handleIntent(ProfileIntent.ToggleFavorite(mockUser))
        advanceUntilIdle()
        coVerify(exactly = 1) {
            mockRepository.removeFavorite(mockUser)
            mockRepository.removeRepositories(1)
            mockRepository.removeStarredRepositories(1)
        }
    }
}