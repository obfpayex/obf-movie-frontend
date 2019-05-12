package com.obf.movie.obfmoviefrontend.model

data class Role (
        var oid: Long?,
        var characterName: String?,
        var moviePersonOid: Long,
        var roleTypeOid: Long)
{
    constructor() : this(null, null,  0 , 0 )
}