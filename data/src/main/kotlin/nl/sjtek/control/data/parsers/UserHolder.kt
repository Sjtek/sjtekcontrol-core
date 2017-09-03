package nl.sjtek.control.data.parsers

import nl.sjtek.control.data.staticdata.User

data class UserHolder(val users: List<User> = listOf(), @Transient val exception: Exception? = null)