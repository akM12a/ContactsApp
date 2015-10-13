package com.example.ak.contacts.db;

import com.example.ak.contacts.db.model.Contact;

import java.util.List;

/**
 * Contacts CRUD
 */
public interface ContactsStore {
    void addContact(String name, String phone, int type);
    void updateContact(String uuid, String name, String phone, int type);
    Contact getContact(String uuid);
    List<Contact> getContacts();
    List<Contact> getContacts(String filter);
    void deleteContact(String uuid);
    void clear();
    void close();
}
