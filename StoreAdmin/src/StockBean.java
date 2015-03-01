import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;


@ManagedBean
@SessionScoped
public class StockBean implements Serializable {

	
	private Namespace ns = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014"); // designate our XML namespace
	private XMLOutputter xo = new XMLOutputter();
	
	private String stock = "";
	private String itemID = "";
	
	public void setStock(String newStock){
		this.stock = newStock;
	}
	
	public String getStock(){
		return this.stock;
	}
	
	public void setItemID(String id){
		this.itemID = id;
	} 
	
	
	public void updateStock() throws MalformedURLException, IOException, JDOMException{
	
		FacesContext fc = FacesContext.getCurrentInstance();
	      Map<String,String> params = 
	      fc.getExternalContext().getRequestParameterMap();
		
	    String id = params.get("id");
		System.out.println(id);
		
		if(id == "" || stock == ""){
			System.out.println("ID "+id+" Stock "+stock+" error!");
		} else {
		
		System.out.println("Item ID "+id+" updated to new Stock "+stock);		
		
		Document modStock = new Document();
		Element stockRoot = new Element("adjustItemStock", ns);
		Element adjustElement = new Element("adjustment", ns);
		Element shopKey = new Element("shopKey", ns);
		Element itemID = new Element("itemID", ns);
		
		adjustElement.addContent(stock);
		
		modStock.setRootElement(stockRoot);
		shopKey.addContent(vars.shopKey);
		itemID.addContent(id);
		stockRoot.addContent(shopKey);		
		stockRoot.addContent(itemID);
		//adjustElement.detach();
		stockRoot.addContent(adjustElement);
		
		// reopen with new url
		HttpURLConnection con = (HttpURLConnection)((new URL(vars.cloudUrl+"/adjustItemStock"))).openConnection();
		con.setRequestMethod("POST"); // our request type is post
		con.setRequestProperty("User-Agent", "TeapotShopItemADDer"); // funny user agent (needed to be a valid post request)
		con.setRequestProperty("Content-type", "text/xml");	// content type
		con.setDoOutput(true);
				
		//InputStream is = new StringBufferInputStream(modStock.toString());
		//modStock = vars.readAndValidateXML(is);
		
		
		
		xo.output(modStock, System.out);
		xo.output(modStock, con.getOutputStream());
		con.connect();
		
		if(con.getResponseCode() == 200){
			fc.getExternalContext().redirect("store.xhtml");
			}
		}

	}
	
}
