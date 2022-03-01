package com.NetCracker.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.NetCracker.entities.ConfirmationToken;
import com.NetCracker.repositories.ConfirmationTokenRepository;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.payload.Response.JwtResponse;
import com.NetCracker.payload.Response.MessageResponse;
import com.NetCracker.repositories.patient.PatientRepository;
import com.NetCracker.security.jwt.JwtUtils;
import com.NetCracker.services.AuthenticationService;
import com.NetCracker.services.EmailSenderService;
import com.NetCracker.services.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.NetCracker.entities.user.ERole;
import com.NetCracker.entities.user.Role;
import com.NetCracker.entities.user.User;
import com.NetCracker.payload.Request.LoginRequest;
import com.NetCracker.payload.Request.SignupRequest;
import com.NetCracker.repositories.RoleRepository;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/auth")
@RestController
@ComponentScan("com.NetCracker.Security")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private EmailSenderService emailSenderService;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	AuthenticationService authenticationService;

	@Autowired
	ConfirmationTokenRepository confirmationTokenRepository;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, ModelAndView modelAndView) {
		if (userRepository.existsByUserName(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		AuthenticationService.UserRegistrationData registrationData = null;

		try
		{
			registrationData = authenticationService.registerUser(signUpRequest);
		}
		catch (DataAccessException e)
		{
			return new ResponseEntity<String>("Ошибка свяязи с базой данных. Попробуте позже",
											HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (Exception e)
		{
			return new ResponseEntity<String>("Неизвестная ошибка", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try
		{
			//TODO - extract this code to a separate mail service
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(registrationData.getUser().getEmail());
			mailMessage.setSubject("Подтверждение регистрации!");
			mailMessage.setFrom("netclinictech@mail.ru");
			mailMessage.setText("Чтобы подтвердить аккаунт перейдите по ссылке "
					+ "http://localhost:8080/api/auth/confirm-account?token="
					+ registrationData.getConfirmationToken().getConfirmationToken());

			emailSenderService.sendEmail(mailMessage);
		}
		catch (Exception e)
		{
			//TODO - handle error correctly. Is it possible to create a new user without e-mail confirmation?
			return new ResponseEntity<String>("Пользователь зарегистрирован, но письмо на почту не было отправлено", HttpStatus.OK);
		}

		modelAndView.addObject("email", registrationData.getUser().getEmail());

		modelAndView.setViewName("successfulRegistration");

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String confirmationToken,  HttpServletResponse response) throws IOException {
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

		if(token != null)
		{	response.sendRedirect("http://localhost:4200/");
			User user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
			user.setEnabled(true);
			userRepository.save(user);
			modelAndView.setViewName("accountVerified");
		}
		else
		{
			modelAndView.addObject("message","The link is invalid or broken!");
			modelAndView.setViewName("error");
		}

		return modelAndView;
	}
}
