package com.PrivateRouter.PrivateMail.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "contacts", primaryKeys = {"UUID"}  )
public class Contact  extends  ContactBase implements Serializable {


    @SerializedName("ParentUUID")
    private
    String parentUUID;

    @SerializedName("Storage")
    private
    String storage;

    @SerializedName("FullName")
    private
    String fullName;

    @SerializedName("PrimaryEmail")
    private
    int primaryEmail;

    @SerializedName("PrimaryPhone")
    private
    int primaryPhone;

    @SerializedName("PrimaryAddress")
    private
    int primaryAddress;

    @SerializedName("ViewEmail")
    private
    String viewEmail;

    @SerializedName("Title")
    private
    String title;

    @SerializedName("FirstName")
    private
    String firstName;

    @SerializedName("LastName")
    private
    String lastName;

    @SerializedName("NickName")
    private
    String nickName;

    @SerializedName("Skype")
    private
    String skype;

    @SerializedName("Facebook")
    private
    String facebook;

    @SerializedName("PersonalEmail")
    private
    String personalEmail;

    @SerializedName("PersonalAddress")
    private
    String personalAddress;

    @SerializedName("PersonalCity")
    private
    String personalCity;

    @SerializedName("PersonalState")
    private
    String personalState;

    @SerializedName("PersonalZip")
    private
    String personalZip;

    @SerializedName("PersonalCountry")
    private
    String personalCountry;

    @SerializedName("PersonalWeb")
    private
    String personalWeb;

    @SerializedName("PersonalFax")
    private
    String personalFax;

    @SerializedName("PersonalPhone")
    private
    String personalPhone;

    @SerializedName("PersonalMobile")
    private
    String personalMobile;

    @SerializedName("BusinessEmail")
    private
    String businessEmail;

    @SerializedName("BusinessCompany")
    private
    String businessCompany;

    @SerializedName("BusinessAddress")
    private
    String businessAddress;

    @SerializedName("BusinessCity")
    private
    String businessCity;

    @SerializedName("BusinessState")
    private
    String businessState;

    @SerializedName("BusinessZip")
    private
    String businessZip;

    @SerializedName("BusinessCountry")
    private
    String businessCountry;

    @SerializedName("BusinessJobTitle")
    private
    String businessJobTitle;

    @SerializedName("BusinessDepartment")
    private
    String businessDepartment;

    @SerializedName("BusinessOffice")
    private
    String businessOffice;

    @SerializedName("BusinessPhone")
    private
    String businessPhone;

    @SerializedName("BusinessFax")
    private
    String businessFax;

    @SerializedName("BusinessWeb")
    private
    String businessWeb;

    @SerializedName("OtherEmail")
    private
    String otherEmail;

    @SerializedName("Notes")
    private
    String notes;

    @SerializedName("BirthDay")
    private
    int birthDay;

    @SerializedName("BirthMonth")
    private
    int birthMonth;

    @SerializedName("BirthYear")
    private
    int birthYear;

    @SerializedName("DateModified")
    private
    String dateModified;

    @SerializedName("DavContacts::UID")
    private
    String davContactsUID;

    @Embedded(prefix = "groupUUIDs")
    @SerializedName("GroupUUIDs")
    private
    ArrayList<String> GroupUUIDs;

    public String getParentUUID() {
        return parentUUID;
    }

    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(int primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public int getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(int primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public int getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(int primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public String getViewEmail() {
        return viewEmail;
    }

    public void setViewEmail(String viewEmail) {
        this.viewEmail = viewEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPersonalAddress() {
        return personalAddress;
    }

    public void setPersonalAddress(String personalAddress) {
        this.personalAddress = personalAddress;
    }

    public String getPersonalCity() {
        return personalCity;
    }

    public void setPersonalCity(String personalCity) {
        this.personalCity = personalCity;
    }

    public String getPersonalState() {
        return personalState;
    }

    public void setPersonalState(String personalState) {
        this.personalState = personalState;
    }

    public String getPersonalZip() {
        return personalZip;
    }

    public void setPersonalZip(String personalZip) {
        this.personalZip = personalZip;
    }

    public String getPersonalCountry() {
        return personalCountry;
    }

    public void setPersonalCountry(String personalCountry) {
        this.personalCountry = personalCountry;
    }

    public String getPersonalWeb() {
        return personalWeb;
    }

    public void setPersonalWeb(String personalWeb) {
        this.personalWeb = personalWeb;
    }

    public String getPersonalFax() {
        return personalFax;
    }

    public void setPersonalFax(String personalFax) {
        this.personalFax = personalFax;
    }

    public String getPersonalPhone() {
        return personalPhone;
    }

    public void setPersonalPhone(String personalPhone) {
        this.personalPhone = personalPhone;
    }

    public String getPersonalMobile() {
        return personalMobile;
    }

    public void setPersonalMobile(String personalMobile) {
        this.personalMobile = personalMobile;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessCompany() {
        return businessCompany;
    }

    public void setBusinessCompany(String businessCompany) {
        this.businessCompany = businessCompany;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessCity() {
        return businessCity;
    }

    public void setBusinessCity(String businessCity) {
        this.businessCity = businessCity;
    }

    public String getBusinessState() {
        return businessState;
    }

    public void setBusinessState(String businessState) {
        this.businessState = businessState;
    }

    public String getBusinessZip() {
        return businessZip;
    }

    public void setBusinessZip(String businessZip) {
        this.businessZip = businessZip;
    }

    public String getBusinessCountry() {
        return businessCountry;
    }

    public void setBusinessCountry(String businessCountry) {
        this.businessCountry = businessCountry;
    }

    public String getBusinessJobTitle() {
        return businessJobTitle;
    }

    public void setBusinessJobTitle(String businessJobTitle) {
        this.businessJobTitle = businessJobTitle;
    }

    public String getBusinessDepartment() {
        return businessDepartment;
    }

    public void setBusinessDepartment(String businessDepartment) {
        this.businessDepartment = businessDepartment;
    }

    public String getBusinessOffice() {
        return businessOffice;
    }

    public void setBusinessOffice(String businessOffice) {
        this.businessOffice = businessOffice;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessFax() {
        return businessFax;
    }

    public void setBusinessFax(String businessFax) {
        this.businessFax = businessFax;
    }

    public String getBusinessWeb() {
        return businessWeb;
    }

    public void setBusinessWeb(String businessWeb) {
        this.businessWeb = businessWeb;
    }

    public String getOtherEmail() {
        return otherEmail;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(int birthDay) {
        this.birthDay = birthDay;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getDavContactsUID() {
        return davContactsUID;
    }

    public void setDavContactsUID(String davContactsUID) {
        this.davContactsUID = davContactsUID;
    }

    public ArrayList<String> getGroupUUIDs() {
        return GroupUUIDs;
    }

    public void setGroupUUIDs(ArrayList<String> groupUUIDs) {
        GroupUUIDs = groupUUIDs;
    }
}