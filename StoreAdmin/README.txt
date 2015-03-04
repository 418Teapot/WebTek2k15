
Handin 3 - README

The web application has been deployed to a server on:
	
	teapots.moore.dk (notice no http - the redirect kind of messes with that)
	and is also available directly at the following ip
	
	http://128.199.43.203:8080/StoreAdmin/
	
	No password or other form of login is necessary (as it was not in the scope of the handin)
	
	
	Description of recursive method for itemDescription
	Not really sure if this should be included or not (vague handin description)
	
	private void addNamespace(Element root) { // we assign an element
	    if(root == null)	 // if element is null return
	        return;
	    
	    root.setNamespace(ns); // set name space on the original element
	    
	    if(!root.getChildren().isEmpty()){ // if the element has children
	    	for(Iterator<Element> itr = root.getChildren().iterator(); itr.hasNext();){ // get iterator
				addNamespace(itr.next()); // add namespace as we iterate through the children - calling ourselves again1
			}
	    } else {
	    	root.setNamespace(ns); // catch all - actually not really needed!
	    	return;
	    }
	}	
		
		
	I also do replaceAll for tags in my beans so <i> becomes <w:italics> and so on - and the same process in reverse
	for when i get data from the server.	
		

HTTP 1.1/418 - All Hail the Mighty Teapot