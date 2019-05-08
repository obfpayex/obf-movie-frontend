package com.obf.movie.obfmoviefrontend.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class Movie(
    var oid: Long?,
    var originalTitle: String,
    var norwegianTitle: String?,
    var runningTime: Long?,
    var releaseDate: Date?,
    var summary: String?,
    var ageLimit: String?,
    var productionYear: Long?
){
    constructor(): this(null,"",null,null,null,null,null,null)
}