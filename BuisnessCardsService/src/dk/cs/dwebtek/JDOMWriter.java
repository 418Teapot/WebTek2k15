package dk.cs.dwebtek;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

@Produces("text/xml")
@Provider
public class JDOMWriter implements MessageBodyWriter<Document> {
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type == Document.class;
	}

	@Override
	public long getSize(Document document, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1; // Not used by JAX-RS 0
	}

	@Override
	public void writeTo(Document document, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		XMLOutputter outputter = new XMLOutputter();
		outputter.output(document, entityStream);
	}
}
