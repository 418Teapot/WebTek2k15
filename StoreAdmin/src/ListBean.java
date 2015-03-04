

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@ManagedBean
@ViewScoped
public class ListBean implements Serializable {			
	
	private SAXBuilder builder = new SAXBuilder();
	
	private Document itemsDoc;
	private Namespace ns = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014"); // designate our XML namespace	
	
	private List<Item> itemsList = new ArrayList<Item>();
	
	private Item specificItem = null;
	
	public ListBean(){	
		System.out.println("ListBean awakens!");
					
	}
		

    @PostConstruct
    public void init(){
    	
    	try {
    	
    	System.out.println("Communicating with server!");
		// open connection and setup for post!
		HttpURLConnection con = (HttpURLConnection)((new URL(vars.listUrl))).openConnection();
		con.setRequestMethod("GET"); // our request type is post
		con.setRequestProperty("User-Agent", "TeapotClient"); // funny user agent (needed to be a valid post request)
		con.setRequestProperty("Content-type", "text/xml");	// content type		
		con.connect();
		
		System.out.println("Server responded: "+con.getResponseCode());
			if(con.getResponseCode() == 200){
				itemsDoc = builder.build(con.getInputStream());					
				
				Element root = itemsDoc.getRootElement();
				List<Element> elements = root.getChildren();
				
				
				for(Element e : elements){																		
					Item i = new Item(e.getChildText("itemID", ns), e.getChildText("itemName", ns),e.getChildText("itemPrice", ns), e.getChildText("itemStock", ns), e.getChildText("itemURL", ns), e.getChild("itemDescription", ns));
					itemsList.add(i);
				}
				
				ArrayList<String> delIds = deletedIds();
				
				// Way more efficient than the old way (see below if u really want too)!
				for(Iterator<Item> it = itemsList.iterator(); it.hasNext();){
					String id = it.next().getId();
					for(String s : delIds){						
						if(id.equals(s)){						
							it.remove();
						}
					}
				}
				
				/* DONE (See above): re-implement - this is way to slow to be acceptable!
				Iterator<Item> itr = itemsList.iterator();
				while(itr.hasNext()){
					Item currItem = itr.next();System.out.println(it.next());
					for(String s : deletedIds()){
						if(currItem.getId().equals(s)){
							itr.remove();
						}
					}
				}*/
								
			}
		
		} catch (MalformedURLException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
        
    }
	
	public List<Item> getItemsList() throws JDOMException{			
		return itemsList;		
	}
	
	public List<Item> getItemsListReversed() throws JDOMException{
		List<Item> reversed = getItemsList();
		Collections.reverse(itemsList);
		return reversed;		
	}
	
	public ArrayList<String> deletedIds() throws MalformedURLException, IOException, JDOMException{
		ArrayList<String> idList = new ArrayList<String>();
		
		HttpURLConnection con = (HttpURLConnection)((new URL(vars.cloudUrl+"/listDeletedItemIDs?shopID=198"))).openConnection();
		con.setRequestMethod("GET"); // our request type is post
		con.setRequestProperty("User-Agent", "TeapotClient"); // funny user agent (needed to be a valid post request)
		con.setRequestProperty("Content-type", "text/xml");	// content type		
		con.connect();
		
		if(con.getResponseCode() == 200){
			Document deletedItems = builder.build(con.getInputStream());		
			List<Element> delElements = deletedItems.getRootElement().getChildren();
			for(Element e : delElements){
				idList.add(e.getText());
			}
		}		
		return idList;		
	}
	
	public Item getSpecificItem(){
		FacesContext fc = FacesContext.getCurrentInstance();
	      Map<String,String> params = 
	      fc.getExternalContext().getRequestParameterMap();
		
	    String id = params.get("itemID");
	    System.out.println("Looking for item with ID: "+id);	    
	    
		for(Item i : itemsList){
			if(i.getId().equals(id)){
				specificItem = i;
				System.out.println("We found it!");
			}
		}
		return specificItem;
	}
	// Method overload!
	public Item getSpecificItem(String id){
		
		System.out.println("Looking for item with ID: "+id);	    
	    
		for(Item i : itemsList){
			if(i.getId().equals(id)){
				specificItem = i;
				System.out.println("We found it!");
			}
		}
		return specificItem;
	}
	
	
}
