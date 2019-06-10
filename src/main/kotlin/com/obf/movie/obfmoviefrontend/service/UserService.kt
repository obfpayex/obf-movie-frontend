package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.LoggedOnUser
import com.obf.movie.obfmoviefrontend.model.User
import com.obf.movie.obfmoviefrontend.util.Constants
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.client.RestTemplate
import java.math.BigInteger
import java.security.MessageDigest
import javax.servlet.http.HttpSession

@Service
class UserService(private val restTemplate: RestTemplate,
                  private val utilService: UtilService) {

    fun getAndCheckUser(password: String, userName: String): LoggedOnUser {
        try {
            return searchUser(userName, password)
        } catch (e: Exception) {
            throw Exception("Cant log inn")
        }
    }

    fun checkUser(httpSession: HttpSession, model: Model): Boolean {
        var user: LoggedOnUser
        val sessionObject = httpSession.getAttribute("user")
        if (sessionObject == null) {
            user = LoggedOnUser("guest", System.currentTimeMillis(), 0)
        } else {
            user = sessionObject as LoggedOnUser
        }


        if (user.level < 10) {
            if (user.lastUsaged > 0) {
                if ((System.currentTimeMillis() - user.lastUsaged) > 900000) {
                    return false
                }
            }

        }
        user.lastUsaged = System.currentTimeMillis()
        httpSession.setAttribute("user", user)
        model.addAttribute("user", user)
        return true
    }

//    fun <S>jalla(httpSession: HttpSession, model: Model,  operation: Supplier<S>): S {
//        var user = httpSession.getAttribute("user") as LoggedOnUser
//
//        if (user.level < 10){
//
//        }
//        model.addAttribute("user", user)
//        return operation.get()
//    }


    fun searchUser(username: String, password: String): LoggedOnUser {

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<User> = restTemplate.exchange(
                "${Constants.URL_UserFindUser}?userName=$username&passWord=${password.md5()}",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<User>())

        if (result.statusCode != HttpStatus.OK) {
            throw Exception("LoggedOnUser not found")
        }
        var user = result.body as User
        return LoggedOnUser(user.username!!, System.currentTimeMillis(), user.userLevel!!)
    }

    fun setupManageUser(model: Model): String {
        var users = getAllUsers()
        removePassword(users)
        model.addAttribute("users", users)
        model.addAttribute("user", User())
        return "manageUsers"
    }

    private fun removePassword(users: List<User>) {
        users.stream().forEach { it.password = "" }
    }


    fun getAllUsers(): List<User> {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<User>> = restTemplate.exchange(
                Constants.URL_User + "?size=1000&page=0&sort=username,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<User>>())


        return result.body as List

    }

    fun saveNewUser(user: User, httpSession: HttpSession, model: Model): String {
        if (validateUser(user, model)) {
            user.password = user.password!!.md5()
            utilService.postObjectNoReplyCreated(user, Constants.URL_User)
        }
        return setupManageUser(model)
    }

    private fun validateUser(user: User, model: Model): Boolean {

        if (user.password.isNullOrEmpty()) {
            model.addAttribute("message", "Password can't be empty")
            return false
        }

        if (user.username.isNullOrEmpty()) {
            model.addAttribute("message", "Username can't be empty")
            return false
        }


        if (user.userLevel == null)
            user.userLevel = 1

        return true
    }

    fun checkUserLevel(httpSession: HttpSession, userLevel: Long): Boolean {
        val user = httpSession.getAttribute("user") as LoggedOnUser

        if (user.level >= userLevel) {
            return true
        }

        return false
    }

    fun removeUser(userOid: Long, model: Model): String {
        restTemplate.delete("${Constants.URL_User}/$userOid")
        return "redirect:/manage-user"
    }


    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

}