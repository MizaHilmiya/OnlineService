package id.ac.polbeng.mizahilmiya.onlineservice.models

data class JasaResponse (
    val message: String,
    val error: Boolean,
    val data: ArrayList<Jasa>
)

