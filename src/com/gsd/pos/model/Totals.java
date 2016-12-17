package com.gsd.pos.model;

import java.math.BigDecimal;

public class Totals {
	private Long shiftId;
	private BigDecimal volume;
	private BigDecimal totalFuelSales;
	private BigDecimal totalDeptSales;
	private BigDecimal totalTax;
	private BigDecimal fuelDiscounts;
	private BigDecimal otherDiscounts;
	
	public Long getShiftId() {
		return shiftId;
	}
	public void setShiftId(Long shiftId) {
		this.shiftId = shiftId;
	}
	public BigDecimal getTotalFuelSales() {
		if (totalFuelSales == null) {
			return new BigDecimal(0);
		}

		return totalFuelSales;
	}
	public void setTotalFuelSales(BigDecimal totalFuelSales) {
		this.totalFuelSales = totalFuelSales;
	}
	public BigDecimal getTotalTax() {
		if (totalTax == null) {
			return new BigDecimal(0);
		}

		return totalTax;
	}
	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}
	public BigDecimal getFuelDiscounts() {
		if (fuelDiscounts == null) {
			return new BigDecimal(0);
		}

		return fuelDiscounts;
	}
	public void setFuelDiscounts(BigDecimal fuelDiscounts) {
		this.fuelDiscounts = fuelDiscounts;
	}
	public BigDecimal getOtherDiscounts() {
		if (otherDiscounts == null) {
			return new BigDecimal(0);
		}

		return otherDiscounts;
	}
	public void setOtherDiscounts(BigDecimal otherDiscounts) {
		this.otherDiscounts = otherDiscounts;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public BigDecimal getTotalDeptSales() {
		if (totalDeptSales == null) {
			return new BigDecimal(0);
		}
		return totalDeptSales;
	}
	public void setTotalDeptSales(BigDecimal totalDeptSales) {
		this.totalDeptSales = totalDeptSales;
	}
	
	
	
}
