package fr.tp.contacts

data class Contact(val id: String, val firstName: String, val lastName: String, val phone: String, val email: String, val photoUri: String? = null, val latitude: Double? = null, val longitude: Double? = null) {
    val displayName get() = "$firstName $lastName".trim()
}
