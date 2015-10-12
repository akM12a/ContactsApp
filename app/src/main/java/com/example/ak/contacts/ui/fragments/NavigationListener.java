package com.example.ak.contacts.ui.fragments;

import com.example.ak.contacts.db.model.Contact;

/**
 * App navigation
 */
public interface NavigationListener {
    void navigateAddContact();
    void navigateEditContact(Contact contact);
    void goBack();

    void setFragmentTitle(String title);
    void showBackButton(boolean visible);
}
