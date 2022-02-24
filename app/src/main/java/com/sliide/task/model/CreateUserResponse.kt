package com.sliide.task.model

import com.google.gson.internal.LinkedTreeMap
import com.sliide.task.base.BaseResponse


open class CreateUserResponse(
    code: Int,
    meta: Meta?=null,

) : BaseResponse(code, meta){
    var data: Any? = null
}