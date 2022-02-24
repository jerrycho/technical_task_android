package com.sliide.task.viewdata

import app.cash.turbine.test
import com.sliide.task.base.BaseTest
import com.sliide.task.base.ViewState
import com.sliide.task.model.*
import com.sliide.task.network.ApiService
import com.sliide.task.responsitory.NetworkRepository
import com.sliide.task.ui.user.UserListViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class UserListViewDataTest : BaseTest() {

    private val dispatcher = UnconfinedTestDispatcher()
    private val mockApiService: ApiService = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test with get user list page ok`() = runBlockingTest {
        launch {
            whenever(mockApiService.getUserList(1)).thenReturn(getUserListResponse())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockViewModel = UserListViewModel(mNetworkRepository)
            mockViewModel._getUserListViewState.test {
                Assert.assertEquals(ViewState.Loading, awaitItem())

                Assert.assertEquals(ViewState.Clean, awaitItem())
                Assert.assertEquals(
                    ViewState.Success<UserListResponse>(getUserListResponse()),
                    awaitItem()
                )
                awaitComplete()
            }
        }
    }

    @Test
    fun `test with create user ok`() = runBlockingTest {
        launch {
            whenever(mockApiService.getUserList(1)).thenReturn(getUserListResponse())
            whenever(mockApiService.createUser(getMockNewUser())).thenReturn(getCreateUserResponseSuccess())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockViewModel = UserListViewModel(mNetworkRepository)

            mockViewModel._createUserViewState.test {
                Assert.assertEquals(ViewState.Normal, awaitItem()) //default
                Assert.assertEquals(ViewState.Loading, awaitItem())
                delay(1000)
                Assert.assertEquals(
                    ViewState.Success<CreateUserResponse>(getCreateUserResponseSuccess()),
                    awaitItem()
                )
                awaitComplete()
            }

            mockViewModel.createUser(
                name = "this is name",
                email = "email3@test.com")
        }
    }

    @Test
    fun `test with create user failed`() = runBlockingTest {
        launch {
            whenever(mockApiService.getUserList(1)).thenReturn(getUserListResponse())
            whenever(mockApiService.createUser(getMockNewUser())).thenReturn(getCreateUserResponseFailed())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockViewModel = UserListViewModel(mNetworkRepository)

            mockViewModel._createUserViewState.test {
                Assert.assertEquals(ViewState.Normal, awaitItem()) //default
                Assert.assertEquals(ViewState.Loading, awaitItem())
                delay(1000)
                Assert.assertEquals(
                    ViewState.Failure("email : wrong email address"),
                    awaitItem()
                )
                awaitComplete()
            }

            mockViewModel.createUser(
                name = "this is name",
                email = "email3@test.com")
        }
    }

    @Test
    fun `test with delete user ok`() = runBlockingTest {
        launch {
            whenever(mockApiService.getUserList(1)).thenReturn(getUserListResponse())
            whenever(mockApiService.deleteUser(1)).thenReturn(getDeleteUserResponseSuccess())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockViewModel = UserListViewModel(mNetworkRepository)

            mockViewModel._deleteUserViewState.test {
                Assert.assertEquals(ViewState.Normal, awaitItem()) //default
                Assert.assertEquals(ViewState.Loading, awaitItem())
                delay(500)
                Assert.assertEquals(
                    ViewState.Success<DeleteUserResponse>(getDeleteUserResponseSuccess()),
                    awaitItem()
                )
                awaitComplete()
            }

            mockViewModel.deleteUser(User(
                id = 1,
                name="name",
                email="email@eee.om",
            ))
        }
    }

    @Test
    fun `test with delete user failed`() = runBlockingTest {
        launch {
            whenever(mockApiService.getUserList(1)).thenReturn(getUserListResponse())
            whenever(mockApiService.deleteUser(1)).thenReturn(getDeleteUserResponseFail())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockViewModel = UserListViewModel(mNetworkRepository)

            mockViewModel._deleteUserViewState.test {
                Assert.assertEquals(ViewState.Normal, awaitItem()) //default
                Assert.assertEquals(ViewState.Loading, awaitItem())
                delay(500)
                Assert.assertEquals(
                    ViewState.Failure("Resource not found"),
                    awaitItem()
                )
                awaitComplete()
            }

            mockViewModel.deleteUser(User(
                id = 1,
                name="name",
                email="email@eee.om",
            ))
        }
    }
}