package com.NetCracker.services.files;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.patient.Patient;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class SickListFactory
{
    private static void addFrontTitle(XWPFDocument document)
    {
        var titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        var titleRun = titleParagraph.createRun();
        for (int i = 0; i < 12; i++)
        {
            titleRun.addBreak();
        }

        titleRun.setFontSize(20);
        titleRun.setFontFamily("Times New Roman");
        titleRun.setBold(true);
        titleRun.setText("Больничный лист");
        titleRun.addBreak();
        titleRun.setText("(Лист нетрудоспособности)");

        for (int i = 0; i < 12; i++)
        {
            titleRun.addBreak();
        }
    }

    private static void addCaption(XWPFDocument document, LocalDate date)
    {
        var captionParagraph = document.createParagraph();
        captionParagraph.setAlignment(ParagraphAlignment.RIGHT);

        var captionRun = captionParagraph.createRun();
        captionRun.setFontSize(12);
        captionRun.setFontFamily("Times New Roman");
        captionRun.setText("Выдан организацией NetClinic");
        captionRun.addBreak();
        var formatToDocument = DateTimeFormatter.ofPattern("'«'dd'»' MMMM yyyy");
        captionRun.setText("дата выдачи: " + formatToDocument.format(date));
        captionRun.addBreak(BreakType.PAGE);
    }

    private static void addDocumentBody(XWPFDocument document, String patientFullName, String doctorFullName, String statement, LocalDate recoveryDate)
    {
        var documentBodyParagraph = document.createParagraph();
        var documentBodyRun = documentBodyParagraph.createRun();
        documentBodyRun.setFontSize(12);
        documentBodyRun.setFontFamily("Times New Roman");
        documentBodyRun.setText("Имя пациента: " + patientFullName);
        documentBodyRun.addBreak();
        documentBodyRun.setText("Ответственный врач: " + doctorFullName);
        documentBodyRun.addBreak();
        documentBodyRun.setText("Диагноз: " + statement);
        documentBodyRun.addBreak();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        documentBodyRun.setText("Приступить к работе начиная с: " + formatter.format(recoveryDate) + "г");
        documentBodyRun.addBreak();
    }

    private static void addSignature(XWPFDocument document)
    {
        var signatureParagraph = document.createParagraph();
        signatureParagraph.setAlignment(ParagraphAlignment.RIGHT);
        var preSignatureRun = signatureParagraph.createRun();
        preSignatureRun.setText("Подпись ответственного врача:");
        preSignatureRun.addBreak();
        var signatureRun = signatureParagraph.createRun();
        signatureRun.setUnderline(UnderlinePatterns.SINGLE);
        signatureRun.setText("                       ");
        var postSignatureRun = signatureParagraph.createRun();
        postSignatureRun.setText("  /                          /");
    }

    public static XWPFDocument createDocument(Appointment appointment, LocalDate recoveryDate)
    {
        Patient patient = appointment.getAppointmentRegistration().getPatient();
        Doctor doctor = appointment.getAppointmentRegistration().getDoctor();

        XWPFDocument retVal = new XWPFDocument();

        addFrontTitle(retVal);
        addCaption(retVal, LocalDate.now());
//        addDocumentBody(retVal, patient.getUser().getFullNameFormatted(), doctor.getUser().getFullNameFormatted(),
//                        appointment.getDocStatement(), recoveryDate);
        addSignature(retVal);

        return retVal;
    }
}
