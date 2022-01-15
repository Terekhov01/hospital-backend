package com.NetCracker.controllers;

import com.NetCracker.repositories.doctor.SpecialistRepository;
import com.NetCracker.services.doctor.SpecialistService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.SortedMap;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
public class SpecialistController
{
    @Autowired
    SpecialistService specialistService;

    @PreAuthorize("permitAll()")
    @GetMapping("specializations")
    public ResponseEntity<String> getSpecialistAutocompleteOptions()
    {
        SortedMap<Character, List<String>> specialistOptions;
        try
        {
            specialistOptions = specialistService.getAutocompleteOptions();
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Could not retrieve all specializations from database", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Gson gson = new Gson();

        String optionsString;

        optionsString = gson.toJson(specialistOptions);

        return new ResponseEntity<String>(optionsString, HttpStatus.OK);
    }
}
