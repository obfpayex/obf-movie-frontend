package com.obf.movie.obfmoviefrontend.model

data class NewDirector (
        var movieOid: Long,
        var name: String?,
        var persons: List<Person>?
){
    constructor() : this(0, null, arrayListOf() )
}