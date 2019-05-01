package com.obf.movie.obfmoviefrontend.model

data class NewMovie(
        var oid: Long,
        var originalTitle: String,
        var norwegianTitle: String?,
        var runningTime: Long?,
        var releaseDate: String?,
        var summary: String?,
        var ageLimit: String?,
        var productionYear: Long?
) {
    constructor() : this(0, "", "", 0, "", "", "", 0)
}