package fr.isen.jeelab.checkers.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import fr.isen.jeelab.checkers.core.ChipColour;
import fr.isen.jeelab.checkers.jpa.CheckersAdapter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

@Provider
@Produces({"application/json","*/*"})
public class CheckersGameBodyWriter implements
        MessageBodyWriter<CheckersAdapter> {

    @Context
    UriInfo info;


    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return type.equals(CheckersAdapter.class);
    }

    @Override
    public long getSize(CheckersAdapter t, Class<?> type,
            Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(CheckersAdapter game, Class<?> type,
            Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {

        JsonFactory factory = new JsonFactory();
        JsonGenerator jg = factory.createGenerator(entityStream, JsonEncoding.UTF8);

        jg.writeStartObject();

        ChipColour winner = game.getWinner();
        jg.writeStringField("winner", winner != null ? winner.toString() : "");
        jg.writeStringField("token", game.getToken());


        jg.writeFieldName("row");
        jg.writeStartArray();

        for(int i=0; i < game.getRowsNumber(); i ++) {
            jg.writeStartObject();

            jg.writeFieldName("cols");
            jg.writeStartArray();
            for(int j=0; j < game.getColumnsNumber(); j ++) {
                ChipColour cell = game.getCell(j,i);
                jg.writeString(cell != null ? cell.toString() : "");
            }
            jg.writeEndArray();
            jg.writeEndObject();
        }
        jg.writeEndArray();
        jg.writeEndObject();
        jg.flush();

    }
}
