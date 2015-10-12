package com.example.ak.contacts.db;

import android.content.Context;

import com.example.ak.contacts.db.model.Contact;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Contacts CRUD
 */
public class ContactsStore {

    private Realm sRealm;

    public ContactsStore(Context context) {
        sRealm = Realm.getInstance(context);
    }

    public void close() {
        sRealm.close();
    }

    public void generateRandomData() {
        sRealm.beginTransaction();
        sRealm.clear(Contact.class);

        for (int i=10; i<10000; ++i) {
            Contact contact = sRealm.createObject(Contact.class);
            contact.setUuid(UUID.randomUUID().toString());
            contact.setName("John Silver - " + i);
            contact.setPhone(String.valueOf(i));
            contact.setType(0);
        }

        sRealm.commitTransaction();
    }

    public void clear() {
        sRealm.beginTransaction();
        sRealm.clear(Contact.class);
        sRealm.commitTransaction();
    }

    public void addContact(String name, String phone, int type) {
        sRealm.beginTransaction();
        Contact contact = sRealm.createObject(Contact.class);
        contact.setUuid(UUID.randomUUID().toString());
        contact.setName(name);
        contact.setPhone(phone);
        contact.setType(type);
        sRealm.commitTransaction();
    }

    public void updateContact(String uuid, String name, String phone, int type) {
        sRealm.beginTransaction();
        Contact contact = getContact(uuid);
        contact.setName(name);
        contact.setPhone(phone);
        contact.setType(type);
        sRealm.commitTransaction();
    }

    public Contact getContact(String uuid) {
        RealmQuery<Contact> query = sRealm.where(Contact.class).equalTo("uuid", uuid);
        return query.findFirst();
    }

    public List<Contact> getContacts() {
        return getContacts(null);
    }

    public List<Contact> getContacts(String filter) {
        RealmQuery<Contact> query = sRealm.where(Contact.class);
        if ((filter != null) && (filter.length() > 0)) {
            query = query.contains("name", filter, false).or().contains("phone", filter, false);
        }
        return query.findAllSorted("name");
    }

    public void deleteContact(String uuid) {
        sRealm.beginTransaction();
        RealmQuery<Contact> query = sRealm.where(Contact.class).equalTo("uuid", uuid);
        query.findAll().clear();
        sRealm.commitTransaction();
    }
}
