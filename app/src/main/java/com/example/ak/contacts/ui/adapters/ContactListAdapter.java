package com.example.ak.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ak.contacts.R;
import com.example.ak.contacts.db.model.Contact;

import java.util.List;

/**
 * Contacts recycler adapter
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>  {

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private View mItemView;
        private TextView mNameText;
        private TextView mPhoneText;
        private TextView mPhoneType;

        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            mItemView = view.findViewById(R.id.layout_item);
            mNameText = (TextView) itemView.findViewById(R.id.text_name);
            mPhoneText = (TextView) itemView.findViewById(R.id.text_phone);
            mPhoneType = (TextView) itemView.findViewById(R.id.text_phone_type);
        }

        public void bind(Contact contact) {
            mItemView.setBackgroundResource((getAdapterPosition() == mSelectedIndex) ?
                    R.color.color_selector_row : android.R.color.transparent);
            mNameText.setText(contact.getName());
            mPhoneText.setText(contact.getPhone());

            switch (contact.getType()) {
                case 0: mPhoneType.setText(R.string.phone_type_mobile);
                    break;
                case 1: mPhoneType.setText(R.string.phone_type_work);
                    break;
                case 2: mPhoneType.setText(R.string.phone_type_home);
                    break;
                default:
                    mPhoneType.setText(R.string.phone_type_mobile);
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateItem(mSelectedIndex);
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition == mSelectedIndex) {
                        deselectContact();
                    }
                    else {
                        selectContact(adapterPosition);
                    }
                }
            });
        }
    }

    public interface ContactSelectListener {
        void onContactSelected();
        void onContactDeselected();
    }

    private ContactSelectListener mListener;
    private List<Contact> mContacts;
    private int mSelectedIndex;
    private String mSelectedUuid;

    public ContactListAdapter(ContactSelectListener listener) {
        mListener = listener;
        setSelectedIndex(-1);
    }

    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        boolean selected = false;
        if (mSelectedUuid != null) {
            for (int i=0; i<mContacts.size(); ++i) {
                if (mContacts.get(i).getUuid().equals(mSelectedUuid)) {
                    selectContact(i);
                    selected = true;
                    break;
                }
            }
        }
        if (!selected) {
            setSelectedIndex(-1);
        }
        notifyDataSetChanged();
    }

    public Contact getSelectedContact() {
        if (mSelectedIndex == -1)
            return null;
        return mContacts.get(mSelectedIndex);
    }

    public void deleteSelectedItem() {
        if (mSelectedIndex != -1) {
            notifyItemRemoved(mSelectedIndex);
            deselectContact();
        }
    }

    private void updateItem(int index) {
        if (index != -1) {
            notifyItemChanged(index);
        }
    }

    private void selectContact(int index) {
        setSelectedIndex(index);
        updateItem(mSelectedIndex);
        mListener.onContactSelected();
    }

    private void deselectContact() {
        int index = mSelectedIndex;
        setSelectedIndex(-1);
        updateItem(index);
        mListener.onContactDeselected();
    }

    private void setSelectedIndex(int index) {
        mSelectedIndex = index;
        mSelectedUuid = (mSelectedIndex != -1)? mContacts.get(mSelectedIndex).getUuid(): null;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.partial_contact_list_item, viewGroup, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {
        Contact contact = mContacts.get(position);
        if (contact != null) {
            contactViewHolder.bind(contact);
        }
    }

    @Override
    public int getItemCount() {
        return (mContacts != null)? mContacts.size(): 0;
    }
}
