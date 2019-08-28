package com.globalm.platform.models;

import java.io.Serializable;

public class ContactModelNew implements Serializable {
    private Contact contact;
    public String sectionName;

    public ContactModelNew(Contact contact) {
        this.contact = contact;
    }

    public ContactModelNew(String sectionName) {
        this.sectionName = sectionName;
    }

    public boolean isSection() {
        return sectionName != null;
    }

    public Contact getContact() {
        return contact;
    }

    public String getSectionName() {
        return sectionName;
    }
}
