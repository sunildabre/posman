package com.gsd.pos.dao;

import java.util.Date;
import java.util.List;

import com.gsd.pos.model.CarwashSales;
import com.gsd.pos.model.FuelInventory;
import com.gsd.pos.model.FuelSales;
import com.gsd.pos.model.Payment;
import com.gsd.pos.model.ShiftReport;
import com.gsd.pos.model.Site;
import com.gsd.pos.model.User;

public interface SiteDao {
	
	public List<Site> getSites() ;

	public boolean saveReport(ShiftReport report);

	public List<FuelSales> getFuelSales(Long shiftId);
	
	public List<Payment> getPayments(Long shiftId);

	public Long getShiftId(Long selectedSiteId, Date selectedDate);

	Site getSite(Long siteId);

	ShiftReport getShift(Long shiftId);

	boolean saveReport(ShiftReport report, boolean overwrite);

	List<Site> getActiveSites();

	void updateSiteReason(Long siteId, String reason);

	ShiftReport getShift(Long siteId, Date date);

	List<CarwashSales> getCarWashSales(Long shiftId);

	List<Site> getActiveSites(User user);

	List<FuelInventory> getFuelInventory(Long shiftId);
}
