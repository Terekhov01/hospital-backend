package com.NetCracker.services.files;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.patient.File;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.repositories.patient.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    private final FileRepository fileRepository;

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

    /*@Transactional(propagation = Propagation.REQUIRED)
    public void save(String fileName, Appointment appointment, byte[] fileBytes) throws DataAccessException
    {
        File file = new File(fileName, appointment, fileBytes);
        fileRepository.save(file);
    }*/

    public File findById(Long fileId) throws DataAccessException
    {
        return fileRepository.findById(fileId).orElse(null);
    }
}
