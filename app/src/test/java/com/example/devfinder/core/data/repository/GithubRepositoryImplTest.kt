package com.example.devfinder.core.data.repository

import com.example.devfinder.core.data.mapper.toDomain
import com.example.devfinder.core.data.mapper.toEntity
import com.example.devfinder.core.data.mapper.toUserRepositoryEntity
import com.example.devfinder.core.data.mapper.toUserStarredEntity
import com.example.devfinder.core.data.model.RepoResponse
import com.example.devfinder.core.data.model.SearchUserItem
import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.database.dao.FavoriteDao
import com.example.devfinder.core.database.model.FavoriteUserEntity
import com.example.devfinder.core.database.model.UserRepositoryEntity
import com.example.devfinder.core.database.model.UserStarredEntity
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.domain.model.Repo
import com.example.devfinder.core.domain.model.User
import com.example.devfinder.core.network.GithubApiService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
        coEvery { mockApiService.getUser("invalid") } returns Response.error(
            404,
            "".toResponseBody()
        )

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
        coEvery { mockApiService.getUsers("invalid") } returns Response.error(
            404,
            "".toResponseBody()
        )

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

    @Test
    fun `getFavorites should return mapped list of users`() = runTest {
        val entities = listOf(
            FavoriteUserEntity(
                id = 1,
                login = "user1",
                avatarUrl = "avatar",
                htmlUrl = "url",
                bio = "bio"
            )
        )
        every { mockDao.getFavorites() } returns flowOf(entities)

        val result = repository.getFavorites().first()
        result shouldBe entities.map { it.toDomain() }
    }

    @Test
    fun `getFavorites should return empty list when there is no user favorited`() = runTest {
        every { mockDao.getFavorites() } returns flowOf(emptyList())

        val result = repository.getFavorites().first()
        result shouldBe emptyList()
    }

    @Test
    fun `getFavoriteUserByName should return User`() = runTest {
        val mockUser = FavoriteUserEntity(
            id = 1,
            login = "user1",
            avatarUrl = "avatar",
            htmlUrl = "url",
            bio = "bio"
        )
        every { mockDao.getFavoriteUserByName("kotlin") } returns mockUser

        val result = repository.getFavoriteUserByName("kotlin")

        result shouldBe mockUser.toDomain()
    }

    @Test
    fun `getFavoriteUserByName should return null when there is no user found`() = runTest {
        every { mockDao.getFavoriteUserByName("kotlin") } returns null

        val result = repository.getFavoriteUserByName("kotlin")

        result shouldBe null
    }

    @Test
    fun `getLocalRepositories should return mapped list of repositories`() = runTest {
        val entity = listOf(
            UserRepositoryEntity(
                name = "mockRepo",
                description = "mockDescription",
                userid = 1,
                repoId = 1,
                stargazersCount = 0,
                updatedAt = "mockDate",
                createdAt = "mockDate",
                language = "mockLanguage"
            )
        )
        every { mockDao.getLocalRepositories(any()) } returns flowOf(entity)

        val result = repository.getLocalRepositories(1).first()

        result shouldBe entity.map { it.toDomain() }
    }

    @Test
    fun `getLocalRepositories should return empty list when there is no local repositories`() = runTest {
        every { mockDao.getLocalRepositories(any()) } returns flowOf(emptyList())

        val result = repository.getLocalRepositories(1).first()

        result shouldBe emptyList()
    }

    @Test
    fun `getLocalStarredRepositories should return mapped list of repositories`() = runTest {
        val entity = listOf(
            UserStarredEntity(
                name = "mockRepo",
                description = "mockDescription",
                userid = 1,
                repoId = 1,
                stargazersCount = 0,
                updatedAt = "mockDate",
                createdAt = "mockDate",
                language = "mockLanguage"
            )
        )
        every { mockDao.getLocalStarredRepositories(any()) } returns flowOf(entity)

        val result = repository.getLocalStarredRepositories(1).first()

        result shouldBe entity.map { it.toDomain() }
    }

    @Test
    fun `getLocalStarredRepositories should return empty list when there is no local repositories`() = runTest {
        every { mockDao.getLocalStarredRepositories(any()) } returns flowOf(emptyList())

        val result = repository.getLocalStarredRepositories(1).first()

        result shouldBe emptyList()
    }

    @Test
    fun `saveFavorite should call dao when invoked`() = runTest {
        val mockUser = User(
            id = 1,
            login = "user1",
            avatarUrl = "avatar",
            htmlUrl = "url",
            bio = "bio"
        )
        repository.saveFavorite(mockUser)

        coVerify(exactly = 1) {
            mockDao.insertFavorite(mockUser.toEntity())
        }
    }


    @Test
    fun `saveRepositories should call dao when invoked`() = runTest {
        val mockRepository = listOf(
            Repo(
                id = 1,
                name = "repo1",
                description = "description",
                language = "kotlin",
                createdAt = "created",
                updatedAt = "updated",
                stargazersCount = 1
        )
        )
        repository.saveRepositories(mockRepository, 1)

        coVerify(exactly = 1) {
            mockDao.insertRepositories(mockRepository.toUserRepositoryEntity(1))
        }
    }

    @Test
    fun `saveStarredRepositories should call dao when invoked`() = runTest {
        val mockStarredRepository = listOf(
            Repo(
                id = 1,
                name = "repo1",
                description = "description",
                language = "kotlin",
                createdAt = "created",
                updatedAt = "updated",
                stargazersCount = 1
            )
        )
        repository.saveStarredRepositories(mockStarredRepository, 1)

        coVerify(exactly = 1) {
            mockDao.insertStarredRepositories(mockStarredRepository.toUserStarredEntity(1))
        }
    }

    @Test
    fun `removeFavorite should call dao when invoked`() = runTest {
        val mockUser = User(
            id = 1,
            login = "user1",
            avatarUrl = "avatar",
            htmlUrl = "url",
            bio = "bio"
        )
        repository.removeFavorite(mockUser)

        coVerify(exactly = 1) {
            mockDao.deleteFavorite(mockUser.toEntity())
        }
    }

    @Test
    fun `removeRepositories should call dao when invoked`() = runTest {
        repository.removeRepositories(1)

        coVerify(exactly = 1) {
            mockDao.clearRepositories(1)
        }
    }

    @Test
    fun `removeStarredRepositories should call dao when invoked`() = runTest {
        repository.removeStarredRepositories(1)

        coVerify(exactly = 1) {
            mockDao.clearStarredRepositories(1)
        }
    }

    @Test
    fun `IsFavorite should return if the userId is saved`() = runTest {
        coEvery { mockDao.isFavorite(any()) } returns flowOf(true)

        val result = repository.isFavorite(1).first()

        result shouldBe true
    }

    @Test
    fun `updateFavorites should call dao when invoked`() = runTest {
        val mockUsers = listOf(
            User(
                id = 1,
                login = "user1",
                avatarUrl = "avatar",
                htmlUrl = "url",
                bio = "bio"
            )
        )
        repository.updateFavorites(mockUsers)

        coVerify(exactly = 1) { mockDao.updateFavorites(mockUsers.toEntity()) }
    }

    @Test
    fun `updateRepositories should call dao when invoked`() = runTest {
        val mockRepositories = listOf(
            Repo(
                id = 1,
                name = "repo1",
                description = "description",
                language = "kotlin",
                createdAt = "created",
                updatedAt = "updated",
                stargazersCount = 1
            )
        )
        repository.updateRepositories(mockRepositories, 1)

        coVerify(exactly = 1) { mockDao.updateLocalRepositories(mockRepositories.toUserRepositoryEntity(1)) }
    }

    @Test
    fun `updateStarredRepositories should call dao when invoked`() = runTest {
        val mockStarredRepositories = listOf(
            Repo(
                id = 1,
                name = "repo1",
                description = "description",
                language = "kotlin",
                createdAt = "created",
                updatedAt = "updated",
                stargazersCount = 1
            )
        )
        repository.updateStarredRepositories(mockStarredRepositories, 1)

        coVerify(exactly = 1) { mockDao.updateLocalStarredRepositories(mockStarredRepositories.toUserStarredEntity(1)) }
    }

    @Test
    fun `checkIsFavoriteDirect should return if the userId is saved`() = runTest {
        coEvery { mockDao.checkIsFavoriteDirect(any())} returns true

        val result = repository.checkIsFavoriteDirect(1)
        result shouldBe true
    }

    @Test
    fun `getRepos with successful response should return repositories list`() = runTest {
        val mockRepos = listOf(
            RepoResponse(
                id = 1,
                name = "mockName",
                description = "mockDescription",
                language = "Kotlin",
                createdAt = "created",
                updatedAt = "updated",
                stargazersCount = 1
            )
        )
        coEvery { mockApiService.getRepos(any()) } returns Response.success(mockRepos)

        val result = repository.getRepos("kotlin")
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe mockRepos.toDomain()
    }

    @Test
    fun `getRepos with error response should return failure`() = runTest {
        coEvery { mockApiService.getRepos(any()) } returns Response.error(404, "".toResponseBody())

        val result = repository.getRepos("kotlin")

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Error: 404"
    }

    @Test
    fun `getRepos with empty body response should return empty list`() = runTest {
        coEvery { mockApiService.getRepos("empty") } returns Response.success(null)

        val result = repository.getRepos("empty")

        result.isSuccess shouldBe true
        result.getOrNull() shouldBe emptyList()
    }

    @Test
    fun `getRepos with exceptions return failure`() = runTest {
        coEvery { mockApiService.getRepos("exception") } throws Exception("Unknown Error")

        val result = repository.getRepos("exception")

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Unknown Error"
    }

    @Test
    fun `getStarredRepos with successful response should return starred repositories list`() = runTest {
        val mockStarredRepos = listOf(
            RepoResponse(
                id = 1,
                name = "mockName",
                description = "mockDescription",
                language = "Kotlin",
                createdAt = "created",
                updatedAt = "updated",
                stargazersCount = 1
            )
        )
        coEvery { mockApiService.getStarredRepos(any()) } returns Response.success(mockStarredRepos)

        val result = repository.getStarredRepos("kotlin")
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe mockStarredRepos.toDomain()
    }

    @Test
    fun `getStarredRepos with error response should return failure`() = runTest {
        coEvery { mockApiService.getStarredRepos(any()) } returns Response.error(404, "".toResponseBody())

        val result = repository.getStarredRepos("kotlin")

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Error: 404"
    }

    @Test
    fun `getStarredRepos with empty body response should return empty list`() = runTest {
        coEvery { mockApiService.getStarredRepos("empty") } returns Response.success(null)

        val result = repository.getStarredRepos("empty")

        result.isSuccess shouldBe true
        result.getOrNull() shouldBe emptyList()
    }

    @Test
    fun `getStarredRepos with exceptions return failure`() = runTest {
        coEvery { mockApiService.getStarredRepos("exception") } throws Exception("Unknown Error")

        val result = repository.getStarredRepos("exception")

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Unknown Error"
    }
}