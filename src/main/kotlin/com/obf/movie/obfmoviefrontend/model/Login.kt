package com.obf.movie.obfmoviefrontend.model

data class Login(var username: String,
                 var password: String,
                 var loginError: String?)
{
    constructor(): this("","", "")
}