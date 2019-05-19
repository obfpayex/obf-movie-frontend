package com.obf.movie.obfmoviefrontend.model


data class ActorToMovie(
        var firstName: String?,
        var middleName: String?,
        var lastName: String?,
        var personOid: Long,
        var characterName: String?,
        var roleOid: Long?,
        var roleTypeOid: Long?,
        var roleType: String?,
        var movieActorOid: Long)
