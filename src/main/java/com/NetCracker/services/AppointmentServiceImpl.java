package com.NetCracker.services;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.appointment.AppointmentRegistration;
import com.NetCracker.entities.patient.File;
import com.NetCracker.payload.Request.AppointmentCreationDTO;
import com.NetCracker.repositories.appointment.AppointmentRepo;
import com.NetCracker.services.files.FileService;
import com.NetCracker.services.files.SickListFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private FileService fileService;

    @Autowired
    private AppointmentRegistrationService appointmentRegistrationService;

    public AppointmentServiceImpl() {
    }

    @Autowired
    public AppointmentServiceImpl(AppointmentRepo theAppointmentRepo) {
        appointmentRepo = theAppointmentRepo;
    }

    @Override
    @Transactional
    public List<Appointment> getAll() {
        return appointmentRepo.findAllByOrderByIdAsc();
    }

    @Override
    @Transactional
    public Appointment getById(Long theId)
    {
        return appointmentRepo.findById(theId).orElse(null);
        /*Appointment theAppointment;

        if (result.isPresent()) {
            theAppointment = result.get();
        } else {
            throw new RuntimeException("Did not find appointment id - " + theId);
        }

        return theAppointment;*/
    }

    @Override
    @Transactional
    public Optional<Appointment> findByAppointmentRegistration(AppointmentRegistration appointmentRegistration)
    {
        return appointmentRepo.findByAppointmentRegistration(appointmentRegistration);
    }

    @Override
    @Transactional
    public void editAppointment(Appointment theAppointment) {
        appointmentRepo.save(theAppointment);
    }

    @Override
    @Transactional
    public void delete(Long theId) {
        appointmentRepo.deleteById(theId);
    }

    @Override
    @Transactional
    public Appointment createAppointment(AppointmentCreationDTO appointmentDTO, List<MultipartFile> filesToUpload, AppointmentRegistration appointmentRegistration) throws DataAccessException, IOException
    {
        Appointment appointment = new Appointment(null, appointmentRegistration, appointmentDTO.getDescription(),
                appointmentDTO.getRecipe(), appointmentDTO.getTreatPlan(), appointmentDTO.getRehabPlan(),
                appointmentDTO.getDocStatement(), new ArrayList<>());

        appointmentRepo.save(appointment);

        if (filesToUpload != null)
        {
            for (var file : filesToUpload)
            {
                File fileToSave = new File(file.getOriginalFilename(), appointment, file.getBytes());
                fileService.save(fileToSave);
                appointment.getFiles().add(fileToSave);
            }
        }

        if (appointmentDTO.isSickListNeeded())
        {
            var sickLeaveDocument = SickListFactory.createDocument(appointment,
                    appointmentDTO.getRecoveryDate());

            ByteArrayOutputStream fileByteStream = new ByteArrayOutputStream();
            sickLeaveDocument.write(fileByteStream);
            byte[] sickListBytes = fileByteStream.toByteArray();

            File sickList = new File("Больничный лист.docx", appointment, sickListBytes);

            fileService.save(sickList);
        }

        return appointment;
    }
}
