
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;


@ManagedBean
@SessionScoped
public class CreateBean {
	
	private String newItemName = "";	
	private String newItemID = "";
	private String newItemURL = "";
	private String newItemPrice = "";
	private String newItemDesc = "";
	
	private Namespace ns = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014"); // designate our XML namespace
	private XMLOutputter xo = new XMLOutputter();
	private SAXBuilder builder = new SAXBuilder();
	
	
	@PostConstruct
	public void init(){		
			newItemName = "";	
			newItemID = "";
			newItemURL = "";
			newItemPrice = "";
			newItemDesc = "";				
	}
	
	public String getNewItemID(){
		return this.newItemID;
	}
	
	public void setNewItemName(String newItemName){
		this.newItemName = newItemName;
	}	
	
	public String getNewItemName(){
		return this.newItemName;
	}
	
	public void setNewItemURL(String newItemURL){
		this.newItemURL = newItemURL;
	}
	
	public String getNewItemURL(){
		return this.newItemURL;
	}	
	
	public void setNewItemPrice(String newItemPrice){
		this.newItemPrice = newItemPrice;
	}
	
	public String getNewItemPrice(){
		return this.newItemPrice;
	}
	
	public void setNewItemDesc(String newItemDesc){
		this.newItemDesc = newItemDesc;
	}
	
	public String getNewItemDesc(){
		return newItemDesc;
	}
	
	public String submit() throws IOException, JDOMException{	
		  FacesContext fc = FacesContext.getCurrentInstance();
	      Map<String,String> params = 
	      fc.getExternalContext().getRequestParameterMap();
	      
	    String step = params.get("step");
	    
		if(step.equals("1")){
			System.out.println("Step 1 executing");
			
			Element rootCreate = new Element("createItem", ns);								
			Element createName = new Element("itemName", ns);
			Element createKey = new Element("shopKey", ns);
			Document createDoc = new Document();
			
			createName.addContent(newItemName);
			createKey.addContent(vars.shopKey);
			rootCreate.addContent(createName);
			rootCreate.addContent(createKey);
			createDoc.setRootElement(rootCreate);
														
			InputStream docStream = new ByteArrayInputStream(xo.outputString(createDoc).getBytes("UTF-8"));			
			createDoc = vars.readAndValidateXML(docStream);
			
			
			
			HttpURLConnection con = vars.connectCloud("createItem");
			xo.output(createDoc, con.getOutputStream());
			
			if(con.getResponseCode() == 200){
				Document responseDoc = builder.build(con.getInputStream());
				xo.output(responseDoc, System.out);
				
				newItemID = responseDoc.getRootElement().getText();
				fc.getExternalContext().redirect("create_2.xhtml");
			}
		}
	    
		return "";
	}
	
}
