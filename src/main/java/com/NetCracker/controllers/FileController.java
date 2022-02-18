package com.NetCracker.controllers;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.patient.File;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.exceptions.FileNotFoundException;
import com.NetCracker.payload.Response.FileDTO;
import com.NetCracker.payload.Response.FileResourceDTO;
import com.NetCracker.repositories.appointment.AppointmentRepo;
import com.NetCracker.repositories.patient.FileRepository;
import com.NetCracker.repositories.patient.PatientRepository;
import com.NetCracker.services.AppointmentServiceImpl;
import com.NetCracker.services.AuthenticationService;
import com.NetCracker.services.files.FileService;
import com.NetCracker.services.PatientService;
import com.NetCracker.services.files.FileViewService;
import com.NetCracker.services.files.SickListFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class FileController {

    @Autowired
    FileRepository repository;

    @Autowired
    SickListFactory sockListFactory;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    PatientService patientService;

    @Autowired
    AppointmentServiceImpl appointmentService;

    @Autowired
    AppointmentRepo appointmentRepo;

    @Autowired
    FileService fileService;

    @Autowired
    FileViewService fileViewService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("files")
    public ResponseEntity<List<File>> getAllFiles(@RequestParam(required = false) Long id) {
        try {
            List<File> files = new ArrayList<>();

            if (id == null)
                files.addAll(repository.findAllByOrderByIdAsc());
            else
                files.add(repository.findById(id).orElseThrow(() -> new FileNotFoundException(id)));

            if (files.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*@GetMapping("/files/download/{appointment_id}")
//    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_PATIENT')")
    public ResponseEntity<Resource> downloadFile(@PathVariable("appointment_id") Long id) throws IOException {
//        File file = repository.getById(id);
        System.out.println("In download get method");
        System.out.println("Appointment id is:" + id.toString());
        List<File> fileData = repository.findByAppointmentId(id);
        System.out.println("Found files: " + fileData.size());
        Long fileId = fileData.get(0).getId();
        System.out.println("File Id is: " + fileId.toString());
        File file = repository.getById(fileId);
        Path path = Paths.get(PropertiesUtils.getProperty(PropertiesUtils.applicationProperties,
                                                        "app.hospital.tmp.directory"));
        Files.write(path, file.getFileData());
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }*/

    @GetMapping("/files/download/{appointment_id}")
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_PATIENT')")
    public ResponseEntity<?> downloadFiles(@PathVariable("appointment_id") Long id, Authentication authentication) throws IOException {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")))
        {
            Doctor authenticatedDoctor;

            try
            {
                authenticatedDoctor = authenticationService.getAuthenticatedDoctor(authentication);
            }
            catch (ClassCastException e)
            {
                return new ResponseEntity<String>("Аутентификация не пройдена", HttpStatus.UNAUTHORIZED);
            }
            catch (DataAccessException e)
            {
                return new ResponseEntity<String>("Аутентификация не пройдена. База данных недоступна",
                                                                                HttpStatus.SERVICE_UNAVAILABLE);
            }

            if (authenticatedDoctor == null)
            {
                return new ResponseEntity<String>("Не найден доктор с Вашей регистрационной информацией. " +
                        "Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            Patient authenticatedPatient;

            try
            {
                authenticatedPatient = authenticationService.getAuthenticatedPatient(authentication);
            }
            catch (ClassCastException e)
            {
                return new ResponseEntity<String>("Аутентификация не пройдена", HttpStatus.UNAUTHORIZED);
            }
            catch (DataAccessException e)
            {
                return new ResponseEntity<String>("Аутентификация не пройдена. База данных недоступна",
                        HttpStatus.SERVICE_UNAVAILABLE);
            }

            if (authenticatedPatient == null)
            {
                return new ResponseEntity<String>("Не найден доктор с Вашей регистрационной информацией. " +
                        "Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        List<FileDTO> fileResources;

        try
        {
            fileResources = fileService.provideFileDTOByAppointmentId(id);
        }
        catch (IOException e)
        {
            return new ResponseEntity<String>("Не удалось создать временные копии запрашиваемых файлов", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Не удалось получить файлы из базы данных", HttpStatus.SERVICE_UNAVAILABLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<String>("Неизвестная ошибка", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String fileDTOJSON;

        try
        {
            fileDTOJSON = objectMapper.writeValueAsString(fileResources);
        }
        catch (JsonProcessingException e)
        {
            return new ResponseEntity<String>("Не удалось преобразовать информацию в JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(fileDTOJSON, HttpStatus.OK);
    }

    @GetMapping("/files/getRecipe/{id}")
    public ResponseEntity<Resource> getRecipe(@PathVariable("id") Long id) throws Exception {
        System.out.println("Printing recipe");

        Optional<Appointment> appointment = appointmentRepo.findById(id);

        if (appointment.isPresent()) {

            java.io.File file = new java.io.File("/Users/mikhail/Downloads/rec.docx");
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            OutputStream out = new FileOutputStream("/Users/mikhail/Downloads/recipe_new.docx");
            XWPFDocument srcDoc = new XWPFDocument(fis);
            XWPFDocument destDoc = new XWPFDocument();

            List<XWPFParagraph> paras = srcDoc.getParagraphs();

            for (XWPFParagraph para : paras) {
                if (!para.getParagraphText().isEmpty()) {
                    XWPFParagraph newPara = destDoc.createParagraph();
                    copyAllRunsToAnotherParagraph(para, newPara);
                }
            }

            destDoc.write(out);

            out.flush();
            out.close();

            java.io.File dest = new java.io.File("/Users/mikhail/Downloads/recipe_new.docx");
            FileInputStream destFis = new FileInputStream(dest.getAbsolutePath());
            XWPFDocument doc = new XWPFDocument(destFis);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy dd LLLL");
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);
            String yy = date.substring(0,4);
            String dd = date.substring(5,7);
            String month = date.substring(8);

            for (XWPFParagraph p : doc.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("patName")) {
                            text = text.replace("patName",
                                    appointment.get().getAppointmentRegistration().getPatient().getUser().getLastName() + " " +
                                    appointment.get().getAppointmentRegistration().getPatient().getUser().getFirstName() + " " +
                                    appointment.get().getAppointmentRegistration().getPatient().getUser().getPatronymic());
                            r.setText(text, 0);

                        } else if (text != null && text.contains("docName")) {
                            text = text.replace("docName",
                                    appointment.get().getAppointmentRegistration().getDoctor().getUser().getLastName() + " " +
                                            appointment.get().getAppointmentRegistration().getDoctor().getUser().getFirstName() + " " +
                                            appointment.get().getAppointmentRegistration().getDoctor().getUser().getPatronymic());
                            r.setText(text, 0);
                        } else if (text != null && text.contains("dd")) {
                            text = text.replace("dd", dd);
                            r.setText(text, 0);
                        } else if (text != null && text.contains("YYYY")) {
                            text = text.replace("YYYY", yy);
                            r.setText(text, 0);
                        } else if (text != null && text.contains("MONTH")) {
                            text = text.replace("MONTH", month);
                            r.setText(text, 0);
                        } else if (text != null && text.contains("recipe")) {
                            text = text.replace("recipe", appointment.get().getRecipe());
                            r.setText(text, 0);
                        } else if (text != null && text.contains("polys")) {
                            text = text.replace("polys", appointment.get().getAppointmentRegistration().getPatient().getPolys());
                            r.setText(text, 0);
                        }
                    }
                }
            }

            doc.write(new FileOutputStream("/Users/mikhail/Downloads/recipe_new.docx"));
            destFis.close();

            try {
                InputStream templateInputStream = new FileInputStream("/Users/mikhail/Downloads/recipe_new.docx");
                WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);
                MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

                String outputfilepath = "/Users/mikhail/Downloads/recipe_new.pdf";
                FileOutputStream os = new FileOutputStream(outputfilepath);

                Docx4J.toPDF(wordMLPackage,os);
                os.flush();
                os.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            System.out.println("End");

        } else {
            throw new RuntimeException("Internal Error!");
        }

        Path path = Paths.get("/Users/mikhail/Downloads/recipe_new.pdf");

        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }

    @GetMapping("files/id/{id}")
//    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_PATIENT')")
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_PATIENT')")
    public ResponseEntity<List<File>> getFilesByPatientId(@PathVariable("id") Long id) {
        System.out.println("In getFilesByPatientId method");
        System.out.println("Id is: " + id.toString());
        List<File> fileData = repository.findByAppointmentId(id);
        System.out.println("file data size: " + fileData.size());
        return new ResponseEntity<>(fileData, HttpStatus.OK);
    }

    @PostMapping("files/{id}")
//    @PreAuthorize("hasRole(ROLE_DOCTOR)")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<File> createFile(@RequestParam("files") List<MultipartFile> files, @PathVariable("id") Long id) {
//        Optional<Patient> patient = patientRepository.findById(id);
        System.out.println("Posting a file, id is " + id.toString());
        Optional<Appointment> appointment = appointmentRepo.findById(id);
        System.out.println("Found appointment? " + appointment.isPresent());
        if (appointment.isPresent()) {
            try {
                File _file = new File();
                for (MultipartFile file : files) {
                    _file = repository
                            .save(new File(file.getName(), appointment.get(), file.getBytes()));
                }
                return new ResponseEntity<>(_file, HttpStatus.CREATED);
            } catch (Exception e) {
                System.out.println("Error! " + e.getMessage());
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO - provide doctor way to view files
    @GetMapping("/files/get-by-id")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<String> getFile(Long fileId, Authentication authentication)
    {
        Patient requestingPatient;
        try
        {
            requestingPatient = authenticationService.getAuthenticatedPatient(authentication);
        }
        catch (ClassCastException ex)
        {
            return new ResponseEntity<String>("Срвер не смог получить информацию о Вас",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (DataAccessException ex)
        {
            return new ResponseEntity<String>("Срвер не смог получить информацию о пользователе из базы данных",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (requestingPatient == null)
        {
            return new ResponseEntity<String>("Не найден пользователь с Вашим id. Ошибка сервера.",
                                                                        HttpStatus.INTERNAL_SERVER_ERROR);
        }

        File requestedFile;
        try
        {
            requestedFile = fileService.findById(fileId);
        }
        catch (DataAccessException ex)
        {
            return new ResponseEntity<String>("Невозможно получить файл: хранилище недоступно",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (requestedFile == null)
        {
            return new ResponseEntity<String>("Запрашиваемый файл не существует", HttpStatus.NOT_FOUND);
        }

        if (!requestedFile.getAppointment().getAppointmentRegistration().getPatient().getId()
                                                                                    .equals(requestingPatient.getId()))
        {
            return new ResponseEntity<String>("У Вас нет доступа к этому файлу", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<String>(fileViewService.toJson(requestedFile), HttpStatus.OK);
    }

    @PutMapping("files/{file_id}/{patient_id}")
    public ResponseEntity<File> updateFile(@PathVariable("file_id") Long file_id,
                                           @PathVariable("patient_id") Long patient_id,
                                           @RequestBody MultipartFile file) throws IOException {
        Optional<Appointment> appointment = appointmentRepo.findById(patient_id);
        Optional<File> fileData = repository.findById(file_id);
//        Optional<Patient> patient = patientRepository.findById(patient_id);
        if (fileData.isPresent() && appointment.isPresent()) {
            File _file = fileData.get();
            _file.setFileData(file.getBytes());
            _file.setAppointment(appointment.get());
            return new ResponseEntity<>(repository.save(_file), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("files/{id}")
    public ResponseEntity<HttpStatus> deleteFile(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void copyAllRunsToAnotherParagraph(XWPFParagraph oldPar, XWPFParagraph newPar) {
        final int DEFAULT_FONT_SIZE = 10;

        for (XWPFRun run : oldPar.getRuns()) {
            String textInRun = run.getText(0);

            int fontSize = run.getFontSize();
//            System.out.println("run text = '" + textInRun + "' , fontSize = " + fontSize);

            XWPFRun newRun = newPar.createRun();

            // Copy text
            newRun.setText(textInRun);

            // Apply the same style
            newRun.setFontSize( ( fontSize == -1) ? DEFAULT_FONT_SIZE : run.getFontSize() );
//            newRun.setStyle("Default");
            newRun.setFontFamily("Courier New");
            newRun.setBold( run.isBold() );
            newRun.setItalic( run.isItalic() );
//            newRun.setStrike( run.isStrike() );
            newRun.setColor( run.getColor() );
            newRun.setUnderline( run.getUnderline() );
        }
    }

}
