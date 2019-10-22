package com.fiit.sipvs.XmlZadanie.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import sk.ditec.zep.dsigner.xades.XadesSig;
import sk.ditec.zep.dsigner.xades.plugin.DataObject;
import sk.ditec.zep.dsigner.xades.plugins.xmlplugin.XmlPlugin;

public class XadesSigner extends AbstractTest {
	
	private String xml;
	private String xsd;
	private String xslt;
	private BorderPane bp;
	
	public XadesSigner(BorderPane bp, String xml, String xsd, String xslt) {
		
		this.bp=bp;
		this.xml = xml;
		this.xsd = xsd;
		this.xslt = xslt;
	}
	
	

	public void sign() throws IOException {
		XadesSig dSigner = new XadesSig();
		dSigner.installLookAndFeel();
		dSigner.installSwingLocalization();
		dSigner.reset();
		//dSigner.setLanguage("sk");
		
		 String DEFAULT_XSD_REF = "http://www.w3.org/2001/XMLSchema";
		 String DEFAULT_XSLT_REF = "http://www.w3.org/1999/XSL/Transform";
		

		XmlPlugin xmlPlugin = new XmlPlugin();
		DataObject xmlObject = xmlPlugin.createObject("XML1", "XML", readResource(xml),
				readResource(xsd),
				"", DEFAULT_XSD_REF,
				readResource(xslt), DEFAULT_XSLT_REF);

		if (xmlObject == null) {
			System.out.println("XMLPlugin.createObject() errorMessage=" + xmlPlugin.getErrorMessage());
			return;
		}

		int rc = dSigner.addObject(xmlObject);
		if (rc != 0) {
			System.out.println("XadesSig.addObject() errorCode=" + rc + ", errorMessage=" + dSigner.getErrorMessage());
			return;
		}

		rc = dSigner.sign20("signatureId20", "http://www.w3.org/2001/04/xmlenc#sha256", "urn:oid:1.3.158.36061701.1.2.2", "dataEnvelopeId",
				"dataEnvelopeURI", "dataEnvelopeDescr");
		if (rc != 0) {
			System.out.println("XadesSig.sign20() errorCode=" + rc + ", errorMessage=" + dSigner.getErrorMessage());
			return;
		}		

		System.out.println(dSigner.getSignedXmlWithEnvelope());
		
		//String SIGNED_FILE_PATH = "src//main//resources//signed_document.xml";
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)",
				"*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(bp.getScene().getWindow());
		
		
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(dSigner.getSignedXmlWithEnvelope());
		fileWriter.flush();
		fileWriter.close();
		
	}
	
	
	
	
}
