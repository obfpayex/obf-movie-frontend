package com.obf.movie.obfmoviefrontend.model

data class User (var oid: Long?,
                 var username: String?,
                 var password: String?,
                 var userLevel: Long?){
    constructor() : this(null,null,null,null)
}