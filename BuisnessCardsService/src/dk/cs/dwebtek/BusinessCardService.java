package dk.cs.dwebtek;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

@Path("businesscards")
public class BusinessCardService {
	private @Context ServletContext sc;
	private Namespace ns = Namespace.getNamespace("http://businesscard.org");

	@GET
	@Produces("text/xml")
	@Path("card")
	public Document getBusinessCard(@QueryParam("name") String name) {
		return getDocumentMap().get(name);
	}

	@POST
	@Path("card")
	public void storeBusinessCard(Document d) {
		String name = d.getRootElement().getChildText("name", ns);
		getDocumentMap().put(name, d);
	}

	@GET
	@Produces("text/xml")
	@Path("cards")
	public Document getAllCards() {
		Map<String, Document> map = getDocumentMap();
		Document d = new Document();
		Element cardlist = new Element("cardlist", ns);
		d.setRootElement(cardlist);
		for (Document card : map.values()) {
			cardlist.addContent(card.getRootElement().clone());
		}
		return d;
	}
	
	@GET
	@Path("status")
	public String getStatus(){
		return (getDocumentMap().isEmpty() ? 
				"We are empty" : 
				"We are NOT empty");
	}

	private Map<String, Document> getDocumentMap() {
		Map<String, Document> map = (Map<String, Document>) sc
				.getAttribute("document_map");
		if (map == null) {
			map = new HashMap<>();
			sc.setAttribute("document_map", map);
		}
		return map;
	}
}
