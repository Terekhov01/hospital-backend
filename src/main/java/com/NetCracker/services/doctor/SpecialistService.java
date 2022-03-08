package com.NetCracker.services.doctor;

import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.repositories.doctor.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SpecialistService
{
    @Autowired
    SpecialistRepository specialistRepository;

    //Sort specializations in alphabetic order
    public SortedMap<Character, List<String>> getAutocompleteOptions()
    {

        var specializations = specialistRepository.findSpecializationByOrderBySpecialization();

        var specialistAutocompleteOptions = new TreeMap<Character, List<String>>(new Comparator<Character>()
        {
            @Override
            public int compare(Character o1, Character o2)
            {
                return o1.compareTo(o2);
            }
        });

        for (var specializationName : specializations)
        {
            var value = specialistAutocompleteOptions.get(Character.toUpperCase(specializationName.charAt(0)));
            if (value == null)
            {
                var emplaceValue = new ArrayList<String>();
                emplaceValue.add(specializationName);
                specialistAutocompleteOptions.put(Character.toUpperCase(specializationName.charAt(0)), emplaceValue);
            }
            else
            {
                value.add(specializationName);
            }
        }

        return specialistAutocompleteOptions;
    }

    public Specialist findById(Integer id)
    {
        return specialistRepository.findById(id).orElse(null);
    }

    public Specialist findBySpecialization(String name)
    {
        return specialistRepository.findBySpecialization(name).orElse(null);
    }
}
