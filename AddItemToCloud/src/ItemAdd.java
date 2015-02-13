import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;


// this is our ItemAdd class for interacting with the cloud
// in this case - we would like to add items to our cloud shop
// via. xml - this program accepts an xml file as argument
// and uploads the item data to the cloud! sup son!

// hail the teapot!

public class ItemAdd {

	private static String cloudURL = "http://services.brics.dk/java4/cloud";	
	private static String shopKey = "92D5C7CB77A1F098381E16CF";
	private static String itemName = "";
	//private static String req = cloudURL+"/createItem";	
	public static void main(String[] args) {
		
		if(args.length != 0 && !args[0].equals("")){ // if args is NOT 0 - and there actually is something written as main arg.
					
			
			if(args[0].equals("TEAPOT")){
				System.out.println("HTTP/1.1 418 - ALL HAIL THE MIGHT TEAPOT!");
				System.exit(0);
			} else {						
							
				
			try {
				
				itemName = args[0];
				System.out.println("Creating item with name: "+itemName);
				// Generate XML - we get a namespace and create a new document			
				Namespace ns = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014");						
				Document doc = new Document();
				
				// we create our elemenst
				Element root = new Element("createItem", ns);
				// shopkey element
				Element sk = new Element("shopKey", ns);
				// itemName 
				Element iN = new Element("itemName", ns);
				sk.addContent(shopKey);
				iN.addContent(itemName);
				
				// add elements to our new "document"
				doc.setRootElement(root);
				root.addContent(sk);
				root.addContent(iN);
				
				// open connection and setup for post and dataoutput
				HttpURLConnection con = (HttpURLConnection)((new URL(cloudURL+"/createItem"))).openConnection();
				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent", "TeapotShopItemADDer");
				con.setRequestProperty("Content-type", "text/xml");							
				con.setDoOutput(true);			
				
				// create an XML output object, and output stream, output it and finally connect to server
				XMLOutputter xo = new XMLOutputter();					
				OutputStream out = con.getOutputStream();
				xo.output(doc, out);				
				con.connect();
				// interpret response code and respond with a nice message :) or allow us to continue creation
				
				int rCode = con.getResponseCode();				
				if(rCode == 200){ // if 200 we get an OK back from the server - original request was good! then ...
					
					try {
						
						// we know the correct response will be XML so we create a new xml document from the response content
						SAXBuilder builder = new SAXBuilder();	
						
						Document responseDoc = builder.build(con.getInputStream());						
											
						con.disconnect();
						// from the response doc we now get our server created itemID 
						String itemID = responseDoc.getRootElement().getText();
						
						// we now create a new request based on modifyItem - firstly we generate the modifyItem XML document
						// we create the new document and all needed elements
						Document modDoc = new Document();						
						Element modRoot, modSk, modItemID, modItemName, modItemURL, modItemPrice, modItemDescription, modItemDocument;  						
						
						
						
						modRoot = new Element("modifyItem", ns);
						modSk = new Element("shopKey", ns);
						modItemID = new Element("itemID", ns);
						modItemName = new Element("itemName", ns);
						modItemURL = new Element("itemURL", ns);
						modItemPrice = new Element("itemPrice", ns);						
						modItemDescription = new Element("itemDescription", ns);
						modItemDocument = new Element("document", ns);
						
						//populate elements with data - first is a given, the server created itemID
						modItemID.addContent(itemID);
						modItemName.addContent(args[0]);
						
						// the next series of data are dependent on input - which we want from the user
						Scanner usrInput = new Scanner(System.in); // System in stream - directly from terminal!
						System.out.println("Item "+args[0]+" was created with id: "+itemID+" - please add following information"+System.lineSeparator());
						System.out.print("Item URL: ");
						String itemURL = usrInput.next();
						System.out.print("Item Price: ");
						String itemPrice = usrInput.next();										
						System.out.print("Item Description: ");
						String itemDesc = usrInput.next();
						
						usrInput.close(); // close user input
						
						// with all the data collected we fill out the elements			
						modItemURL.addContent(itemURL);
						modSk.addContent(shopKey);
						modItemPrice.addContent(itemPrice);
						
						modItemDocument.addContent(itemDesc);
						modItemDescription.addContent(modItemDocument);
						
						// and we then construct our document
						modDoc.setRootElement(modRoot);
						modRoot.addContent(modSk);
						modRoot.addContent(modItemID);
						modRoot.addContent(modItemName);			
						modRoot.addContent(modItemPrice);
						modRoot.addContent(modItemURL);						
						modRoot.addContent(modItemDescription);

						HttpURLConnection modCon = (HttpURLConnection) new URL(cloudURL+"/modifyItem").openConnection();
						modCon.setRequestMethod("POST");
						modCon.setRequestProperty("User-Agent", "ItemModifier");
						modCon.setRequestProperty("Content-type", "text/xml");
						modCon.setDoOutput(true);
						
						modCon.connect();												
						
						OutputStream modOut = modCon.getOutputStream();
						xo.output(modDoc, modOut);
						System.out.println(modOut);
					
						System.out.println(modCon.getResponseCode()+" - "+modCon.getResponseMessage());					
						
					} catch (JDOMException e){
						e.printStackTrace();
					} catch (IOException e){
						e.printStackTrace();					
					}
				} else if(rCode == 400){
					System.out.println("Bad request - maybe malformed xml or shopkey - please try again later!");
				} else if(rCode == 404){
					System.out.println("Shop not found - pls contact the programmer! he made a grave mistake!");
				} else if(rCode == 500){
					System.out.println("Server error - something is very wrong!");				
				} else {
					System.out.println("Other error occured - pls refer to following messages");
					System.out.println("Code: "+con.getResponseCode()+" Msg:");
					System.out.println(con.getResponseMessage());
				}				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			}
			
		}

	}

}
