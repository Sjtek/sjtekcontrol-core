package nl.sjtek.control.core.settings

object UserManager {
    private val users: List<User> = listOf(
            User("wouter", "Wouter", "Habets"),
            User("kevin", "Kevin", "Grond"),
            User("tijn", "Tijn", "Renders"),
            User("job", "Job", "Huntjens"),
            User("ilja", "Ilja", "Oosterbaan")
    )

    fun get(name: String?): User? {
        if (name == null) return null
        return users.find { it.firstName == name }
    }

    fun get(request: spark.Request): User? {
        val userName = request.queryParams("user")
        return get(userName)
    }
}