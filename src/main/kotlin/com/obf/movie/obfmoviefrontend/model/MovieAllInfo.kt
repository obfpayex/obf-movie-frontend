package com.obf.movie.obfmoviefrontend.model

data class MovieAllInfo(var movie: Movie,
                        var categories: List<Category>?,
                        var actorsToMovie: List<ActorToMovie>?,
                        var directorsToMovie: List<DirectorToMovie>?,
                        var categoriesAsString: String?)