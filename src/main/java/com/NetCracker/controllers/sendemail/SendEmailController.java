package com.NetCracker.controllers.sendemail;

import com.NetCracker.domain.DTO.EmailDto;
import com.NetCracker.services.MailService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@AllArgsConstructor
@RequestMapping({"/sendEmail"})
public class SendEmailController {

    private final MailService mailService;


    @PreAuthorize("permitAll()")
    @PostMapping
    public void sendEmail(@RequestBody EmailDto emailDto) {

        System.out.println();

        String messageBody ="почтовый адрес: " + emailDto.getEmail()+"\n" +
                "телефон :" + emailDto.getPhone() +"\n" +
                "Обращение :" + emailDto.getMessage()+"\n ";
    if(StringUtils.hasText(emailDto.getEmail()) && StringUtils.hasText(emailDto.getPhone())&& StringUtils.hasText(emailDto.getMessage())&& StringUtils.hasText(emailDto.getFirstname())){
        mailService.sendSimpleEmail("008_1998@mail.ru", "Обращение от : "+ emailDto.getFirstname(), messageBody);
    }

    }
}
