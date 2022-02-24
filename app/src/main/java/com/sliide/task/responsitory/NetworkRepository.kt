package com.sliide.task.responsitory


import com.sliide.task.model.CreateUserResponse
import com.sliide.task.model.DeleteUserResponse
import com.sliide.task.model.User
import com.sliide.task.model.UserListResponse
import com.sliide.task.network.ApiService
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val apiService : ApiService
) {
    suspend fun getUserList(page : Int): UserListResponse {
        return  apiService.getUserList(page)
    }

    suspend fun deleteUser(id: Long): DeleteUserResponse {
        return  apiService.deleteUser(id)
    }

    suspend fun createUser(body: User): CreateUserResponse {
        return  apiService.createUser(body)
    }
}