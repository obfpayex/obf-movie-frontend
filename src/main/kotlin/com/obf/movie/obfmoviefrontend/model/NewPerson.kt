package com.obf.movie.obfmoviefrontend.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

data class NewPerson(var oid: Long?,
                     var firstName: String?,
                     var lastName: String?,
                     var born: String?,
                     var deceased: String?,
                     var bornCountryOid: Long?,
                     var picture: String?,
                     var gender: String?,
                     var middleName: String?,
                     var bornName: String?) {
    constructor() : this(0, "", "", null, null, 0, "", "", "", "")
}