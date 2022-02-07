package com.NetCracker.services.files;

import com.NetCracker.entities.patient.File;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.repositories.patient.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void save(MultipartFile file) throws IOException
    {
        File fileEntity = new File();
    }

    public void save(String fileName, Patient relatedPatient, byte[] fileBytes) throws DataAccessException
    {
        File file = new File(fileName, relatedPatient, fileBytes);
        fileRepository.save(file);
    }

    public File findById(Long fileId) throws DataAccessException
    {
        return fileRepository.findById(fileId).orElse(null);
    }
}
