
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


public class vars {
	
	public static final String shopKey = "92D5C7CB77A1F098381E16CF";	 
	public static final String cloudUrl = "http://services.brics.dk/java4/cloud";
	public static final String listUrl = cloudUrl+"/listItems?shopID=198";
	public static final String cloudSchemeURL = "http://128.199.43.203:8080/StoreAdmin/resources/cloud.xsd";

	// graciously borrowed from the validator supplied
		@SuppressWarnings("deprecation")
	    public static Document readAndValidateXML(InputStream xmlToReadAndValidate) throws JDOMException, IOException {			
				SAXBuilder builder = new SAXBuilder();				
				builder.setValidation(true);
				builder.setProperty(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				"http://www.w3.org/2001/XMLSchema");
				builder.setProperty(
				"http://java.sun.com/xml/jaxp/properties/schemaSource",
				vars.cloudSchemeURL);
				return builder.build(xmlToReadAndValidate);
		}
		
		public static HttpURLConnection connectCloud(String end) throws MalformedURLException, IOException{
			// open connection and setup for post and dataoutput
			HttpURLConnection con = (HttpURLConnection)((new URL(cloudUrl+"/"+end))).openConnection();
			con.setRequestMethod("POST"); // our request type is post
			con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
			con.setRequestProperty("Content-type", "text/xml");	// content type
			con.setDoOutput(true);
			
			return con;
		}
	
}