package com.obf.movie.obfmoviefrontend.util

object Constants {
    const val URL_Movie = "http://localhost:20200/api/movie"
    const val URL_AllInfoOneMOvie = "$URL_Movie/complete-movie-info/"
    const val URL_LatestMovie = "$URL_Movie/latest"
    const val URL_SearchOriginalTitle = "$URL_Movie/searchOriginalTitle"
    const val URL_MovieAddCategory = "$URL_Movie/add-category"
    const val URL_MovieAddActor = "$URL_Movie/add-actor"
    const val URL_MovieAddDirector = "$URL_Movie/add-director"
    const val URL_MovieRemoveDirector = "$URL_Movie/remove-director/"
    const val URL_MovieRemoveActor = "$URL_Movie/remove-actor/"
    const val URL_Person = "http://localhost:20200/api/person"
    const val URL_SearchPersonName = "$URL_Person/search-name"
    const val URL_AllInfoPerson = "$URL_Person/complete-person-info/"
    const val URL_ListRoleType = "http://localhost:20200/api/role-type"
    const val URL_Role = "http://localhost:20200/api/role"
    const val URL_Category = "http://localhost:20200/api/category"
    const val URL_Country = "http://localhost:20200/api/country"
    const val URL_User = "http://localhost:20200/api/user"
    const val URL_UserFindUser = "$URL_User/find-user"



}