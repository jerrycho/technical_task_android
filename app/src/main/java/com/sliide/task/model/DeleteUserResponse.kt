package com.sliide.task.model

import com.sliide.task.base.BaseResponse


open class DeleteUserResponse(
    code: Int,
    meta: Meta?=null
) : BaseResponse(code, meta) {
    var data: DeleteUserData? = null
}