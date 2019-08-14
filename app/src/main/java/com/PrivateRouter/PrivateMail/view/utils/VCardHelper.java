package com.PrivateRouter.PrivateMail.view.utils;

import android.text.TextUtils;

import com.PrivateRouter.PrivateMail.model.Contact;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.StructuredName;

public class VCardHelper {
    public static String getVCardData(Contact contact) {
        VCard vcard = new VCard();
        StructuredName n = new StructuredName();
        n.setFamily(contact.getLastName());
        n.setGiven(contact.getFirstName());
        vcard.setStructuredName(n);
        vcard.setNickname(contact.getNickName());


        vcard.setFormattedName(contact.getFullName() );
        if (!TextUtils.isEmpty(contact.getPersonalEmail()))
            vcard.addEmail(contact.getPersonalEmail(), EmailType.HOME);

        if (!TextUtils.isEmpty(contact.getBusinessEmail()))
            vcard.addEmail(contact.getBusinessEmail(), EmailType.WORK);

        if (!TextUtils.isEmpty(contact.getOtherEmail()))
            vcard.addEmail(contact.getOtherEmail(), EmailType.ATTMAIL);

        String str = Ezvcard.write(vcard).version(VCardVersion.V2_1).go();

        Address adr = new Address();
        adr.setStreetAddress(contact.getBusinessAddress());
        adr.setLocality(contact.getBusinessCity());
        adr.setRegion(contact.getBusinessState());
        adr.setPostalCode(contact.getBusinessZip());
        adr.setCountry(contact.getBusinessCountry());
        adr.getTypes().add(AddressType.WORK);
        vcard.addAddress(adr);

        if (!TextUtils.isEmpty(contact.getBusinessPhone()))
            vcard.addTelephoneNumber(contact.getBusinessPhone(), TelephoneType.WORK, TelephoneType.VOICE);

        if (!TextUtils.isEmpty(contact.getBusinessFax()))
            vcard.addTelephoneNumber(contact.getBusinessFax(), TelephoneType.WORK, TelephoneType.FAX);

        vcard.addAddress(adr);

        adr = new Address();
        adr.setStreetAddress(contact.getPersonalAddress());
        adr.setLocality(contact.getPersonalCity());
        adr.setRegion(contact.getPersonalState());
        adr.setPostalCode(contact.getPersonalZip());
        adr.setCountry(contact.getPersonalCountry());
        adr.getTypes().add(AddressType.WORK);
        vcard.addAddress(adr);

        if (!TextUtils.isEmpty(contact.getPersonalPhone()))
            vcard.addTelephoneNumber(contact.getPersonalPhone(), TelephoneType.WORK, TelephoneType.VOICE);

        if (!TextUtils.isEmpty(contact.getPersonalFax()))
            vcard.addTelephoneNumber(contact.getPersonalFax(), TelephoneType.WORK, TelephoneType.FAX);

        vcard.addAddress(adr);

        if (!TextUtils.isEmpty(contact.getNotes()))
            vcard.addNote( contact.getNotes() );

        return str;
    }
}
