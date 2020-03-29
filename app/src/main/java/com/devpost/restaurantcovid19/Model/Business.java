package com.devpost.restaurantcovid19.Model;

import java.util.List;

public class Business {
    public String name;
    public String locationName;
    public String address;
    public List<Double> l;
    public String cert;
    public String cuisine;
    public String phone;
    public String url;
    public String start;
    public String end;
    public boolean orderFromCar;
    public boolean contactLess;
    public String etd;
    public boolean singleServiceItem;
    public String containerUsed;
    public List<String> payment;

    public Business() {

    }

    public Business(String name, String locationName, String address, List<Double> l,
                    String cert, String cuisine, String phone, String url, String start, String end,
                    boolean orderFromCar, boolean contactLess, String etd, boolean singleServiceItem,
                    String containerUsed, List<String> payment) {
        this.name = name;
        this.locationName = locationName;
        this.address = address;
        this.l = l;
        this.cert = cert;
        this.cuisine = cuisine;
        this.phone = phone;
        this.url = url;
        this.start = start;
        this.end = end;
        this.orderFromCar = orderFromCar;
        this.contactLess = contactLess;
        this.etd = etd;
        this.singleServiceItem = singleServiceItem;
        this.containerUsed = containerUsed;
        this.payment = payment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Double> getL() {
        return l;
    }

    public void setL(List<Double> l) {
        this.l = l;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isOrderFromCar() {
        return orderFromCar;
    }

    public void setOrderFromCar(boolean orderFromCar) {
        this.orderFromCar = orderFromCar;
    }

    public boolean isContactLess() {
        return contactLess;
    }

    public void setContactLess(boolean contactLess) {
        this.contactLess = contactLess;
    }

    public String getEtd() {
        return etd;
    }

    public void setEtd(String etd) {
        this.etd = etd;
    }

    public boolean isSingleServiceItem() {
        return singleServiceItem;
    }

    public void setSingleServiceItem(boolean singleServiceItem) {
        this.singleServiceItem = singleServiceItem;
    }

    public String getContainerUsed() {
        return containerUsed;
    }

    public void setContainerUsed(String containerUsed) {
        this.containerUsed = containerUsed;
    }

    public List<String> getPayment() {
        return payment;
    }

    public void setPayment(List<String> payment) {
        this.payment = payment;
    }
}
