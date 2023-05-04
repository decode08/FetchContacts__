package com.example.fetchcontacts__

data class Contact(  val id: String,val name: String, val number: String, val type: ContactType)

enum class ContactType {
    PERSONAL,
    BUSINESS
}
