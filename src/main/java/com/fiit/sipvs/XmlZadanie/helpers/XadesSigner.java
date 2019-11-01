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
	
	private String[] xmls;
	private String xsd;
	private String xslt;
	private BorderPane bp;
	private String signed;
	
	public XadesSigner(BorderPane bp, String[] xmls, String xsd, String xslt, String signed) {
		
		this.bp=bp;
		this.xmls = xmls;
		this.xsd = xsd;
		this.xslt = xslt;
		this.signed =signed + "/signed.xml";
		System.out.println(this.signed);
	}
	
	

	public void sign() throws IOException {
		XadesSig dSigner = new XadesSig();
		dSigner.installLookAndFeel();
		dSigner.installSwingLocalization();
		dSigner.reset();
		//dSigner.setLanguage("sk");
		
		 String DEFAULT_XSD_REF = "http://www.w3.org/2001/XMLSchema";
		 String DEFAULT_XSLT_REF = "http://www.example.org/sipvs";
		

		XmlPlugin xmlPlugin = new XmlPlugin();
		int rc = 0;
		int i =1;
		for (String xml : xmls) {
			System.out.println(i);
			DataObject xmlObject = xmlPlugin.createObject2("XML" + i, "XML"+ i, readResource(xml),
					readResource(xsd),
					"http://www.example.org/sipvs", DEFAULT_XSD_REF,
					readResource(xslt), DEFAULT_XSLT_REF, "HTML");
			
			if (xmlObject == null) {
				System.out.println("XMLPlugin.createObject() errorMessage=" + xmlPlugin.getErrorMessage());
				return;
			}

			rc = dSigner.addObject(xmlObject);
			if (rc != 0) {
				System.out.println("XadesSig.addObject() errorCode=" + rc + ", errorMessage=" + dSigner.getErrorMessage());
				return;
			}
			
			i++;
		}
		
		
		
		

		rc = dSigner.sign20("signatureId20", "http://www.w3.org/2001/04/xmlenc#sha256", "urn:oid:1.3.158.36061701.1.2.2", "dataEnvelopeId",
				"dataEnvelopeURI", "dataEnvelopeDescr");
		if (rc != 0) {
			System.out.println("XadesSig.sign20() errorCode=" + rc + ", errorMessage=" + dSigner.getErrorMessage());
			return;
		}		

		System.out.println(dSigner.getSignedXmlWithEnvelope());
		
		
		FileWriter fileWriter = new FileWriter(new File(this.signed));
		fileWriter.write(dSigner.getSignedXmlWithEnvelope());
		fileWriter.flush();
		fileWriter.close();
		
	}
	
	
	
	
}
