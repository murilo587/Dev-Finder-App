package com.example.devfinder.core.data.repository

import com.example.devfinder.core.data.mapper.toDomain
import com.example.devfinder.core.data.model.SearchUserItem
import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.database.dao.FavoriteDao
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.network.GithubApiService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

class GithubRepositoryImplTest {
    private lateinit var repository: GithubRepository
    private lateinit var mockApiService: GithubApiService
    private lateinit var mockDao: FavoriteDao

    @BeforeEach
    fun setup() {
        mockApiService = mockk()
        mockDao = mockk(relaxed = true)
        repository = GithubRepositoryImpl(mockApiService, mockDao)
    }

    @Test
    fun `getUser with successful response returns user data`() = runTest {
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
        coEvery { mockApiService.getUser(any()) } returns Response.success(mockUserResponse)
        val result = repository.getUser("kotlin")

        result.isSuccess shouldBe true
        result.getOrNull() shouldBe mockUserResponse.toDomain()
    }
    @Test
    fun `getUser with error response returns failure`() = runTest {
        coEvery { mockApiService.getUser("invalid") } returns Response.error(404, "".toResponseBody())

        val result = repository.getUser("invalid")
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Error: 404"
    }
    @Test
    fun `getUser with null body returns failure with Empty body message`() = runTest {
        coEvery { mockApiService.getUser("empty") } returns Response.success(null)

        val result = repository.getUser("empty")
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Empty Body"
    }
    @Test
    fun `getUser with exception returns failure`() = runTest {
        val exception = Exception("Network timeout")
        coEvery { mockApiService.getUser("failure") } throws exception

        val result = repository.getUser("failure")
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Network timeout"
    }
    @Test
    fun `getUsers with successful response returns user list`() = runTest {
        val mockSearchUserItem = listOf(
            SearchUserItem(
                id = 1,
                login = "mockUser",
                avatarUrl = "mockAvatarUrl",
                htmlUrl = "mockHtmlUrl",
                bio = "mockBio"
            ),
            SearchUserItem(
                id = 2,
                login = "mockUser2",
                avatarUrl = "mockAvatarUrl2",
                htmlUrl = "mockHtmlUrl2",
                bio = "mockBio2"
            )
        )
        val mockUserListResponse = UserListResponse(
            totalCount = 2,
            incompleteResults = false,
            items = mockSearchUserItem
        )
        coEvery { mockApiService.getUsers(any()) } returns Response.success(mockUserListResponse)
        val result = repository.getUsers("kotlin")

        result.isSuccess shouldBe true
        result.getOrNull() shouldBe mockUserListResponse
    }
    @Test
    fun `getUsers with error response returns failure`() = runTest {
        coEvery { mockApiService.getUsers("invalid") } returns Response.error(404, "".toResponseBody())

        val result = repository.getUsers("invalid")
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Error: 404"
    }
    @Test
    fun `getUsers with null body returns failure with Empty body message`() = runTest {
        coEvery { mockApiService.getUsers("empty") } returns Response.success(null)

        val result = repository.getUsers("empty")
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Empty Body"
    }
    @Test
    fun `getUsers with exception returns failure`() = runTest {
        val exception = Exception("Network timeout")
        coEvery { mockApiService.getUsers("failure") } throws exception

        val result = repository.getUsers("failure")
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Network timeout"
    }

}