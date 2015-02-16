import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

// This very nice itemadd from file will basically be the same
// old same old additem file - this is it!

public class AddItemFromFile {
	
	private static String shopKey = "92D5C7CB77A1F098381E16CF";
	private static String cloudURL = "http://services.brics.dk/java4/cloud";	
	public static void main(String[] arg){
		
		if(arg.length != 0){
			
			// We take the XML input file and parse this:
			try {
				InputStream xmlFile = new FileInputStream(arg[0]);
				SAXBuilder builder = new SAXBuilder();				
				XMLOutputter xo = new XMLOutputter();
				
				Document inputDoc = builder.build(xmlFile);			
				
				
				// get the working namespace								
				Namespace ns = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014"); // designate our XML namespace		
							
				Element rootCreate = new Element("createItem", ns);
				Element createName = new Element(inputDoc.getRootElement().getChildText("itemName"), ns);
				Element createKey = new Element(shopKey, ns);
				Document createDoc = new Document();
				
				createDoc.setRootElement(rootCreate);
				rootCreate.addContent(createKey);
				rootCreate.addContent(createName);
				
				// open connection and setup for post and dataoutput
				HttpURLConnection con = (HttpURLConnection)((new URL(cloudURL+"/createItem"))).openConnection();
				con.setRequestMethod("POST"); // our request type is post
				con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
				con.setRequestProperty("Content-type", "text/xml");	// content type
				
				xo.output(createDoc, con.getOutputStream());
				con.connect();
				
				if(con.getResponseCode() == 200){
					
					Document responseDoc = builder.build(con.getInputStream());
					con.disconnect();					
					
					Document modDoc = new Document();
					
					Element modRoot, modSk, modItemID, modItemName, modItemURL, modItemPrice, modItemDescription; 
					modRoot = new Element("modifyItem", ns);
					modSk = new Element(shopKey, ns);
					modItemID = responseDoc.getRootElement();
					modItemName = inputDoc.getRootElement().getChild("itemName");
					modItemURL = inputDoc.getRootElement().getChild("itemURL");
					modItemPrice = inputDoc.getRootElement().getChild("itemPrice");
					modItemDescription = inputDoc.getRootElement().getChild("itemDescription");
					
					modDoc.setRootElement(modRoot);
					modRoot.addContent(modSk);
					modRoot.addContent(modItemID);
					modRoot.addContent(modItemName);
					modRoot.addContent(modItemURL);
					modRoot.addContent(modItemPrice);
					modRoot.addContent(modItemDescription);
					
					// reopen with new url
					con = (HttpURLConnection)((new URL(cloudURL+"/modifyItem"))).openConnection();
					con.setRequestMethod("POST"); // our request type is post
					con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
					con.setRequestProperty("Content-type", "text/xml");	// content type
					
					xo.output(modDoc, con.getOutputStream());
					con.connect();
					
					if(con.getResponseCode() == 200){
						// we successfully modified the item!
						// restock!
						con.disconnect();
						
						Document modStock = new Document();
						Element stockRoot = new Element("adjustItemStock", ns);
						Element adjustElement = inputDoc.getRootElement().getChild("itemStock");
						
						modStock.setRootElement(stockRoot);
						stockRoot.addContent(createKey);
						stockRoot.addContent(modItemID);
						stockRoot.addContent(adjustElement);
						
						// reopen with new url
						con = (HttpURLConnection)((new URL(cloudURL+"/adjustItemStock"))).openConnection();
						con.setRequestMethod("POST"); // our request type is post
						con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
						con.setRequestProperty("Content-type", "text/xml");	// content type
						
						xo.output(modStock, con.getOutputStream());
						con.connect();
						if(con.getResponseCode() == 200){
							System.out.println("We are done! Thank you very much!");
							System.exit(0);
						} else {
							System.out.println("Of course we fail NOW - fuck diz! im out!");
							System.exit(0);
						}
					}
					
					
				} else {
					con.disconnect();
					System.out.println("Didnt work ERROR ERROR - wups!");
					System.exit(0);
				}
				
				
			} catch (JDOMException e){
				e.printStackTrace();
			} catch (FileNotFoundException e) { 
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}			
						
		} else {
			System.out.println("USAGE: AddItemFromFile -(xmlfile) ERROR: ");
			System.out.println("You have not provided an input file, please try again!");
			System.exit(0);
		}
		
	}

}