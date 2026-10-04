package com.ecomflow.model;

public class Address {
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;

    public Address(String street, String city, String state, String pincode, String country) {
        this.street = (street != null) ? street.trim() : "";
        this.city = (city != null) ? city.trim() : "";
        this.state = (state != null) ? state.trim() : "";
        this.pincode = (pincode != null) ? pincode.trim() : "";
        this.country = (country != null) ? country.trim() : "";
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = (street != null) ? street.trim() : "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = (city != null) ? city.trim() : "";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = (state != null) ? state.trim() : "";
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = (pincode != null) ? pincode.trim() : "";
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = (country != null) ? country.trim() : "";
    }

    public static boolean isValidPincode(String pincode) {
        if (pincode == null || pincode.trim().isEmpty()) {
            return false;
        }
        return pincode.trim().matches("^[0-9A-Za-z\\s\\-]{3,10}$");
    }

    public boolean isComplete() {
        return !street.isEmpty()
                && !city.isEmpty()
                && !state.isEmpty()
                && isValidPincode(pincode)
                && !country.isEmpty();
    }

    @Override
    public String toString() {
        return street + ", " + city + ", " + state + " - " + pincode + ", " + country;
    }
}
