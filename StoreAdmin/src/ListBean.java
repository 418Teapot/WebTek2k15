
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import java.io.Serializable;
import java.util.ArrayList;


@ManagedBean
@SessionScoped
public class ListBean implements Serializable {
	
	private ArrayList<String> items;
	
	public ListBean(){	
		System.out.println("ListBean awakens!");
		
		// run request and create an xml document!
		
	}
	
	public ArrayList<String> getItems(){
		return this.items;
	}
	
	public void setItems(String s){
		items.add(s);	
	}
	
	
}
