package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
    @SerializedName("UUID")
    private
    String UUID;

    @SerializedName("Name")
    private
    String name;

//    @SerializedName("IsOrganization")
//    private
//    int isOrganization;

    @SerializedName("Email")
    private
    String email;

    @SerializedName("Country")
    private
    String country;

    @SerializedName("City")
    private
    String city;

    @SerializedName("Company")
    private
    String Company;

    @SerializedName("Fax")
    private
    String fax;

    @SerializedName("Phone")
    private
    String phone;

    @SerializedName("State")
    private
    String state;

    @SerializedName("Street")
    private
    String street;

    @SerializedName("Web")
    private
    String web;

    @SerializedName("Zip")
    private
    String zip;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public int isOrganization() {
//        return isOrganization;
//    }
//
//    public void setIsOrganization(int organization) {
//        isOrganization = organization;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

}
