package com.NetCracker.payload.Response;

import com.NetCracker.entities.patient.File;
import lombok.Getter;
import org.docx4j.wml.U;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Getter
public class FileResourceDTO
{
    String originalName;
    LocalDateTime creationDate;
    Resource urlResource;

    public FileResourceDTO(Path path, File file) throws MalformedURLException
    {
        this.originalName = file.getName();
        this.creationDate = file.getCreationDate();
        urlResource = new UrlResource(path.toUri());
    }

    public boolean exists()
    {
        return this.urlResource.exists();
    }

    public URI getURI() throws IOException
    {
        return urlResource.getURI();
    }

    public String getOriginalName()
    {
        return originalName;
    }

    public LocalDateTime getCreationDate()
    {
        return creationDate;
    }

    public Resource getUrlResource()
    {
        return urlResource;
    }
}
