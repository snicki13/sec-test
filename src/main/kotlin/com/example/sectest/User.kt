package com.example.sectest

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.security.Principal

data class User(
    val userId: String,
    val fullName: String,
): Principal, UserDetails {

    var userRole: UserRole = UserRole.Student

    override fun getName(): String = fullName

    fun getRole() = userRole

    fun hasRole(role: UserRole, vararg roles: UserRole): Boolean {
        return userRole == role || roles.contains(userRole)
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(userRole)
    }

    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return userId
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}

enum class UserRole(val value: Int, val bbbRole: BBBRole) : GrantedAuthority {
    Teacher(0, BBBRole.Moderator) {
        override fun getAuthority() = "TEACHER"
    },
    Tutor(1, BBBRole.Moderator) {
        override fun getAuthority() = "TUTOR"
    },
    Student(2, BBBRole.Participant) {
        override fun getAuthority() = "STUDENT"
    }
}

enum class BBBRole(val value: Int) {
    Moderator(0),
    Participant(1)
}
