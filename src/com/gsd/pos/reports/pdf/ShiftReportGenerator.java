package com.gsd.pos.reports.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.gsd.pos.model.CarwashSales;
import com.gsd.pos.model.Discount;
import com.gsd.pos.model.FuelInventory;
import com.gsd.pos.model.FuelSales;
import com.gsd.pos.model.Payment;
import com.gsd.pos.model.ShiftReport;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.vaadin.server.StreamResource.StreamSource;

public class ShiftReportGenerator implements StreamSource {
	private final ByteArrayOutputStream os = new ByteArrayOutputStream();
	private static final Logger logger = Logger
			.getLogger(ShiftReportGenerator.class.getName());

	public ShiftReportGenerator(ShiftReport sr) {
		Document doc = null;
		try {
			Font font1 = new Font(FontFamily.HELVETICA, 8, Font.NORMAL,
					BaseColor.BLACK);
			Font font2 = new Font(FontFamily.HELVETICA, 10, Font.BOLD,
					BaseColor.BLACK);
			Font font3 = new Font(FontFamily.HELVETICA, 12, Font.BOLD,
					BaseColor.BLACK);
			doc = new Document(PageSize.B4);
			PdfWriter.getInstance(doc, os);
			doc.addHeader("Shift [" + sr.getStoreName() + "]", "");
			doc.addCreator("sms-client");
			doc.addAuthor("sms");
			doc.addTitle("Shift Report.");
			doc.open();
			Paragraph title = new Paragraph("Shift Close Report for site [ "
					+ sr.getStoreName() + ",  " + sr.getStoreNumber() + "]",
					font3);
			title.setAlignment(Element.ALIGN_CENTER);
			doc.add(title);
			title = new Paragraph(
					"From Date  [ "
							+ new DateTime(sr.getStartTime()).toString("YYYY-MM-dd hh:mm:ss")
							+ " ]", font2);
			title.setAlignment(Element.ALIGN_CENTER);
			doc.add(title);
			title = new Paragraph(
					"To Date  [ "
							+ new DateTime(sr.getEndTime()).toString("YYYY-MM-dd hh:mm:ss")
							+ " ]", font2);
			title.setAlignment(Element.ALIGN_CENTER);
			doc.add(title);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			// title = new Paragraph("Total Sales " +
			// asMoney(sr.getGrandTotal(), 2), font3);
			// title.setAlignment(Element.ALIGN_LEFT);
			// doc.add(title);
			// doc.add(Chunk.NEWLINE);
			doc.add(new LineSeparator());
			doc.add(Chunk.NEWLINE);
			title = new Paragraph("Fuel Sales", font2);
			title.setAlignment(Element.ALIGN_LEFT);
			doc.add(title);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			PdfPTable table = new PdfPTable(5);
			table.setHeaderRows(1);
			insertCell(table, "Grade", Element.ALIGN_LEFT, 1, font2);
			insertCell(table, "Grade Name", Element.ALIGN_LEFT, 1, font2);
			insertCell(table, "Volume", Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "Sales", Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "% Of Total Fuel Sales", Element.ALIGN_RIGHT, 1,
					font2);
			table.setWidthPercentage(100);
			List<FuelSales> sales = sr.getFuelSales();
			BigDecimal a = null;
			BigDecimal total = new BigDecimal(0);
			BigDecimal totalVolume = new BigDecimal(0);
			for (FuelSales s : sales) {
				total = total.add(s.getSales());
				totalVolume = totalVolume.add(s.getVolume());
			}
			for (FuelSales s : sales) {
				insertCell(table, s.getGrade(), Element.ALIGN_LEFT, 1, null);
				insertCell(table, s.getGradeName(), Element.ALIGN_LEFT, 1, null);
				insertCell(table, asString(s.getVolume(), 3),
						Element.ALIGN_RIGHT, 1, null);
				insertCell(table, asMoney(s.getSales(), 2),
						Element.ALIGN_RIGHT, 1, null);
				try {
					a = s.getSales().divide(total, 4, RoundingMode.HALF_UP)
							.multiply(new BigDecimal(100));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				insertCell(table, asString(a, 2) + "%", Element.ALIGN_RIGHT, 1,
						null);
			}
			insertCell(table, "", Element.ALIGN_LEFT, 5, font2);
			insertCell(table, "Total Fuel Sales", Element.ALIGN_LEFT, 2, font2);
			insertCell(table, asString(totalVolume, 3), Element.ALIGN_RIGHT, 1,
					font2);
			insertCell(table, asMoney(sr.getTotals().getTotalFuelSales(), 2),
					Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "", Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "Fuel Discounts", Element.ALIGN_LEFT, 3, font2);
			insertCell(table, asMoney(sr.getTotals().getFuelDiscounts(), 2),
					Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "", Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "Total Non Fuel Sales", Element.ALIGN_LEFT, 3,
					font2);
			insertCell(table, asMoney(sr.getTotals().getTotalDeptSales(), 2),
					Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "", Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "Other Discounts", Element.ALIGN_LEFT, 3, font2);
			insertCell(table, asMoney(sr.getTotals().getOtherDiscounts(), 2),
					Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "", Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "Total Taxes Collected", Element.ALIGN_LEFT, 3,
					font2);
			insertCell(table, asMoney(sr.getTotals().getTotalTax(), 2),
					Element.ALIGN_RIGHT, 1, font2);
			insertCell(table, "", Element.ALIGN_RIGHT, 1, font2);
			doc.add(table);
			doc.add(Chunk.NEWLINE);
			doc.add(new LineSeparator());
			doc.add(Chunk.NEWLINE);
			title = new Paragraph("Payments", font2);
			title.setAlignment(Element.ALIGN_LEFT);
			doc.add(title);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			table = new PdfPTable(2);
			table.setHeaderRows(1);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.setWidthPercentage(100);
			insertCell(table, "Type", Element.ALIGN_LEFT, 1, font2);
			insertCell(table, "Amount", Element.ALIGN_RIGHT, 1, font2);
			List<Payment> payments = sr.getPayments();
			BigDecimal nonCash = new BigDecimal(0);
			BigDecimal loyalty = new BigDecimal(0);

			for (Payment pay : payments) {
				if (!"loyalty".equalsIgnoreCase(pay.getType())) {
					insertCell(table, pay.getType(), Element.ALIGN_LEFT, 1, null);
					insertCell(table, asMoney(pay.getAmount(), 2),
							Element.ALIGN_RIGHT, 1, null);
					
				}

				if (!"cash".equalsIgnoreCase(pay.getType()) && !"loyalty".equalsIgnoreCase(pay.getType())) {
					nonCash = nonCash.add(pay.getAmount());
				}else if ("loyalty".equalsIgnoreCase(pay.getType())) {
					loyalty = loyalty.add(pay.getAmount());
				}

			}
			insertCell(table, "Non Cash Total", Element.ALIGN_LEFT, 1, font2);
			insertCell(table, asMoney(nonCash, 2), Element.ALIGN_RIGHT, 1,
					font2);
			insertCell(table, "Loyalty", Element.ALIGN_LEFT, 1, font2);
			insertCell(table, asMoney(loyalty, 2), Element.ALIGN_RIGHT, 1,
					font2);
			insertCell(table, "Non Cash Total adjusted for Loyalty", Element.ALIGN_LEFT, 1, font2);
			insertCell(table, asMoney(nonCash.add(loyalty), 2), Element.ALIGN_RIGHT, 1,
					font2);

			
			doc.add(table);
			doc.add(Chunk.NEWLINE);
			doc.add(new LineSeparator());
			doc.add(Chunk.NEWLINE);

			if ((sr.getFuelInventory() != null)
					&& (!sr.getFuelInventory().isEmpty())) {
				title = new Paragraph("Fuel Inventory", font2);
				title.setAlignment(Element.ALIGN_LEFT);
				doc.add(title);
				doc.add(Chunk.NEWLINE);
				doc.add(Chunk.NEWLINE);
				table = new PdfPTable(3);
				table.setHeaderRows(1);
				insertCell(table, "Grade Name", Element.ALIGN_LEFT, 1, font2);
				insertCell(table, "Tank Id", Element.ALIGN_LEFT, 1, font2);
				insertCell(table, "Volume", Element.ALIGN_RIGHT, 1, font2);
				table.setWidthPercentage(100);
				List<FuelInventory> inventory = sr.getFuelInventory();
				for (FuelInventory s : inventory) {
					insertCell(table, s.getGradeName(), Element.ALIGN_LEFT, 1,null);
					insertCell(table, s.getTankId() + "", Element.ALIGN_LEFT, 1,null);
					insertCell(table, asString(s.getVolume(), 3),
							Element.ALIGN_RIGHT, 1, null);
				}
				doc.add(table);
				doc.add(Chunk.NEWLINE);
				doc.add(new LineSeparator());
				doc.add(Chunk.NEWLINE);

			}
			if ((sr.getDiscounts() != null)
					&& (!sr.getDiscounts().isEmpty())) {
				title = new Paragraph("Discounts", font2);
				title.setAlignment(Element.ALIGN_LEFT);
				doc.add(title);
				doc.add(Chunk.NEWLINE);
				doc.add(Chunk.NEWLINE);
				table = new PdfPTable(3);
				table.setHeaderRows(1);
				insertCell(table, "Grade Name", Element.ALIGN_LEFT, 1, font2);
				insertCell(table, "Count", Element.ALIGN_LEFT, 1, font2);
				insertCell(table, "Amount", Element.ALIGN_RIGHT, 1, font2);
				table.setWidthPercentage(100);
				List<Discount> discounts = sr.getDiscounts();
				for (Discount s : discounts) {
					insertCell(table, s.getGrade(), Element.ALIGN_LEFT, 1,null);
					insertCell(table, s.getCount()+ "", Element.ALIGN_LEFT, 1,null);
					insertCell(table, asMoney(s.getAmount(), 2),
							Element.ALIGN_RIGHT, 1, null);
				}
				doc.add(table);
				doc.add(Chunk.NEWLINE);
				doc.add(new LineSeparator());
				doc.add(Chunk.NEWLINE);

			}

			if ((sr.getCarwashSales() != null)
					&& (!sr.getCarwashSales().isEmpty())) {
				title = new Paragraph("Car Wash Sales", font2);
				title.setAlignment(Element.ALIGN_LEFT);
				doc.add(title);
				doc.add(Chunk.NEWLINE);
				doc.add(Chunk.NEWLINE);
				table = new PdfPTable(7);
				table.setHeaderRows(1);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.setWidthPercentage(100);
				insertCell(table, "Gross Sales", Element.ALIGN_LEFT, 1, font2);
				insertCell(table, "Item Count", Element.ALIGN_RIGHT, 1, font2);
				insertCell(table, "Refund Count", Element.ALIGN_RIGHT, 1, font2);
				insertCell(table, "Net Count", Element.ALIGN_RIGHT, 1, font2);
				insertCell(table, "Refund", Element.ALIGN_RIGHT, 1, font2);
				insertCell(table, "Discount", Element.ALIGN_RIGHT, 1, font2);
				insertCell(table, "Net Sales", Element.ALIGN_RIGHT, 1, font2);
				List<CarwashSales> carwashSales = sr.getCarwashSales();
				for (CarwashSales pay : carwashSales) {
					insertCell(table, asMoney(pay.getGrossSales(), 2),
							Element.ALIGN_RIGHT, 1, null);
					insertCell(table, pay.getItemCount() + "",
							Element.ALIGN_RIGHT, 1, null);
					insertCell(table, pay.getRefundCount() + "",
							Element.ALIGN_RIGHT, 1, null);
					insertCell(table, pay.getNetCount() + "",
							Element.ALIGN_RIGHT, 1, null);
					insertCell(table, asMoney(pay.getRefund(), 2),
							Element.ALIGN_RIGHT, 1, null);
					insertCell(table, asMoney(pay.getDiscount(), 2),
							Element.ALIGN_RIGHT, 1, null);
					insertCell(table, asMoney(pay.getNetSales(), 2),
							Element.ALIGN_RIGHT, 1, null);
				}
				doc.add(table);
				doc.add(Chunk.NEWLINE);
				doc.add(new LineSeparator());
				doc.add(Chunk.NEWLINE);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	private void insertCell(PdfPTable table, String text, int align,
			int colspan, Font font) {
		if (text == null) {
			text = "";
		}
		PdfPCell cell = null;
		if (font == null) {
			cell = new PdfPCell(new Phrase(text.trim()));
		} else {
			cell = new PdfPCell(new Phrase(text.trim(), font));
		}
		cell.setHorizontalAlignment(align);
		cell.setColspan(colspan);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		cell.setBorderWidth(0);
		table.addCell(cell);
	}

	@Override
	public InputStream getStream() {
		return new ByteArrayInputStream(os.toByteArray());
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
}
