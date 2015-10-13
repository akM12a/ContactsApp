package com.example.ak.contacts.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.example.ak.contacts.R;
import com.example.ak.contacts.db.ContactsStore;
import com.example.ak.contacts.db.ContactsStoreRealm;
import com.example.ak.contacts.ui.fragments.NavigationListener;

/**
 * All app fragments extends this class
 */
public abstract class BaseAppFragment extends Fragment {
    protected interface DatabaseQuery <T> {
        T query() throws Exception;
    }

    protected interface DatabaseResultListener <T> {
        void onComplete(T result);
    }

    protected interface DatabaseErrorListener {
        void onError(Exception e);
    }

    protected NavigationListener mNavigationListener;

    protected abstract String fragmentTitle();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationListener) {
            mNavigationListener = (NavigationListener) context;
        }
        else {
            throw new IllegalStateException("Activity not implement NavigationListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mNavigationListener.setFragmentTitle(this.fragmentTitle());
    }

    protected ContactsStore connectToContactsStore(Context context, final DatabaseErrorListener errorListener) {
        try {
            return new ContactsStoreRealm(context);
        }
        catch (final Exception e) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.database_error_header)
                    .setMessage(R.string.database_connection_error)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            if (errorListener != null) {
                                errorListener.onError(e);
                            }
                        }
                    });
            alertDialog.show();
        }
        return null;
    }

    protected void disconnectFromContactsStore(ContactsStore contactsStore, DatabaseErrorListener errorListener) {
        try {
            contactsStore.close();
        }
        catch (Exception e) {
            if (errorListener != null) {
                errorListener.onError(e);
            }
        }
    }

    protected <T> void makeDatabaseQuery(final DatabaseQuery<T> query, final DatabaseResultListener<T> callback,
                                         final DatabaseErrorListener errorListener) {
        try {
            T result = query.query();
            callback.onComplete(result);
        }
        catch (final Exception e) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.database_error_header)
                    .setMessage(e.getMessage())
                    .setCancelable(false)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            makeDatabaseQuery(query, callback, errorListener);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (errorListener != null) {
                                errorListener.onError(e);
                            }
                        }
                    });
            alertDialog.show();
        }
    }
}
