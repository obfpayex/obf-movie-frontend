package com.obf.movie.obfmoviefrontend.model

data class PersonAllInfo(val person: Person,
                         val moviesToActor: List<MovieToActor>?,
                         val moviesToDirector: List<MovieToDirector>?)