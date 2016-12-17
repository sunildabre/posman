package com.gsd.pos.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class MyPdfSource implements StreamSource {
	private final ByteArrayOutputStream os = new ByteArrayOutputStream();
	private Document doc;

	public MyPdfSource(Document doc) {
		this.doc = doc;
		try {
			PdfWriter.getInstance(doc, os);
			doc.open();
			doc.add(new Paragraph("This is some content for the sample PDF!"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	@Override
	public InputStream getStream() {
		return new ByteArrayInputStream(os.toByteArray());
	}
}
