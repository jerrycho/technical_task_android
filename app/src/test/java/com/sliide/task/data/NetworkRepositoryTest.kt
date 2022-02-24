package com.sliide.task.data


import com.sliide.task.base.BaseTest
import com.sliide.task.constants.*
import com.sliide.task.network.ApiService
import com.sliide.task.responsitory.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


@ExperimentalCoroutinesApi
class NetworkRepositoryTest : BaseTest() {

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
    fun `test with get user list`() {
        runTest {
            whenever(mockApiService.getUserList(1)).thenReturn(getUserListResponse())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockResponse = mNetworkRepository.getUserList(1)
            assertEquals(mockResponse.code, HTTP_STATUS_OK)
        }
    }

    @Test
    fun `test with create user success`() {
        runTest {
            whenever(mockApiService.createUser(
                getMockNewUser()
            )).thenReturn(getCreateUserResponseSuccess())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockResponse = mNetworkRepository.createUser(getMockNewUser())
            assertEquals(mockResponse.code, CREATE_USER_OK)
        }
    }

    @Test
    fun `test with create user fail`() {
        runTest {
            whenever(mockApiService.createUser(
                getMockNewUser()
            )).thenReturn(getCreateUserResponseFailed())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockResponse = mNetworkRepository.createUser(getMockNewUser())
            assertEquals(mockResponse.code, CREATE_USER_FAIL)
        }
    }

    @Test
    fun `test with delete user success`() {
        runTest {
            whenever(mockApiService.deleteUser(
                1
            )).thenReturn(getDeleteUserResponseSuccess())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockResponse = mNetworkRepository.deleteUser(1)
            assertEquals(mockResponse.code, DELETE_USER_OK)
        }
    }

    @Test
    fun `test with delete user failed`() {
        runTest {
            whenever(mockApiService.deleteUser(
                2
            )).thenReturn(getDeleteUserResponseFail())
            val mNetworkRepository = NetworkRepository(mockApiService)
            val mockResponse = mNetworkRepository.deleteUser(2)
            assertEquals(mockResponse.code, DELETW_USER_FAIL)
        }
    }


}
