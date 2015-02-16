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
				Element createName = new Element("itemName", ns);
				Element createKey = new Element("shopKey", ns);
				Document createDoc = new Document();
				
				createDoc.setRootElement(rootCreate);
				createKey.addContent(shopKey);
				rootCreate.addContent(createKey);
				
				createName = inputDoc.getRootElement().getChild("itemName", ns);
				createName.detach();
				rootCreate.addContent(createName);
				
				// open connection and setup for post and dataoutput
				HttpURLConnection con = (HttpURLConnection)((new URL(cloudURL+"/createItem"))).openConnection();
				con.setRequestMethod("POST"); // our request type is post
				con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
				con.setRequestProperty("Content-type", "text/xml");	// content type
				con.setDoOutput(true);
				
				xo.output(createDoc, con.getOutputStream());
				con.connect();
				
				if(con.getResponseCode() == 200){
					
					Document responseDoc = builder.build(con.getInputStream());
					con.disconnect();					
					
					Document modDoc = new Document();
					
					System.out.println("ID be: "+responseDoc.getRootElement().getText()+" yo!");
					
					Element modRoot, modSk, modItemID, modItemName, modItemURL, modItemPrice, modItemDescription;
					
					modRoot = new Element("modifyItem", ns);
					modDoc.setRootElement(modRoot);
					//reuse shopkey from the create key!
					createKey.detach();
					modRoot.addContent(createKey);
					
					modItemID = responseDoc.getRootElement(); // root element in response is the ID
					modItemID.detach();
					modRoot.addContent(modItemID);

					// reuse name from the createDoc
					createName.detach();
					modRoot.addContent(createName);
										
					modItemURL = inputDoc.getRootElement().getChild("itemURL", ns);
					modItemURL.detach();
					modRoot.addContent(modItemURL);
					
					
					modItemPrice = inputDoc.getRootElement().getChild("itemPrice", ns);
					modItemPrice.detach();
					modRoot.addContent(modItemPrice);
					
					modItemDescription = inputDoc.getRootElement().getChild("itemDescription", ns);
					modItemDescription.detach();
					modRoot.addContent(modItemDescription);
				
					
					// reopen with new url
					con = (HttpURLConnection)((new URL(cloudURL+"/modifyItem"))).openConnection();
					con.setRequestMethod("POST"); // our request type is post
					con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
					con.setRequestProperty("Content-type", "text/xml");	// content type
					con.setDoOutput(true);
					
					xo.output(modDoc, System.out);
					
					xo.output(modDoc, con.getOutputStream());
					con.connect();
					System.out.println(con.getResponseCode()+" "+con.getResponseMessage());
					if(con.getResponseCode() == 200){
						// we successfully modified the item!
						// restock!
						con.disconnect();
						System.out.println("Mod'er stock!");
						Document modStock = new Document();
						Element stockRoot = new Element("adjustItemStock", ns);
						Element adjustElement = new Element("adjustment", ns);
						adjustElement.addContent(inputDoc.getRootElement().getChild("itemStock", ns).getText());
						
						modStock.setRootElement(stockRoot);
						createKey.detach();
						stockRoot.addContent(createKey);
						modItemID.detach();
						stockRoot.addContent(modItemID);
						//adjustElement.detach();
						stockRoot.addContent(adjustElement);
						
						// reopen with new url
						con = (HttpURLConnection)((new URL(cloudURL+"/adjustItemStock"))).openConnection();
						con.setRequestMethod("POST"); // our request type is post
						con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
						con.setRequestProperty("Content-type", "text/xml");	// content type
						con.setDoOutput(true);
						
						xo.output(modStock, System.out);
						xo.output(modStock, con.getOutputStream());
						con.connect();
						
						System.out.println(con.getResponseCode());
						
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