package com.NetCracker.services.files;

import com.NetCracker.entities.patient.File;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

@Service
public class FileViewService
{
    public String toJson(File file)
    {
        Gson gson = new Gson();
        return gson.toJson(file);
    }
}
