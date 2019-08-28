package com.globalm.platform.utils;

import com.globalm.platform.models.ContactModel;

public class AddContactModel {

    private ContactModel mContactModel;

    public AddContactModel(ContactModel contactModel) {
        mContactModel = contactModel;
    }

    public ContactModel getContactModel() {
        return mContactModel;
    }
}
