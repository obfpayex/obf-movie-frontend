package com.obf.movie.obfmoviefrontend.model

data class MovieToActor(var originalTitle: String,
                        var movieOid: Long,
                        var characterName: String?,
                        var roleType: String?,
                        var roleOid: Long?,
                        var roleTypeOid: Long?,
                        var productionYear: Long? = 0)