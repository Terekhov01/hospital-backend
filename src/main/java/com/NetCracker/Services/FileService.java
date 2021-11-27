package com.NetCracker.Services;

import com.NetCracker.Entities.File;
import com.NetCracker.Repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void save(MultipartFile file) throws IOException {
        File fileEntity = new File();

    }
}
