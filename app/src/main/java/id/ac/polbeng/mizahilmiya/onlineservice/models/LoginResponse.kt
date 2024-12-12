package id.ac.polbeng.mizahilmiya.onlineservice.models

data class LoginResponse (
    val message: String,
    val error: Boolean,
    val data: User
)
