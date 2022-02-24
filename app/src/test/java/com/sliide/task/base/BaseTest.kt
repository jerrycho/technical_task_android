package com.sliide.task.base

import com.sliide.task.constants.*
import com.sliide.task.model.*

open class BaseTest {
    fun getUserListResponse(): UserListResponse {
        var response = UserListResponse(
            code = HTTP_STATUS_OK,
            meta = Meta(
                pagination = Pagination(
                    total = 1,
                    pages = 1,
                    page = 1,
                    limit = 20
                )
            )
        )
        response.data = listOf(
            User(
                id = 1,
                name = "this is name",
                email = "email3@test.com",
                gender = "male",
                status = "active"
            )
        )
        return response
    }

    fun getCreateUserResponseSuccess(): CreateUserResponse {
        var response = CreateUserResponse(
            code = CREATE_USER_OK,
            meta= null
        )
        response.data =
            User(
                id = 1,
                name = "this is name",
                email = "email3@test.com",
                gender = "male",
                status = "active"
            )
        return response;
    }

    fun getCreateUserResponseFailed(): CreateUserResponse {
        var response = CreateUserResponse(
            code = CREATE_USER_FAIL,
            meta= null,
        )
        response.data = arrayListOf(
            CreateUserError(
                field = "email",
                message = "wrong email address",
            )
        )
        return response
    }

    fun getDeleteUserResponseSuccess(): DeleteUserResponse {
        var response = DeleteUserResponse(
            code = DELETE_USER_OK,
            meta= null
        )
        return response;
    }

    fun getDeleteUserResponseFail(): DeleteUserResponse {
        var response = DeleteUserResponse(
            code = DELETW_USER_FAIL,
            meta= null
        )
        response.data = DeleteUserData(
            message = "Resource not found"
        )
        return response;
    }

    fun getMockNewUser() : User {
        return User(
            name = "this is name",
            email = "email3@test.com",
            gender = "male",
            status = "active"
        )
    }
}