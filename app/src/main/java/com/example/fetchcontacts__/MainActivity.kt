package com.example.fetchcontacts__

import ContactAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchcontacts__.Contact
//import com.example.fetchcontacts__.ContactAdapter
import com.example.fetchcontacts__.ContactType
import com.google.android.material.tabs.TabLayout
class MainActivity : AppCompatActivity() {


        private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

        private lateinit var tabLayout: TabLayout
        private lateinit var contactsRecyclerView: RecyclerView

        private var contacts: List<Contact> = emptyList()
        private var personalContacts: List<Contact> = emptyList()
        private var businessContacts: List<Contact> = emptyList()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // Set up the TabLayout
            tabLayout = findViewById(R.id.tab_layout)
            tabLayout.addTab(tabLayout.newTab().setText("Personal"))
            tabLayout.addTab(tabLayout.newTab().setText("Business"))
            tabLayout.addTab(tabLayout.newTab().setText("All"))

            // Set up the RecyclerView
            contactsRecyclerView = findViewById(R.id.contacts_recycler_view)
            contactsRecyclerView.layoutManager = LinearLayoutManager(this)

            // Request permission to read contacts if necessary
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS
                )
            } else {
                // Permission has already been granted, so load the contacts
                loadContacts()
            }

            // Set up the TabLayout listener
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        when (it.position) {
                            0 -> {
                                // Show only personal contacts
                                contactsRecyclerView.adapter = ContactAdapter(personalContacts)
                            }
                            1 -> {
                                // Show only business contacts
                                contactsRecyclerView.adapter = ContactAdapter(businessContacts)
                            }
                            2 -> {
                                // Show all contacts
                                contactsRecyclerView.adapter = ContactAdapter(contacts)
                            }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        @SuppressLint("Range")
        private fun loadContacts() {
            // Define the projection (i.e. which columns to retrieve)
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
            )

            // Perform the query
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
            )

            // Iterate over the cursor and create a Contact object for each row
            cursor?.let {
                while (it.moveToNext()) {
                    val name =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val type =
                        it.getInt(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))

                    val contactType = when (type) {
                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MAIN,
                        ContactsContract.CommonDataKinds.Phone.TYPE_OTHER -> ContactType.PERSONAL
                        ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER,
                        ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MMS,
                        ContactsContract.CommonDataKinds.Phone.TYPE_ISDN,
                        ContactsContract.CommonDataKinds.Phone.TYPE_TELEX,
                        ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK,
                        ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM -> ContactType.BUSINESS
                        else -> ContactType.PERSONAL
                    }


                    val contact = Contact(id =" ", name, number, contactType)
                    contacts += contact

                    if (contactType == ContactType.PERSONAL) {
                        personalContacts += contact
                    } else if (contactType == ContactType.BUSINESS) {
                        businessContacts += contact
                    }
                }

                // Set the adapter to show all contacts by default
                contactsRecyclerView.adapter = ContactAdapter(contacts)

                // Close the cursor
                it.close()
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, so load the contacts
                loadContacts()
            } else {
                val toast = Toast.makeText(applicationContext, "Access Denied", Toast.LENGTH_SHORT)
                toast.show()
                // Permission has been denied, so show a message and close the app
//                findViewById<TextView>(R.id.no_permission_text_view).visibility = View.VISIBLE
//                contactsRecyclerView.visibility = View.GONE
            }
        }
    }
}
