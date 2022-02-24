package com.sliide.task.base

import com.sliide.task.model.Meta


open class BaseResponse(
    val code: Int,
    val meta: Meta?
)