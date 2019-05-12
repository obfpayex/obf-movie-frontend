package com.obf.movie.obfmoviefrontend.model

data class NewActor (
        var movieOid: Long,
        var name: String?,
        var characterName: String?,
        var roleTypeOid: Long?,
        var persons: List<Person>?
){
    constructor() : this(0, null, null, null, arrayListOf() )
}