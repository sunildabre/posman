package com.gsd.pos.model;

import java.math.BigDecimal;

public class FuelSales {
	private Long shiftId;
	private Long fuelSalesId;
	private String grade;
	private String gradeName;
	private BigDecimal volume;
	private BigDecimal sales;
	private BigDecimal percentOfTotalSales;
	
	public Long getShiftId() {
		return shiftId;
	}
	public void setShiftId(Long shiftId) {
		this.shiftId = shiftId;
	}
	public Long getFuelSalesId() {
		return fuelSalesId;
	}
	public void setFuelSalesId(Long fuelSalesId) {
		this.fuelSalesId = fuelSalesId;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getGradeName() {
		return gradeName;
	}
	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public BigDecimal getSales() {
		return sales;
	}
	public void setSales(BigDecimal sales) {
		this.sales = sales;
	}
	public BigDecimal getPercentOfTotalSales() {
		return percentOfTotalSales;
	}
	public void setPercentOfTotalSales(BigDecimal percentOfTotalSales) {
		this.percentOfTotalSales = percentOfTotalSales;
	}
	
	
	
}
