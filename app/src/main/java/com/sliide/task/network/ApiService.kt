package com.sliide.task.network


import com.sliide.task.model.CreateUserResponse
import com.sliide.task.model.DeleteUserResponse
import com.sliide.task.model.User
import com.sliide.task.model.UserListResponse
import retrofit2.http.*


interface ApiService {

    @GET("/public-api/users")
    suspend fun getUserList(@Query("page") pageId: Int): UserListResponse

    @DELETE("/public-api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): DeleteUserResponse

    @POST("/public-api/users")
    suspend fun createUser(@Body body: User): CreateUserResponse
}