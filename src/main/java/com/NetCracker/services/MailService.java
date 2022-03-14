package com.NetCracker.services;

public interface MailService {

    boolean sendSimpleEmail(final String toAddress, final String subject, final String message);

}
