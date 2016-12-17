package com.gsd.pos.model;

import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class Site {

	private Long siteId;
	private String name;
	private String storeNumber;
	private String address1;
	private String address2;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String phone;
	private String email;
	private String pager;
	private String contact;
	private String ip;
	private String status;
	private String reason;
	private Date lastCollectedDate;
	private boolean carwashEnabled;
	private boolean fuelInventoryEnabled;
	
	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPager() {
		return pager;
	}

	public void setPager(String pager) {
		this.pager = pager;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getLastCollectedDate() {
		return lastCollectedDate;
	}

	public void setLastCollectedDate(Date lastCollectedDate) {
		this.lastCollectedDate = lastCollectedDate;
	}

	public String getAddress() {

		return getStreet() + ", " + getCity() + ", " + getState() + ", "
				+ getZip();
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}



}
