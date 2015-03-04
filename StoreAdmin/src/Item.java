
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;


// A class for storing items!

public class Item {
	
	private String id, name, price, stock, url;
	private Element description;
	private Namespace ns = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014"); // designate our XML namespace
	private XMLOutputter xo = new XMLOutputter();
	
	public Item(String id, String name, String price, String stock, String url, Element description){
		this.id = id;
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.url = url;
		this.description = description;
	}
	
	public String getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getStock(){
		return stock;
	}
	
	public String getUrl(){
		return url;
	}
	
	public String getPrice(){
		return price;
	}
	
	public String getDescription(){
		
		String d = xo.outputString(description.getChild("document", ns));
		d = d.replaceAll("<w:document xmlns:w=\"http://www.cs.au.dk/dWebTek/2014\">", "");
		d = d.replaceAll("<w:document xmlns:w=\"http://www.cs.au.dk/dWebTek/2014\" />", "");
		d = d.replaceAll("</w:document>", "");
		
		// italics
		d = d.replaceAll("<w:italics>", " <i>");
		d = d.replaceAll("</w:italics>", "</i> ");
		// bold
		d = d.replaceAll("<w:bold>", " <b>");
		d = d.replaceAll("</w:bold>", "</b> ");
		// list
		d = d.replaceAll("<w:list>", " <ul>");
		d = d.replaceAll("</w:list>", "</ul> ");
		// list items
		d = d.replaceAll("<w:item>", " <li>");
		d = d.replaceAll("</w:item>", "</li> ");
				
		return d;
	}
	
	public String toString(){
		return "ID: "+id+" Name: "+name+" Stock: "+stock+" URL: "+url;
	}

}
