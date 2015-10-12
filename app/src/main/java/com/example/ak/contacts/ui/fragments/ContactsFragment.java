package com.example.ak.contacts.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ak.contacts.R;
import com.example.ak.contacts.db.ContactsStore;
import com.example.ak.contacts.db.model.Contact;
import com.example.ak.contacts.ui.BaseAppFragment;
import com.example.ak.contacts.ui.adapters.ContactListAdapter;

/**
 * Fragment for display contact list
 */
public class ContactsFragment extends BaseAppFragment {

    private ContactListAdapter mAdapter;
    private MenuItem mEditMenuItem;
    private MenuItem mDeleteMenuItem;
    private SearchView mSearchView;
    private TextView mNoContactsMessage;
    private boolean mDataWithFilter;

    private String mSearchQuery;

    private ContactsStore mContactsStore;

    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mContactsStore = new ContactsStore(getContext());
        //mContactsStore.generateRandomData();
        //mContactsStore.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.button_add_contact);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationListener.navigateAddContact();
            }
        });

        mNoContactsMessage = (TextView) view.findViewById(R.id.text_no_contacts_message);
        mNoContactsMessage.setVisibility(View.INVISIBLE);

        final RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_clinics);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mAdapter == null) {
            mAdapter = new ContactListAdapter(new ContactListAdapter.ContactSelectListener() {
                @Override
                public void onContactSelected() {
                    updateContactActionsVisibility();
                }

                @Override
                public void onContactDeselected() {
                    updateContactActionsVisibility();
                }
            });
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contacts, menu);
        mEditMenuItem = menu.findItem(R.id.action_edit);
        mDeleteMenuItem = menu.findItem(R.id.action_delete);
        updateContactActionsVisibility();

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mNavigationListener.showBackButton(false);
                updateData(null);
                return true;
            }
        });

        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                updateData(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                updateData(s);
                return true;
            }
        });

        if (mSearchQuery != null) {
            String query = mSearchQuery;
            searchMenuItem.expandActionView();
            mSearchView.setQuery(query, true);
            mSearchView.clearFocus();
        }
        else {
            updateData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteSelectedContact();
                return true;

            case R.id.action_edit:
                editSelectedContact();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        mSearchView.setOnQueryTextListener(null);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mContactsStore.close();
        super.onDestroy();
    }

    private void updateData() {
        mAdapter.setContacts(mContactsStore.getContacts());
        updateContactActionsVisibility();
        mDataWithFilter = false;
        updateNoContactsMessage();
        mSearchQuery = null;
    }

    private void updateData(String filter) {
        if ((filter != null) && (filter.trim().length() > 0)) {
            mAdapter.setContacts(mContactsStore.getContacts(filter));
            updateContactActionsVisibility();
            mDataWithFilter = true;
            updateNoContactsMessage();
        }
        else {
            if (mDataWithFilter) {
                updateData();
            }
        }
        mSearchQuery = filter;
    }

    private void updateNoContactsMessage() {
        mNoContactsMessage.setText(mDataWithFilter? R.string.no_results: R.string.you_have_no_contacts_yet);
        mNoContactsMessage.setVisibility((mAdapter.getItemCount() == 0) ? View.VISIBLE : View.INVISIBLE);
    }

    private void editSelectedContact() {
        Contact contact = mAdapter.getSelectedContact();
        if (contact != null) {
            mNavigationListener.navigateEditContact(contact);
        }
    }

    private void deleteSelectedContact() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_item_header)
                .setMessage(R.string.delete_item_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Contact contact = mAdapter.getSelectedContact();
                        if (contact != null) {
                            mAdapter.deleteSelectedItem();
                            mContactsStore.deleteContact(contact.getUuid());
                            updateNoContactsMessage();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }

    private void updateContactActionsVisibility() {
        boolean visible = mAdapter.getSelectedContact() != null;
        if (mDeleteMenuItem != null) {
            mDeleteMenuItem.setVisible(visible);
        }
        if (mEditMenuItem != null) {
            mEditMenuItem.setVisible(visible);
        }
    }

    @Override
    protected String fragmentTitle() {
        return getString(R.string.fragment_contacts_title);
    }
}
