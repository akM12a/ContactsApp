package com.example.ak.contacts.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ak.contacts.R;
import com.example.ak.contacts.db.ContactsStore;
import com.example.ak.contacts.db.model.Contact;
import com.example.ak.contacts.ui.BaseAppFragment;

/**
 * Fragment for add/edit contact
 */
public class ContactPropertiesFragment extends BaseAppFragment {
    private static final String ARG_CONTACT_UUID = "uuid";

    private MenuItem mDoneMenuItem;
    private EditText mNameEdit;
    private EditText mPhoneEdit;
    private Spinner mPhoneTypeSpinner;

    private Contact mContact;
    private ContactsStore mContactsStore;

    public static ContactPropertiesFragment newInstance(String uuid) {
        ContactPropertiesFragment fragment = new ContactPropertiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTACT_UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContactsStore = new ContactsStore(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_properties, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mNameEdit = (EditText) view.findViewById(R.id.edit_name);
        mPhoneEdit = (EditText) view.findViewById(R.id.edit_phone);
        mPhoneTypeSpinner = (Spinner) view.findViewById(R.id.spinner_phone_type);

        ArrayAdapter<String> phoneTypeAdapter = new ArrayAdapter<>(getContext(), R.layout.partial_phone_type_item,
                new String[] {getString(R.string.phone_type_mobile), getString(R.string.phone_type_work), getString(R.string.phone_type_home)});
        mPhoneTypeSpinner.setAdapter(phoneTypeAdapter);

        Bundle args = getArguments();
        if (args != null) {
            String uuid = args.getString(ARG_CONTACT_UUID);
            if (uuid != null) {
                mContact = mContactsStore.getContact(uuid);
                mNameEdit.setText(mContact.getName());
                mPhoneEdit.setText(mContact.getPhone());
                mPhoneTypeSpinner.setSelection(mContact.getType());
            }
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateActionButtonsState();
            }
        };

        mPhoneEdit.addTextChangedListener(textWatcher);
        mNameEdit.addTextChangedListener(textWatcher);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_properties, menu);
        mDoneMenuItem = menu.findItem(R.id.action_done);
        updateActionButtonsState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (mContact == null) {
                    mContactsStore.addContact(mNameEdit.getText().toString(), mPhoneEdit.getText().toString(),
                            mPhoneTypeSpinner.getSelectedItemPosition());
                } else {
                    mContactsStore.updateContact(mContact.getUuid(), mNameEdit.getText().toString(),
                            mPhoneEdit.getText().toString(), mPhoneTypeSpinner.getSelectedItemPosition());
                }
                mNavigationListener.goBack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        mContactsStore.close();
        super.onDestroy();
    }

    private void updateActionButtonsState() {
        if (mDoneMenuItem == null) {
            return;
        }

        if ((mNameEdit.getText().toString().trim().length() > 0) &&
                (mPhoneEdit.getText().toString().trim().length() > 0)) {
            mDoneMenuItem.setEnabled(true);
            mDoneMenuItem.getIcon().setAlpha(255);
        }
        else {
            mDoneMenuItem.setEnabled(false);
            mDoneMenuItem.getIcon().setAlpha(70);
        }
    }

    @Override
    protected String fragmentTitle() {
        return (mContact == null)? getString(R.string.fragment_add_contact_title):
                getString(R.string.fragment_edit_contact_title);
    }
}
