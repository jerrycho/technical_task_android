package com.sliide.task.model

import com.sliide.task.base.BaseResponse


open class UserListResponse(
    code: Int,
    meta: Meta,

) : BaseResponse(code, meta){
    var data: List<User> = emptyList()
}