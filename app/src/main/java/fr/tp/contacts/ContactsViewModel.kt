package fr.tp.contacts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactsViewModel : ViewModel() {
    val contacts = MutableLiveData(listOf(
        Contact("c1", "Ada", "Lovelace", "+33142220001", "ada@example.com", latitude=48.8566, longitude=2.3522),
        Contact("c2", "Alan", "Turing", "+442079460001", "alan@example.com", latitude=51.5072, longitude=-0.1276),
        Contact("c3", "Grace", "Hopper", "+12125550101", "grace@example.com")
    ))
    fun get(id: String) = contacts.value?.firstOrNull { it.id == id }
    fun update(contact: Contact) { contacts.value = contacts.value.orEmpty().map { if (it.id == contact.id) contact else it } }
}
