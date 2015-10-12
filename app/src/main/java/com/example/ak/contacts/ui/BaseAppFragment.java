package com.example.ak.contacts.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.example.ak.contacts.ui.fragments.NavigationListener;

/**
 * All app fragments extends this class
 */
public abstract class BaseAppFragment extends Fragment {

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
}
