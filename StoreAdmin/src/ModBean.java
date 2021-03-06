

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

// modifier bean!
@ManagedBean
public class ModBean implements Serializable  {	
	
	private String newName = "";
	private String newURL = "";
	private String newPrice = "";
	private String newDesc = "";
	
	private String newItemID = "";
	
	private Namespace ns = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014"); // designate our XML namespace		
	
	public ModBean(){
		System.out.println("ModBean awakens!");
	}
	
	public void setName(String name){
		this.newName = name;	
	}
	
	public String getName(){
		return this.newName;
	}
	
	public void setUrl(String url){
		this.newURL = url;
	}
	
	public String getUrl(){
		return this.newURL;
	}
	
	public void setPrice(String price){
		this.newPrice = price;
	}
	
	public String getPrice(){
		return this.newPrice;
	}
	
	public void setDesc(String desc){
		this.newDesc = desc;
	}
	
	public String getDesc(){
		return this.newDesc;
	}
	
	public void modifyItem() throws IOException, JDOMException{
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
	      
		System.out.println(params.toString());	    	
			
		if(newItemID.equals("")){
			newItemID = params.get("itemID");
		}
		
		if(newName.equals("")){
			newName = params.get("itemName");
		}
		
		if(newURL.equals("")){
			newURL = params.get("itemUrl");
		}
		
		if(newPrice.equals("")){
			newPrice = params.get("itemPrice");
		}
		
		if(newDesc.equals("")){
			newDesc = params.get("itemDesc");
		}
		
		System.out.println("Modifying item...");
		System.out.println("Values: "+newName+", "+newURL+", "+newPrice+", "+newDesc);	
		
		// modifier document
		Document modDoc = new Document();
		
		// doc elements
		Element modRoot, modKey, modID, modName, modURL, modPrice, modDesc, modDescDoc;
		
		modRoot = new Element("modifyItem", ns);
		modDoc.setRootElement(modRoot);
		
		
		modKey = new Element("shopKey", ns);
		modID = new Element("itemID", ns);
		modName = new Element("itemName", ns);
		modURL = new Element("itemURL", ns);
		modPrice = new Element("itemPrice", ns);		
		modDesc = new Element("itemDescription", ns);
		modDescDoc = new Element("document", ns);
		
		modKey.addContent(vars.shopKey);
		modID.addContent(newItemID);
		modName.addContent(newName);
		modURL.addContent(newURL);
		modPrice.addContent(newPrice);
		
		String descString = newDesc;
		if(newDesc != null){
		descString = descString.replaceAll("<i>", "<italics>");
		descString = descString.replaceAll("</i>", "</italics>");
		descString = descString.replaceAll("<b>", "<bold>");
		descString = descString.replaceAll("</b>", "</bold>");
		descString = descString.replaceAll("<ul>", "<list>");
		descString = descString.replaceAll("</ul>", "</list>");
		descString = descString.replaceAll("<li>", "<item>");
		descString = descString.replaceAll("</li>", "</item>");
		descString = descString.replaceAll("  ", " "); // accidental whitespace removal!
		
		modDescDoc.addContent(descString);
		descString = "<document>"+descString+"</document>";
		
		// new way
		SAXBuilder builder = new SAXBuilder();
		InputStream stream = new ByteArrayInputStream(descString.getBytes("UTF-8"));
		Document descDoc = builder.build(stream);
		
		Element descRoot = descDoc.getRootElement();		
		descRoot.detach();
		addNamespace(descRoot);
		//descRoot.setNamespace(ns);							
		
		
		modDesc.addContent(descRoot);
		}		
		modRoot.addContent(modKey);
		modRoot.addContent(modID);
		modRoot.addContent(modName);
		modRoot.addContent(modURL);
		modRoot.addContent(modPrice);
		modRoot.addContent(modDesc);
		
		XMLOutputter xo = new XMLOutputter();
		System.out.println(modDoc.toString());
		xo.output(modDoc, System.out);
		
		HttpURLConnection con = (HttpURLConnection)((new URL(vars.cloudUrl+"/modifyItem"))).openConnection();
		con.setRequestMethod("POST"); // our request type is post
		con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
		con.setRequestProperty("Content-type", "text/xml");	// content type
		con.setDoOutput(true);
		
		InputStream docStream = new ByteArrayInputStream(xo.outputString(modDoc).getBytes("UTF-8"));			
		modDoc = vars.readAndValidateXML(docStream);		
		
		
		xo.output(modDoc, con.getOutputStream());
		if(con.getResponseCode() == 200){
			System.out.println("We modified the item!");
			context.getExternalContext().redirect("store.xhtml");
		} else {
			System.out.println("ERROR - Response was: "+con.getResponseCode());
		}
		
	}
	
	private void addNamespace(Element root) {
	    if(root == null)
	        return;
	    
	    root.setNamespace(ns);
	    
	    if(!root.getChildren().isEmpty()){
	    	for(Iterator<Element> itr = root.getChildren().iterator(); itr.hasNext();){
				addNamespace(itr.next());
			}
	    } else {
	    	root.setNamespace(ns);
	    	return;
	    }
	}
	
}
