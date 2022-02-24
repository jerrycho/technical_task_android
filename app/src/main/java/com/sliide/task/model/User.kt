package com.sliide.task.model

data class User (
    var id : Long = 0,
    val name : String,
    val email : String,
    val gender : String  = "male",
    val status : String  = "active"
)