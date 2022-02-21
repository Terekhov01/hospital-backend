package com.NetCracker.services.files;

import com.NetCracker.entities.patient.File;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.gson.Gson;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FileViewService
{
    public String toJson(File file)
    {
        Gson gson = new Gson();
        return gson.toJson(file);
    }

    public static class CustomUrlResourceSerializer extends StdSerializer<UrlResource>
    {
        public CustomUrlResourceSerializer() {
            this(null);
        }

        public CustomUrlResourceSerializer(Class<UrlResource> t) {
            super(t);
        }

        @Override
        public void serialize(UrlResource urlResource, JsonGenerator jsonGenerator, SerializerProvider serializer) throws IOException
        {
            jsonGenerator.writeString(urlResource.getURL().toString());
        }
    }
}
