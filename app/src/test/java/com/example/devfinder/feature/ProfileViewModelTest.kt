package com.example.devfinder.feature

import app.cash.turbine.test
import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.data.repository.GithubRepositoryImpl
import com.example.devfinder.core.domain.GithubRepository
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProfileViewModelTest {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var mockRepository: GithubRepository
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        viewModel = ProfileViewModel(mockRepository)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Idle`() {
        viewModel.uiState.value shouldBe ProfileUiState.Idle
    }

    @Test
    fun `Success should emit Idle, Loading, Success`() = runTest {
        val mockUserResponse = UserResponse(
            id = 1,
            login = "mockUser",
            avatarUrl = "mockAvatarUrl",
            htmlUrl = "mockHtmlUrl",
            name = "mockUser",
            bio = "mockBio",
            followers = 1,
            following = 1,
            publicRepos = 1
        )
        coEvery { mockRepository.getUser("kotlin")} returns Result.success(mockUserResponse)

        viewModel.uiState.test {
            awaitItem() shouldBe ProfileUiState.Idle
            viewModel.handleIntent(ProfileIntent.LoadUser("kotlin"))
            awaitItem() shouldBe ProfileUiState.Loading
            awaitItem().shouldBeInstanceOf<ProfileUiState.Success>()

            coVerify(exactly = 1) { mockRepository.getUser("kotlin") }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Error should emit Idle, Loading, Error`() = runTest {
        coEvery { mockRepository.getUser("kotlin")} returns Result.failure(Exception("Error"))

        viewModel.uiState.test {
            awaitItem() shouldBe ProfileUiState.Idle
            viewModel.handleIntent(ProfileIntent.LoadUser("kotlin"))
            awaitItem() shouldBe ProfileUiState.Loading
            awaitItem().shouldBeInstanceOf<ProfileUiState.Error>()

            coVerify(exactly = 1) { mockRepository.getUser("kotlin") }
            cancelAndIgnoreRemainingEvents()
        }
    }

}