import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;

// so we can delete things!
@ManagedBean
public class DeleteBean {
	
	
	public void deleteItem() throws IOException{
				
		XMLOutputter xo = new XMLOutputter();
		
		Namespace ns = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014"); // designate our XML namespace	
		Document delDoc = new Document();
		Element eRoot = new Element("deleteItem", ns);
		Element eKey = new Element("shopKey", ns);
		Element eID = new Element("itemID", ns);
		
		FacesContext fc = FacesContext.getCurrentInstance();
	      Map<String,String> params = 
	      fc.getExternalContext().getRequestParameterMap();
		
	    String id = params.get("id");
	    
	    eKey.addContent(vars.shopKey);
	    eID.addContent(id);
	    eRoot.addContent(eKey);
	    eRoot.addContent(eID);
	    delDoc.setRootElement(eRoot);
	    
	    HttpURLConnection con = (HttpURLConnection)((new URL(vars.cloudUrl+"/deleteItem"))).openConnection();
	    con.setRequestMethod("POST"); // our request type is post
		con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
		con.setRequestProperty("Content-type", "text/xml");	// content type
		con.setDoOutput(true);
		
		//xo.output(delDoc, System.out);
		xo.output(delDoc, con.getOutputStream());
		con.connect();
		System.out.println(con.getResponseCode()+" "+con.getResponseMessage());
		if(con.getResponseCode() == 200){
			System.out.println("Item Deleted!");
			fc.getExternalContext().redirect("store.xhtml");
		}
	}
	
}
