package com.obf.movie.obfmoviefrontend.model

data class LoggedOnUser(var username: String,
                        var lastUsaged: Long,
                        var level: Long)
{
    constructor(): this("",0,0)
}