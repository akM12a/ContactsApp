package com.example.ak.contacts.db;

import android.content.Context;

import com.example.ak.contacts.db.model.Contact;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Contacts CRUD implementation
 */
public class ContactsStoreRealm implements ContactsStore {

    private Realm sRealm;

    public ContactsStoreRealm(Context context) {
        sRealm = Realm.getInstance(context);
    }

    @Override
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

    @Override
    public void clear() {
        sRealm.beginTransaction();
        sRealm.clear(Contact.class);
        sRealm.commitTransaction();
    }

    @Override
    public void addContact(String name, String phone, int type) {
        sRealm.beginTransaction();
        Contact contact = sRealm.createObject(Contact.class);
        contact.setUuid(UUID.randomUUID().toString());
        contact.setName(name);
        contact.setPhone(phone);
        contact.setType(type);
        sRealm.commitTransaction();
    }

    @Override
    public void updateContact(String uuid, String name, String phone, int type) {
        sRealm.beginTransaction();
        Contact contact = getContact(uuid);
        contact.setName(name);
        contact.setPhone(phone);
        contact.setType(type);
        sRealm.commitTransaction();
    }

    @Override
    public Contact getContact(String uuid) {
        RealmQuery<Contact> query = sRealm.where(Contact.class).equalTo("uuid", uuid);
        return query.findFirst();
    }

    @Override
    public List<Contact> getContacts() {
        return getContacts(null);
    }

    @Override
    public List<Contact> getContacts(String filter) {
        RealmQuery<Contact> query = sRealm.where(Contact.class);
        if ((filter != null) && (filter.length() > 0)) {
            query = query.contains("name", filter, false).or().contains("phone", filter, false);
        }
        return query.findAllSorted("name");
    }

    @Override
    public void deleteContact(String uuid) {
        sRealm.beginTransaction();
        RealmQuery<Contact> query = sRealm.where(Contact.class).equalTo("uuid", uuid);
        query.findAll().clear();
        sRealm.commitTransaction();
    }
}
