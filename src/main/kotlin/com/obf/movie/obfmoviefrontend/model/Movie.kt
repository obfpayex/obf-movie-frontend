package com.obf.movie.obfmoviefrontend.model

import java.util.*

data class Movie(
    var oid: Long,
    var originalTitle: String,
    var norwegianTitle: String?,
    var runningTime: Long?,
    var releaseDate: Date?,
    var summary: String?,
    var ageLimit: String,
    var productionYear: Long
)