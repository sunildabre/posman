package com.gsd.pos.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.gsd.pos.dao.SiteDao;
import com.gsd.pos.dao.impl.SitesDaoImpl;
import com.gsd.pos.model.CarwashSales;
import com.gsd.pos.model.Discount;
import com.gsd.pos.model.FuelInventory;
import com.gsd.pos.model.FuelSales;
import com.gsd.pos.model.Payment;
import com.gsd.pos.model.ShiftReport;
import com.gsd.pos.model.Site;
import com.gsd.pos.model.User;
import com.gsd.pos.reports.pdf.ShiftReportGenerator;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@Title("MainUI")
@SuppressWarnings("unchecked")
// @PreserveOnRefresh
// use this to NOT load the ui again on refresh
public class SitesView extends CustomComponent implements View {
	/* User interface components are stored in session. */
	private Table sitesList = new Table();
	private Table fuelSalesTable;
	private Table fuelInventoryTable;
	private Table carwashTable;
	private Table paymentsTable;
	private Table discountTable;
	private TextField searchField = new TextField();
	private Panel siteInfoPanel;
	private Panel shiftInfoPanel;
	private Panel totalPanel;
	private Panel header;
	private Panel footer;
	public static final String NAME = "Site Name";
	public static final String LAST_COLLECTED = "Last Collected";
	public static final String SITE_ID = "Site Id";
	public static final String ADDRESS = "Address";
	private static final String IS_CURRENT = "IS CURRENT";
	private static final String GRADE = "Grade";
	private static final String GRADE_NAME = "Grade Name";
	private static final String TANK_ID = "Tank Number";
	private static final String SALES = "Sales";
	private static final String VOLUME = "Volume";
	private static final String COUNT = "count";
	private static final String PERCENT_OF_TOTAL_SALES = "% Of Total Fuel Sales";
	private static final String[] fieldNames = new String[] { NAME };
	private static final String[] allFuelTableColumns = new String[] { GRADE,
			GRADE_NAME, VOLUME, SALES, PERCENT_OF_TOTAL_SALES };
	private static final String[] allFuelInventoryTableColumns = new String[] { 
		GRADE_NAME, TANK_ID, VOLUME };

	private static final String NET_SALES = "Net Sales";
	private static final String GROSS_SALES = "Gross Sales";
	private static final String ITEM_COUNT = "Item Count";
	private static final String REFUND_COUNT = "Refund Count";
	private static final String NET_COUNT = "Net Count";
	private static final String REFUND = "Refund";
	private static final String DISCOUNT = "Discount";
	private static final String BOLD = "BOLD";

	private static final String[] allCarwashTableColumns = new String[] { GROSS_SALES,
		ITEM_COUNT, REFUND_COUNT, NET_COUNT, REFUND,DISCOUNT,NET_SALES };

	private static final String METHOD_OF_PAYMENT = "Method of Payment";
	private static final String AMOUNT = "Sales";
	private static final String[] allPaymentTableColumns = new String[] {
			METHOD_OF_PAYMENT, AMOUNT };
	private static final String[] allDiscountTableColumns = new String[] {
		GRADE_NAME, COUNT, AMOUNT };
	private IndexedContainer sitesContainer = null;
	private SiteDao sitesDao ;
	private Long selectedSiteId;
	private DateTime selectedDate;
	private ShiftReport selectedShift = null;
	private Site selectedSite = null;
	private String FOOTER = "@Copyright 2017 GSD Tech, Version 3.0";
	private static final Logger logger = Logger.getLogger(SitesView.class.getName());

	/*
	 * After UI class is created, init() is executed. You should build and wire
	 * up your user interface here.
	 */
	public SitesView() {
		Page.getCurrent().setTitle("Site Management System");
		sitesDao = new SitesDaoImpl();
		setSizeFull();
		initLayout();
		initSitesList();
		initSearch();

	}

	private void initLayout() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull(); // to ensure whole space is in use
		setCompositionRoot(mainLayout);
		mainLayout.setStyleName(Runo.LAYOUT_DARKER);
		header = getHeaderPanel();
		mainLayout.addComponent(header);
		/* Root of the user interface component tree is set */
		VerticalLayout mainPanel = new VerticalLayout();
		mainPanel.setMargin(true);
		mainPanel.setSizeFull();
		mainPanel.setSpacing(true);
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		splitPanel.addStyleName(Runo.SPLITPANEL_SMALL);
		mainPanel.addComponent(splitPanel);
		splitPanel.setSplitPosition(25, Sizeable.Unit.PERCENTAGE);
		splitPanel.setSizeFull();
		splitPanel.addComponent(getLeftLayout());
		// Set cell style generator
		/*
		 * In the bottomLeftLayout, searchField takes all the width there is
		 * after adding addNewContactButton. The height of the layout is defined
		 * by the tallest component.
		 */
		VerticalLayout l = new VerticalLayout();
		l.addComponent(getReportChooserPanel());
		l.addComponent(getRightPanel());
		splitPanel.addComponent(l);
		mainPanel.setHeight("100%");
		mainLayout.addComponent(mainPanel);
		footer = getFooterPanel();
		mainLayout.addComponent(footer);
		mainLayout.setExpandRatio(mainPanel, 8.75f);
		mainLayout.setExpandRatio(footer, 0.50f);
		mainLayout.setExpandRatio(header, 0.75f);
		fuelSalesTable.setVisible(false);
		fuelInventoryTable.setVisible(false);
		paymentsTable.setVisible(false);
		carwashTable.setVisible(false);
		discountTable.setVisible(false);
	}

	private Layout getLeftLayout() {
		VerticalLayout leftLayout = new VerticalLayout();
		leftLayout.addComponent(searchField);
		searchField.setSizeFull();
		leftLayout.setExpandRatio(searchField, 0.50f);
		leftLayout.addComponent(sitesList);
		leftLayout.setSizeFull();
		leftLayout.setExpandRatio(sitesList, 9.50f);
		sitesList.setSizeFull();
		return leftLayout;
	}

	private void initSearch() {
		/*
		 * We want to show a subtle prompt in the search field. We could also
		 * set a caption that would be shown above the field or description to
		 * be shown in a tooltip.
		 */
		searchField.setInputPrompt("Search sites");
		/*
		 * Granularity for sending events over the wire can be controlled. By
		 * default simple changes like writing a text in TextField are sent to
		 * server with the next Ajax call. You can set your component to be
		 * immediate to send the changes to server immediately after focus
		 * leaves the field. Here we choose to send the text over the wire as
		 * soon as user stops writing for a moment.
		 */
		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
		/*
		 * When the event happens, we handle it in the anonymous inner class.
		 * You may choose to use separate controllers (in MVC) or presenters (in
		 * MVP) instead. In the end, the preferred application architecture is
		 * up to you.
		 */
		searchField.addTextChangeListener(new TextChangeListener() {
			public void textChange(final TextChangeEvent event) {
				sitesContainer.removeAllContainerFilters();
				sitesContainer.addContainerFilter(new SitesFilter(event
						.getText()));
				selectedSite = null;
				selectedShift = null;
				selectedSiteId = null;
				clear();
			}
		});
	}

	/*
	 * A custom filter for searching names and companies in the
	 * contactContainer.
	 */
	private class SitesFilter implements Filter {
		private String needle;

		public SitesFilter(String needle) {
			this.needle = needle.toLowerCase();
		}

		public boolean passesFilter(Object itemId, Item item) {
			String haystack = ("" + item.getItemProperty(NAME).getValue()).toLowerCase();
			return haystack.contains(needle);
		}

		public boolean appliesToProperty(Object id) {
			return true;
		}
	}

	private void initSitesList() {
		sitesContainer = createSitesContainer();
		sitesList.setContainerDataSource(sitesContainer);
		CellStyleGenerator cellStyleGenerator = new CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				Item item = source.getItem(itemId);
				boolean isCurrent = (Boolean) item.getItemProperty(IS_CURRENT).getValue();
				if ((propertyId != null) && (((String) propertyId).equalsIgnoreCase(NAME))) {
					if (!isCurrent) {
						return "backlogged";
					}
				}
				return "current";
			}
		};
		sitesList.setCellStyleGenerator(cellStyleGenerator);
		sitesList.setVisibleColumns(fieldNames);
		sitesList.setSelectable(true);
		sitesList.setImmediate(true);
		sitesList.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object siteId = sitesList.getValue();
				Item item = sitesList.getItem(siteId);
				if (siteId != null) {
					selectedSiteId = (Long) item.getItemProperty(SITE_ID).getValue();
				}
				selectedDate = new DateTime().minusDays(1);
				updateReport();
			}
		});
	}

	private Panel getDatePanel() {
		Panel p1 = new Panel();
		// p1.addStyleName(Runo.PANEL_LIGHT);
		final InlineDateField d1 = new InlineDateField();
		d1.setWidth(0.5f, Unit.PERCENTAGE);
		// d1.setTimeZone(TimeZone.getDefault());
		d1.setLocale(Locale.US);
		d1.setResolution(Resolution.DAY);
		if (selectedDate != null) {
			d1.setValue(selectedDate.toDate());
		} else {
			final DateTime now = new DateTime();
			d1.setValue(now.minusDays(1).toDate());
		}
		d1.addValueChangeListener(getDateChangeListener(d1));
		d1.setImmediate(true);
		p1.setContent(d1);
		return p1;
	}

	private ValueChangeListener getDateChangeListener(final InlineDateField d1) {
		return new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				final String valueString = String.valueOf(event.getProperty()
						.getValue());
				Notification.show("Value changed:", valueString,
						Type.TRAY_NOTIFICATION);
				selectedDate = new DateTime((Date) d1.getValue());
				updateReport();
			}
		};
	}

	private void updateReport() {
		clear();
		final DateTime now = new DateTime();
		if (selectedDate.isAfter(now)) {
			Notification.show(String.format(
					"This is a future date."),
					Type.HUMANIZED_MESSAGE);
			return;
		}
		if (selectedSiteId == null) {
			return ;
		}
		ShiftReport s = sitesDao.getShift(selectedSiteId, selectedDate.toDate());
		if (s == null) {
			Notification.show("No shift report was found for " + selectedDate.toString("MM/dd/yy"),
					Type.HUMANIZED_MESSAGE);
		} else {
			selectedShift = s;
			populateTables(s);
			populateSiteInfo();
			populateShiftInfo();
			// populateTotalInfo();
		}
	}

	protected void clear() {
		clearFuelSalesTable();
		clearFuelInventoryTable();
		clearPaymentsTable();
		clearCarwashTable();
		clearDiscountTable();
		selectedShift = null;
		populateSiteInfo();
		populateShiftInfo();
	}

	private Panel getRightPanel() {
		Panel p = new Panel();
		p.setSizeFull();
		// p.addStyleName(Runo.PANEL_LIGHT);
		final VerticalLayout mainlayout = new VerticalLayout();
		p.setContent(mainlayout);
		mainlayout.setSpacing(true);
		mainlayout.setMargin(true);
		// totalPanel = new Panel();
		// totalPanel.setSizeFull();
		// totalPanel.setVisible(false);
		// rightLayout.addComponent(totalPanel);
		shiftInfoPanel = getShiftInfoPanel();
		mainlayout.addComponent(shiftInfoPanel);
		shiftInfoPanel.setSizeFull();
		fuelSalesTable = new Table() {
			@Override
			public Align getColumnAlignment(Object propertyId) {
				if ((propertyId != null)
						&&
						((VOLUME.equalsIgnoreCase(propertyId.toString())) || (SALES.equalsIgnoreCase(propertyId
								.toString())))) {
					return Table.Align.RIGHT;
				}
				if ((propertyId != null) && (PERCENT_OF_TOTAL_SALES.equalsIgnoreCase(propertyId.toString()))) {
					return Table.Align.CENTER;
				}
				return super.getColumnAlignment(propertyId);
			}
		};
		fuelSalesTable.setCaption("Fuel Sales");
		// fuelSalesTable.addStyleName(Runo.TABLE_BORDERLESS);
		IndexedContainer fuelSalesDS = createFuelSalesContainer();
		fuelSalesTable.setContainerDataSource(fuelSalesDS);
		CellStyleGenerator cellStyleGenerator = new CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				Item item = source.getItem(itemId);
				if (item.getItemProperty(GRADE_NAME).getValue() == null) {
					return "bold";
				}
				return null;
			}
		};
		fuelSalesTable.setCellStyleGenerator(cellStyleGenerator);
		mainlayout.addComponent(fuelSalesTable);
		fuelSalesTable.setSizeFull();
		fuelSalesTable.setSortEnabled(false);
		fuelSalesTable.setColumnReorderingAllowed(false);
		fuelSalesTable.setColumnCollapsingAllowed(false);
		HorizontalLayout secondRow = new HorizontalLayout();
		VerticalLayout paymentsLayout = new VerticalLayout();

		IndexedContainer paymentDS = createPaymentContainer();
		paymentsTable = new Table() {
			@Override
			public Align getColumnAlignment(Object propertyId) {
				if ((propertyId != null) &&
						((AMOUNT.equalsIgnoreCase(propertyId.toString())))) {
					return Table.Align.RIGHT;
				}
				return super.getColumnAlignment(propertyId);
			}
		};
		cellStyleGenerator = new CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				Item item = source.getItem(itemId);
				Object c = item.getItemProperty(METHOD_OF_PAYMENT).getValue();
				if ( (c!= null) && (c.toString().toLowerCase().indexOf("cash") != -1)) {
					return "bold";
				}
				return null;
			}
		};
		paymentsTable.setCellStyleGenerator(cellStyleGenerator);
		paymentsTable.setCaption("Payments");
		// paymentsTable.addStyleName(Runo.TABLE_BORDERLESS);
		paymentsTable.setContainerDataSource(paymentDS);
		paymentsTable.setWidth("95%");
		paymentsTable.setSizeFull();
		paymentsTable.setSortEnabled(false);
		paymentsTable.setColumnReorderingAllowed(false);
		paymentsLayout.addComponent(paymentsTable);
		paymentsLayout.setComponentAlignment(paymentsTable, Alignment.TOP_LEFT);
		paymentsLayout.setSizeFull();
		
		secondRow.addComponent(paymentsLayout);
		secondRow.setComponentAlignment(paymentsLayout, Alignment.TOP_LEFT);
		secondRow.setExpandRatio(paymentsLayout, 1.0f);

		VerticalLayout gradeLayout = new VerticalLayout();
		gradeLayout.setSpacing(true);
//		gradeLayout.setMargin(true);
		gradeLayout.setWidth("95%");
		IndexedContainer fuelInventoryDS = this.createFuelInventoryContainer();
		this.fuelInventoryTable = new Table();
//		fuelInventoryTable.setCellStyleGenerator(cellStyleGenerator);
		fuelInventoryTable.setCaption("Fuel Inventory");
		fuelInventoryTable.setContainerDataSource(fuelInventoryDS);
		fuelInventoryTable.setWidth("100%");
		fuelInventoryTable.setSortEnabled(false);
		fuelInventoryTable.setColumnReorderingAllowed(false);
		

		gradeLayout.addComponent(fuelInventoryTable);
		gradeLayout.setComponentAlignment(fuelInventoryTable, Alignment.TOP_RIGHT);

		IndexedContainer discountsDS = this.createDiscountsContainer();
		this.discountTable = new Table();
//		discountTable.setCellStyleGenerator(cellStyleGenerator);
		discountTable.setCaption("Discounts");
		discountTable.setContainerDataSource(discountsDS);
		discountTable.setWidth("100%");
		discountTable.setSortEnabled(false);
		discountTable.setColumnReorderingAllowed(false);
		
		gradeLayout.addComponent(discountTable);
		gradeLayout.setComponentAlignment(discountTable, Alignment.BOTTOM_RIGHT);
		gradeLayout.setSizeFull();
		secondRow.setSpacing(true);
//		secondRow.setMargin(true);

		secondRow.addComponent(gradeLayout);
		secondRow.setExpandRatio(gradeLayout, 1.0f);
		secondRow.setComponentAlignment(gradeLayout, Alignment.TOP_RIGHT);
		
		
		secondRow.setWidth("100%");
		mainlayout.addComponent(secondRow);

		carwashTable = new Table() {
			@Override
			public Align getColumnAlignment(Object propertyId) {
				if ((propertyId != null)
						&&
						((VOLUME.equalsIgnoreCase(propertyId.toString())) || (SALES.equalsIgnoreCase(propertyId
								.toString())))) {
					return Table.Align.RIGHT;
				}
				if ((propertyId != null) && (PERCENT_OF_TOTAL_SALES.equalsIgnoreCase(propertyId.toString()))) {
					return Table.Align.CENTER;
				}
				return super.getColumnAlignment(propertyId);
			}
		};
		carwashTable.setCaption("Carwash Sales");

		// fuelSalesTable.addStyleName(Runo.TABLE_BORDERLESS);
		IndexedContainer carwashDS = createCarwashContainer();
		carwashTable.setContainerDataSource(carwashDS);
		mainlayout.addComponent(carwashTable);
		carwashTable.setSizeFull();
		carwashTable.setSortEnabled(false);
		carwashTable.setColumnReorderingAllowed(false);

		
		return p;
	}

	private IndexedContainer createDiscountsContainer() {
		logger.debug("Created Discount container");
		IndexedContainer ic = new IndexedContainer();
		for (String p : allDiscountTableColumns) {
			ic.addContainerProperty(p, String.class, null);
		}
		return ic;
	}

	private Panel getShiftInfoPanel() {
		Panel p = new Panel();
		p.setSizeUndefined();
		p.addStyleName(Runo.PANEL_LIGHT);
		HorizontalLayout g = new HorizontalLayout();
		g.setSizeFull();
		g.setSpacing(true);
		if (selectedShift != null) {
			String shiftStart = "";
			String shiftEnd = "";
			shiftStart = " From Date "
					+ new DateTime(selectedShift.getStartTime()).toString("YYYY-MM-dd hh:mm:ss a");
			shiftEnd = " To Date " + new DateTime(selectedShift.getEndTime()).toString("YYYY-MM-dd hh:mm:ss a");
			Label shiftInfoLabel = new Label(shiftStart + shiftEnd);
			shiftInfoLabel.setSizeFull();
			shiftInfoLabel.addStyleName("bold");
			g.addComponent(shiftInfoLabel);
			Label shiftInfoLabel1 = new Label("&nbsp;", ContentMode.HTML);
			g.addComponent(shiftInfoLabel1);
			shiftInfoLabel1 = new Label("&nbsp;", ContentMode.HTML);
			g.addComponent(shiftInfoLabel1);
			// final Label totalLabel = new Label("Total Sales " +
			// asMoney(selectedShift.getGrandTotal(), 2));
			// totalLabel.setStyleName("bolder");
			// l.addComponent(totalLabel);
			Button print = new Button("Save Report");
			print.addStyleName(Runo.BUTTON_DEFAULT);
			print.addClickListener(getReportListener());
			g.addComponent(print);
			print.setVisible(true);
		}
		p.setContent(g);
		return p;
	}

	private Panel getInfoPanel() {
		Panel p = new Panel();
		p.addStyleName(Runo.PANEL_LIGHT);
		p.setSizeFull();
		final VerticalLayout l = new VerticalLayout();
		if (this.selectedSiteId != null) {
			selectedSite = sitesDao.getSite(this.selectedSiteId);
			if (selectedSite != null) {
				String address = selectedSite.getAddress();
				// l.setMargin(true);
				Label heading = new Label("Site Info");
				heading.addStyleName("bold-red");
				l.addComponent(heading);
				String name = selectedSite.getName() + " #" + selectedSite.getStoreNumber();
				Label nameLabel = new Label(name);
				nameLabel.addStyleName("bold");
				l.addComponent(nameLabel);
				Label addressLabel = new Label(address);
				addressLabel.addStyleName("bold");
				l.addComponent(addressLabel);
				Label storeNumberLabel = new Label("Store  # : " + selectedSite.getStoreNumber());
				storeNumberLabel.setSizeUndefined();
				storeNumberLabel.addStyleName("bold");
				l.addComponent(storeNumberLabel);
				boolean isCurrent = isCurrent(selectedSite);
				String collectionStatus = isCurrent ? "Current" : "BackLogged";
				String reason = ((selectedSite.getReason() == null) || (selectedSite.getReason().isEmpty())) ? "" : " [" + selectedSite.getReason() + "]";
				String status = collectionStatus + reason;
				int wrapAt = 80;
				if (status.length() > wrapAt) {
					int loop = status.length() / wrapAt;
					int endPoint = wrapAt;
					for (int i = 0; i < loop + 1; i++) {
						Label st = new Label(status.substring(i * wrapAt,
								(endPoint > status.length()) ? status.length()
										: endPoint));
						if (!isCurrent) {
							st.addStyleName("backlogged");
						} else {
							st.addStyleName("bold");
						}
						st.setSizeUndefined();
						l.addComponent(st);
						endPoint += wrapAt;
					}
				} else {
					Label st = new Label(status);
					if (!isCurrent) {
						st.addStyleName("backlogged");
					} else {
						st.addStyleName("bold");
					}
					st.setSizeUndefined();
					l.addComponent(st);
				}
				if (!isCurrent) {
					if (selectedSite.getLastCollectedDate() != null) {
						Label st = new Label("Last Collected at ["
								+ new DateTime(selectedSite.getLastCollectedDate()).toString("YYYY-MM-dd") + "]");
						st.setSizeUndefined();
						st.addStyleName("backlogged");
						l.addComponent(st);
					}
				}
				l.addComponent(new Label("&nbsp;", ContentMode.HTML));
			}
			/*
			 * ComboBox combobox = new ComboBox("Select Report");
			 * combobox.setInvalidAllowed(false);
			 * combobox.setNullSelectionAllowed(false);
			 * combobox.addItem("Shift Close");
			 * combobox.addItem("EPA Compliance");
			 * combobox.addValueChangeListener(new ValueChangeListener() {
			 * 
			 * @Override public void valueChange(ValueChangeEvent event) {
			 * Notification.show("Selected "); } }); l.addComponent(combobox);
			 */
		}
		p.setContent(l);
		return p;
	}

	private ClickListener getReportListener() {
		ClickListener c = new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (selectedShift == null) {
					Notification.show(String.format(
							"No shift report was found for %tm/%td/%ty %n",
							selectedDate, selectedDate, selectedDate),
							Type.HUMANIZED_MESSAGE);
				} else {
					Site s = sitesDao.getSite(selectedShift.getSiteId());
					selectedShift.setStoreName(s.getName());
					selectedShift.setStoreNumber(s.getStoreNumber());
					StreamSource source = new ShiftReportGenerator(selectedShift);
					String filename = "shift_report-"
							+ new DateTime(selectedShift.getEndTime()).toString("yyyy-MM-dd-hh-mm-ss")
							+ ".pdf";
					StreamResource resource = new StreamResource(source, filename);
					// These settings are not usually necessary. MIME type
					// is detected automatically from the file name, but
					// setting it explicitly may be necessary if the file
					// suffix is not ".pdf".
					resource.setMIMEType("application/pdf");
					resource.getStream().setParameter("Content-Disposition",
							"attachment; filename=" + filename);
					// Open it in this window - this will either launch
					// PDF viewer or let the user download the file. Could
					// use "_blank" target to open in another window, but
					// may not be necessary.
					Page.getCurrent().open(resource, "report", true);
				}
			}
		};
		return c;
	}

	private void populateSiteInfo() {
		siteInfoPanel.setContent(getInfoPanel());
		siteInfoPanel.setVisible(true);
	}

	private void populateShiftInfo() {
		shiftInfoPanel.setContent(getShiftInfoPanel());
		shiftInfoPanel.setVisible(true);
	}

	private Panel getReportChooserPanel() {
		Panel p = new Panel();
		// p.addStyleName(Runo.PANEL_LIGHT);
		final HorizontalLayout h = new HorizontalLayout();
		h.setSizeFull();
		h.setSpacing(true);
		h.setMargin(true);
		siteInfoPanel = getInfoPanel();
		h.addComponent(siteInfoPanel);
		h.setComponentAlignment(siteInfoPanel, Alignment.TOP_LEFT);
		h.setExpandRatio(siteInfoPanel, 2f);
		VerticalLayout l1 = new VerticalLayout();
		Panel datePanel = getDatePanel();
		datePanel.setSizeUndefined();
		l1.addComponent(datePanel);
		h.addComponent(l1);
		h.setComponentAlignment(l1, Alignment.TOP_RIGHT);
		h.setExpandRatio(l1, 1f);
		p.setContent(h);
		return p;
	}

	private void populateTotalInfo() {
		final HorizontalLayout h = new HorizontalLayout();
		h.setSizeFull();
		Label l = new Label("Total Sales " + asMoney(selectedShift.getTotals().getTotalFuelSales(), 2));
		h.addComponent(l);
		totalPanel.setContent(h);
		totalPanel.setVisible(true);
	}

	private Panel getHeaderPanel() {
		Panel header = new Panel();
		// header.addStyleName(Runo.PANEL_LIGHT);
		HorizontalLayout l = new HorizontalLayout();
//		Button b1 = new Button("");
//		l.addComponent(b1);
//		l.setComponentAlignment(b1,Alignment.TOP_LEFT);
		l.setSpacing(true);
		l.setSizeFull();
//		b1.setVisible(false);
		
		Label lb = new Label(
				"<div align=\"center\" style=\"font-family:arial;color:red\"><h1 style=\"margin-bottom:0;font-family:arial;color:red\">Capitol Petroleum Group Site Management System</h1><div>",
				ContentMode.HTML);
		l.setMargin(false);
		l.addComponent(lb);
		l.setComponentAlignment(lb,Alignment.TOP_CENTER);
//		l.setWidth("100%");
		
		Button b = new Button("Logout");
		b.addStyleName(Runo.BUTTON_DEFAULT);
	
		b.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				Notification.show("Logout button was clicked",
						Type.TRAY_NOTIFICATION);
				getUI().getSession().close();
				getUI().getPage().setLocation(SimpleLoginView.NAME);
			}
		});
		l.addComponent(b);
		l.setComponentAlignment(b,Alignment.TOP_RIGHT);
//		b1.setSizeFull();
		lb.setWidth("100%");
//		lb.setSizeFull();
//		b.setWidth("10%");
		l.setExpandRatio(lb, 1.0f);

		header.setContent(l);
		return header;
	}

	private Panel getFooterPanel() {
		Panel footer = new Panel();
		footer.addStyleName(Runo.PANEL_LIGHT);
		HorizontalLayout l = new HorizontalLayout();
		l.setSizeFull();
		Label lb = new Label();
		lb.setSizeFull();
		l.addComponent(lb);
		l.setComponentAlignment(lb, Alignment.BOTTOM_LEFT);
		lb = new Label(FOOTER );
		lb.addStyleName("bold");
		lb.setSizeFull();
		l.addComponent(lb);
		l.setComponentAlignment(lb, Alignment.BOTTOM_RIGHT);
		footer.setContent(l);
		return footer;
	}

	private void populateTables(ShiftReport s) {
		logger.debug("Populating information for shift " + s.getShiftId());
		populateFuelSalesTable(s);
		fuelSalesTable.setVisibleColumns(allFuelTableColumns);
		fuelSalesTable.setPageLength(fuelSalesTable.size() + 1);
		fuelSalesTable.setImmediate(true);
//		logger.debug("Carwash sales is Enabled [" + selectedSite.isCarwashEnabled() + "]");

		if ((s.getCarwashSales() != null) && (!s.getCarwashSales().isEmpty())) {
			carwashTable.setVisible(true);
			populateCarwashTable(s);
			carwashTable.setVisibleColumns(allCarwashTableColumns);
			carwashTable.setPageLength(carwashTable.size() + 1);
			carwashTable.setImmediate(true);
		} else {
			logger.debug("No carwash sales found");
			carwashTable.setVisible(false);
		}
//		logger.debug("Fuel Inventory is Enabled [" + selectedSite.isFuelInventoryEnabled() + "]");

		if ( (s.getFuelInventory() != null) && (!s.getFuelInventory().isEmpty())) {
			fuelInventoryTable.setVisible(true);
			populateFuelInventoryTable(s);
			fuelInventoryTable.setVisibleColumns(allFuelInventoryTableColumns);
			fuelInventoryTable.setPageLength(fuelInventoryTable.size() + 1);
			fuelInventoryTable.setImmediate(true);
		} else {
			logger.debug("No fuel inventory  found");
			fuelInventoryTable.setVisible(false);
		}
		if ((s.getDiscounts() != null) && (!s.getDiscounts().isEmpty())) {
			discountTable.setVisible(true);
			populateDiscountTable(s);
			discountTable.setVisibleColumns(allDiscountTableColumns);
			discountTable.setPageLength(discountTable.size() + 1);
			discountTable.setImmediate(true);
		} else {
			logger.debug("No Discounts found");
			discountTable.setVisible(false);
		}
		paymentsTable.setWidth("100%");
		populatePaymentsTable(s);
		paymentsTable.setVisibleColumns(allPaymentTableColumns);
		paymentsTable.setPageLength(paymentsTable.size() + 1);
		paymentsTable.setImmediate(true);

	}

	private IndexedContainer createSitesContainer() {
		IndexedContainer ic = new IndexedContainer();
		for (String p : fieldNames) {
			ic.addContainerProperty(p, String.class, "");
		}
		ic.addContainerProperty(SITE_ID, Long.class, "");
		ic.addContainerProperty(IS_CURRENT, Boolean.class, null);
		return ic;
	}
	

	private IndexedContainer createFuelSalesContainer() {
		logger.debug("Created Fuel sales container");
		IndexedContainer ic = new IndexedContainer();
		for (String p : allFuelTableColumns) {
			ic.addContainerProperty(p, String.class, null);
		}
//		ic.addContainerProperty(BOLD, Boolean.class, new Boolean(false));
		return ic;
	}

	private IndexedContainer createFuelInventoryContainer() {
		logger.debug("Created Fuel Inventory container");
		IndexedContainer ic = new IndexedContainer();
		for (String p : allFuelInventoryTableColumns) {
			ic.addContainerProperty(p, String.class, null);
		}
		return ic;
	}

	
	
	private void populateFuelSalesTable(ShiftReport report) {
		this.fuelSalesTable.removeAllItems();
		List<FuelSales> sales = report.getFuelSales();
		BigDecimal total = new BigDecimal(0);
		BigDecimal totalVolume = new BigDecimal(0);
		for (FuelSales s : sales) {
			total = total.add(s.getSales());
			totalVolume = totalVolume.add(s.getVolume());
		}
		for (FuelSales s : sales) {
			Object id = fuelSalesTable.addItem();
			fuelSalesTable.getContainerProperty(id, GRADE).setValue(
					s.getGrade());
			fuelSalesTable.getContainerProperty(id, GRADE_NAME).setValue(
					s.getGradeName());
			fuelSalesTable.getContainerProperty(id, VOLUME).setValue(asString(s.getVolume(), 3));
			fuelSalesTable.getContainerProperty(id, SALES).setValue(asMoney(s.getSales(), 2));
			BigDecimal a = null;
			try {
				a = s.getSales().divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
			} catch (Exception e) {
				e.printStackTrace();
			}
			fuelSalesTable.getContainerProperty(id, PERCENT_OF_TOTAL_SALES)
					.setValue((a != null) ? asString(a, 2) + "%" : "");
		}
		Object id = fuelSalesTable.addItem();
		fuelSalesTable.getContainerProperty(id, GRADE).setValue("Total Fuel Sales");
		fuelSalesTable.getContainerProperty(id, VOLUME).setValue(asString(totalVolume, 3));
		fuelSalesTable.getContainerProperty(id, SALES).setValue(asMoney(report.getTotals().getTotalFuelSales(), 2));
//		fuelSalesTable.getContainerProperty(id, BOLD).setValue(new Boolean(true));
		id = fuelSalesTable.addItem();
		fuelSalesTable.getContainerProperty(id, GRADE).setValue("Fuel Discounts");
		fuelSalesTable.getContainerProperty(id, SALES).setValue(asMoney(report.getTotals().getFuelDiscounts(), 2));
//		fuelSalesTable.getContainerProperty(id, BOLD).setValue(new Boolean(true));
		id = fuelSalesTable.addItem();
		fuelSalesTable.getContainerProperty(id, GRADE).setValue("Total Non Fuel Sales");
		fuelSalesTable.getContainerProperty(id, SALES).setValue(asMoney(report.getTotals().getTotalDeptSales(), 2));
//		fuelSalesTable.getContainerProperty(id, BOLD).setValue(new Boolean(true));
		id = fuelSalesTable.addItem();
		fuelSalesTable.getContainerProperty(id, GRADE).setValue("Other Discounts");
		fuelSalesTable.getContainerProperty(id, SALES).setValue(asMoney(report.getTotals().getOtherDiscounts(), 2));
//		fuelSalesTable.getContainerProperty(id, BOLD).setValue(new Boolean(true));
		id = fuelSalesTable.addItem();
		fuelSalesTable.getContainerProperty(id, GRADE).setValue("Total Taxes Collected");
		fuelSalesTable.getContainerProperty(id, SALES).setValue(asMoney(report.getTotals().getTotalTax(), 2));
//		fuelSalesTable.getContainerProperty(id, BOLD).setValue(new Boolean(true));
		fuelSalesTable.setVisible(true);
	}

	private void clearFuelSalesTable() {
		this.fuelSalesTable.removeAllItems();
		fuelSalesTable.setVisible(false);
	}


	
	private void populateFuelInventoryTable(ShiftReport report) {
		this.fuelInventoryTable.removeAllItems();
		if ((report.getFuelInventory() == null) || (report.getFuelInventory().isEmpty())) {
			return;
		}
		List<FuelInventory> sales = report.getFuelInventory();
		for (FuelInventory s : sales) {
			Object id = fuelInventoryTable.addItem();
			fuelInventoryTable.getContainerProperty(id, GRADE_NAME).setValue(
					s.getGradeName());
			fuelInventoryTable.getContainerProperty(id, TANK_ID).setValue(s.getTankId() + "");
			fuelInventoryTable.getContainerProperty(id, VOLUME).setValue(asString(s.getVolume(), 3));
		}
		fuelInventoryTable.setVisible(true);
	}

	private void clearFuelInventoryTable() {
		this.fuelInventoryTable.removeAllItems();
		fuelInventoryTable.setVisible(false);
	}

	
	private IndexedContainer createCarwashContainer() {
		IndexedContainer ic = new IndexedContainer();
		for (String p : allCarwashTableColumns) {
			ic.addContainerProperty(p, String.class, null);
		}
		return ic;
	}

	private void populateCarwashTable(ShiftReport report) {
		this.carwashTable.removeAllItems();
		List<CarwashSales> sales = report.getCarwashSales();
		for (CarwashSales s : sales) {
			Object id = carwashTable.addItem();
			carwashTable.getContainerProperty(id, GROSS_SALES).setValue(
					asMoney(s.getGrossSales(), 2));
			carwashTable.getContainerProperty(id, ITEM_COUNT).setValue(
					s.getItemCount() + "");

			carwashTable.getContainerProperty(id, REFUND_COUNT).setValue(
					s.getRefundCount() + "");
			carwashTable.getContainerProperty(id, NET_COUNT).setValue(
					s.getNetCount() + "");
			carwashTable.getContainerProperty(id, REFUND).setValue(asMoney(s.getRefund(), 2));
			carwashTable.getContainerProperty(id, DISCOUNT).setValue(asMoney(s.getDiscount(), 2));
			carwashTable.getContainerProperty(id, NET_SALES).setValue(asMoney(s.getNetSales(), 2));

		}
		carwashTable.setVisible(true);
	}

	private void clearDiscountTable() {
		this.discountTable.removeAllItems();
		discountTable.setVisible(false);
	}

	private void populateDiscountTable(ShiftReport report) {
		this.discountTable.removeAllItems();
		List<Discount> sales = report.getDiscounts();
		for (Discount s : sales) {
			Object id = discountTable.addItem();
			discountTable.getContainerProperty(id, GRADE_NAME).setValue(s.getGrade());
			discountTable.getContainerProperty(id, COUNT).setValue(
					s.getCount() + "");

			discountTable.getContainerProperty(id, AMOUNT).setValue(asMoney(s.getAmount(), 2));

		}
		discountTable.setVisible(true);
	}

	private void clearCarwashTable() {
		this.carwashTable.removeAllItems();
		carwashTable.setVisible(false);
	}
	
	
	
	private void clearPaymentsTable() {
		this.paymentsTable.removeAllItems();
		paymentsTable.setVisible(false);
	}

	private IndexedContainer createPaymentContainer() {
		IndexedContainer ic = new IndexedContainer();
		ic.addContainerProperty(METHOD_OF_PAYMENT, String.class, null);
		ic.addContainerProperty(AMOUNT, String.class, null);
//		ic.addContainerProperty(BOLD, Boolean.class, new Boolean(false));
	return ic;
	}

	private void populatePaymentsTable(ShiftReport sr) {
		this.paymentsTable.removeAllItems();
		List<Payment> payments = sr.getPayments();
		if ((sr.getPayments() == null) || (sr.getPayments().isEmpty())) {
			return;
		}
		BigDecimal nonCash = new BigDecimal(0);
		BigDecimal loyalty = new BigDecimal(0);

		for (Payment s : payments) {
			if (!"loyalty".equalsIgnoreCase(s.getType())) {
				Object id = paymentsTable.addItem();
				paymentsTable.getContainerProperty(id, METHOD_OF_PAYMENT).setValue(
						s.getType());
				paymentsTable.getContainerProperty(id, AMOUNT).setValue(asMoney(s.getAmount(), 2));
				
			}
			if (!"cash".equalsIgnoreCase(s.getType()) && !"loyalty".equalsIgnoreCase(s.getType())) {
				nonCash = nonCash.add(s.getAmount());
			} else if ("loyalty".equalsIgnoreCase(s.getType())) {
				loyalty = loyalty.add(s.getAmount());
			}
		}
		
		Object id = paymentsTable.addItem();
		paymentsTable.getContainerProperty(id, METHOD_OF_PAYMENT).setValue("Non Cash Total");
		paymentsTable.getContainerProperty(id, AMOUNT).setValue(asMoney(nonCash, 2));
//		paymentsTable.getContainerProperty(id, BOLD).setValue(new Boolean(true));
		id = paymentsTable.addItem();
		paymentsTable.getContainerProperty(id, METHOD_OF_PAYMENT).setValue("Loyalty");
		paymentsTable.getContainerProperty(id, AMOUNT).setValue(asMoney(loyalty, 2));
		id = paymentsTable.addItem();
		paymentsTable.getContainerProperty(id, METHOD_OF_PAYMENT).setValue("Non Cash Total adjusted for Loyalty");
		paymentsTable.getContainerProperty(id, AMOUNT).setValue(asMoney(nonCash.add(loyalty), 2));

		paymentsTable.setVisible(true);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		System.out.println("In sites view");	
		if (getSession() != null) {
			User u = (User) getSession().getAttribute("user");
			System.out.println("User " + u );
			if (u != null) {
				System.out.println("Got user [" + u.getUserId() + "/" + u.getUsername() + "]");

			}
		} else {
			System.out.println("Session is null");
			
		}
		List<Site> sites  = new ArrayList<Site>();
		if (getSession() != null) {
			User u = (User) getSession().getAttribute("user");
			if ( u != null) {
				sites = sitesDao.getActiveSites(u);
/*
				Collections.sort(sites, new Comparator<Site>() {

					@Override
					public int compare(Site o1, Site o2) {
						return o1.getName().compareTo(o2.getName());
					}});
*/
			}
			for (Site s : sites) {
				Object id = sitesContainer.addItem();
				sitesContainer.getContainerProperty(id, NAME).setValue(s.getStreet().toUpperCase());
				sitesContainer.getContainerProperty(id, SITE_ID).setValue(s.getSiteId());
				sitesContainer.getContainerProperty(id, IS_CURRENT).setValue(isCurrent(s));
			}
		}

	}

	private String asString(BigDecimal bd, int size) {
		if (bd == null) {
			String zeroes = "";
			for (int i = 0; i < size; i++) {
				zeroes += "0";
			}
			return "0." + zeroes;
		}
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(size);
		return df.format(bd);
	}

	private String asMoney(BigDecimal bd, int size) {
		return "$" + asString(bd, size);
	}

	private boolean isCurrent(Site s) {
		DateTime lastCollected = new DateTime(s.getLastCollectedDate());
		logger.trace("Last Collected Date is [" + lastCollected.toString("YYYY-MM-dd hh:mm:ss a") + "] for site [" + s.getName() + "]");
		if (s.getLastCollectedDate() == null) {
			return false;
		}
		Period p = new Period(  lastCollected , new DateTime());
		int days = p.getDays();
		int hours = p.getHours();
		logger.trace(String.format("Months/Weeks/Days/Hours behind %d/%d/%d/%d", p.getMonths() , p.getWeeks() ,p.getDays() , p.getHours()));
		if ((p.getMonths() > 0 ) || (p.getWeeks() > 0 ) || ( (days == 1  ) && hours > 4) || (days > 1) ) {
			return false;
		}
		return true;
	}
	

}
