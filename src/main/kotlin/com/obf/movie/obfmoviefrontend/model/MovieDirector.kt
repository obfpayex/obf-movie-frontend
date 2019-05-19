package com.obf.movie.obfmoviefrontend.model

data class MovieDirector (var oid: Long?,
                          var movieOid: Long,
                          var personOid: Long)
{
    constructor() : this(null, 0, 0)
}