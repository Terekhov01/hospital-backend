package com.NetCracker.payload.Response;

import com.NetCracker.entities.patient.File;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.net.MalformedURLException;
import java.time.LocalDateTime;

@Getter
public class FileDTO
{
    String originalName;
    String MIMEType;
    LocalDateTime creationDate;
    byte[] content;

    String getMIMETypeByFileExtension(String fileName)
    {
        var fileExtension = FilenameUtils.getExtension(fileName);

        switch (fileExtension)
        {
            case "pdf":
                return "application/pdf";

            case "doc":
                return "application/msword";

            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            case "txt":
                return "text/plain";

            default:
                return "";
        }
    }

    public FileDTO(File file) throws MalformedURLException
    {
        this.originalName = file.getName();
        this.creationDate = file.getCreationDate();
        this.content = file.getFileData();
        this.MIMEType = getMIMETypeByFileExtension(file.getName());
    }
}