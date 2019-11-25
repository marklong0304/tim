package com.travelinsurancemaster.model.dto.serialiazation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.travelinsurancemaster.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MultiFormatDateDeserializer extends StdDeserializer<LocalDate> {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(MultiFormatDateDeserializer.class);

    private static final DateTimeFormatter localeDateFormatter = DateTimeFormatter.ofPattern(DateUtil.DEFAULT_DATE_FORMAT);

    public MultiFormatDateDeserializer() {
        this(null);
    }

    public MultiFormatDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);

        JsonNodeType nodeType = node.getNodeType();

        if(nodeType == JsonNodeType.STRING) {

            //Try to parse local date stored in default format
            final String date = node.textValue();
            try {
                return LocalDate.parse(String.valueOf(date), localeDateFormatter);
            } catch (Exception e) {}

        } else if(nodeType == JsonNodeType.NUMBER) {

            //Try to parse timestamp
            final long dateTimestamp = node.longValue();
            try {
                Timestamp timestamp = new Timestamp(dateTimestamp);
                return timestamp.toLocalDateTime().toLocalDate();
            } catch (Exception e) {}

        } else if(nodeType == JsonNodeType.NULL) {

            //Null value
            return null;
        }

        throw new JsonParseException(jp, "Unparseable date: " + node.asText());
    }
}