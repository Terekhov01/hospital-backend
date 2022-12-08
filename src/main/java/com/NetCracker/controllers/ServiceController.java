package com.NetCracker.controllers;

import com.NetCracker.entities.Service;
import com.NetCracker.exceptions.ServiceNotFoundException;
import com.NetCracker.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    ServiceRepository repository;

    @GetMapping("/services")
    public ResponseEntity<List<Service>> getAllServices(@RequestParam(required = false) Long id) {
        try {
            List<Service> services = new ArrayList<>();

            if (id == null)
                services.addAll(repository.findAllByOrderByIdAsc());
            else
                services.add(repository.findById(id).orElseThrow(() -> new ServiceNotFoundException(id)));

            if (services.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(services, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/services/id/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable("id") Long id) {
        Optional<Service> ServiceData = repository.findById(id);

        return ServiceData.map(Service ->
                new ResponseEntity<>(Service, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/services/lastname/{name}")
    public ResponseEntity<Service> getServiceByName(@PathVariable("name") String lastName) {
        System.out.println("In get method int Service");
        System.out.println("Last name: " + lastName);
        Optional<Service> ServiceData = repository.findServiceByName(lastName);

        if (ServiceData.isPresent()) {
            System.out.println("Found Service!");
        } else {
            System.out.println("Service Not Found!");
        }

        return ServiceData.map(Service ->
                new ResponseEntity<>(Service, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/services")
    public ResponseEntity<Service> createService(@RequestBody Service Service) {
        try {
            Service _Service = repository
                    .save(new Service(Service.getServiceName()));
            return new ResponseEntity<>(_Service, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<Service> updateService(@PathVariable("id") Long id, @RequestBody Service Service) {
        Optional<Service> ServiceData = repository.findById(id);

        if (ServiceData.isPresent()) {
            Service _Service = ServiceData.get();
            _Service.setServiceName(Service.getServiceName());
            return new ResponseEntity<>(repository.save(_Service), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<HttpStatus> deleteService(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/services")
    public ResponseEntity<HttpStatus> deleteAllServices() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
