package com.NetCracker.services.files;

import com.NetCracker.entities.patient.File;
import com.NetCracker.payload.Response.FileDTO;
import com.NetCracker.payload.Response.FileResourceDTO;
import com.NetCracker.repositories.patient.FileRepository;
import com.NetCracker.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileService {

    private final FileRepository fileRepository;

    // Used to create unique filenames
    static int fileCounter = 100000;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(MultipartFile file) throws IOException
    {
        File fileEntity = new File();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(File file)
    {
        fileRepository.save(file);
    }

    public File findById(Long fileId) throws DataAccessException
    {
        return fileRepository.findById(fileId).orElse(null);
    }

    public Path generateUniqueFilePath(Path targetDirectory)
    {
        if (fileCounter > 1000000)
        {
            fileCounter = 100000;
        }

        var generatedPath = Path.of(targetDirectory.toString(), fileCounter + ".temporary");

        while (Files.exists(generatedPath))
        {
            fileCounter++;
            generatedPath = Path.of(targetDirectory.toString(), fileCounter + ".temporary");
        }

        fileCounter++;
        return generatedPath;
    }

    public List<FileDTO> provideFileDTOByAppointmentId(Long appointmentId) throws IOException, DataAccessException
    {
        List<File> fileData = fileRepository.findByAppointmentId(appointmentId);

        var retVal = new ArrayList<FileDTO>();

        for (var file : fileData)
        {
            retVal.add(new FileDTO(file));
        }

        return retVal;
    }
}
