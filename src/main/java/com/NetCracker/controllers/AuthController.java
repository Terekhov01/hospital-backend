package com.NetCracker.controllers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.NetCracker.entities.ConfirmationToken;
import com.NetCracker.payload.Request.DoctorDTO;
import com.NetCracker.payload.Request.UserDTO;
import com.NetCracker.repositories.ConfirmationTokenRepository;
import com.NetCracker.repositories.MedCardRepo;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.payload.Response.JwtResponse;
import com.NetCracker.repositories.patient.PatientRepository;
import com.NetCracker.security.jwt.JwtUtils;
import com.NetCracker.services.security.AuthenticationService;
import com.NetCracker.services.EmailSenderService;
import com.NetCracker.services.security.RegistrationService;
import com.NetCracker.services.user.UserDetailsImpl;
import com.NetCracker.utils.IncorrectRoleException;
import com.NetCracker.utils.IncorrectRoomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.NetCracker.entities.user.User;
import com.NetCracker.payload.Request.LoginRequest;
import com.NetCracker.payload.Request.PatientDTO;
import com.NetCracker.repositories.RoleRepository;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
@RestController
@ComponentScan("com.NetCracker.Security")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	MedCardRepo medCardRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	AuthenticationService authenticationService;

	@Autowired
	RegistrationService registrationService;

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private EmailSenderService emailSenderService;

	@Autowired
	JwtUtils jwtUtils;

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

	@PostMapping("/signup/doctor")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorDTO doctorSignupRequest, ModelAndView modelAndView)
	{
		var validationResult = registrationService.validateUserRegistrationData((UserDTO) doctorSignupRequest);
		if (validationResult != null)
		{
			return validationResult;
		}

		try
		{
			registrationService.registerDoctor(doctorSignupRequest, modelAndView);
		}
		catch (DataAccessException e)
		{
			return new ResponseEntity<String>("Ошибка свяязи с базой данных. Попробуте позже",
					HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (IncorrectRoleException | IncorrectRoomException e)
		{
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (IOException e)
		{
			return new ResponseEntity<>("Ошибка отправки сообщения на электронную почту. Проверьте корректность" +
					" введенных данных и попробуйте еще раз", HttpStatus.BAD_GATEWAY);
		}
		catch (Exception e)
		{
			return new ResponseEntity<String>("Неизвестная ошибка", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseEntity.ok().build();
	}

	@PostMapping("/signup/patient")
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientDTO patientSignupRequest, ModelAndView modelAndView)
	{
		var validationResult = registrationService.validateUserRegistrationData((UserDTO) patientSignupRequest);
		if (validationResult != null)
		{
			return validationResult;
		}

		try
		{
			registrationService.registerPatient(patientSignupRequest, modelAndView);
		}
		catch (DataAccessException e)
		{
			return new ResponseEntity<>("Ошибка свяязи с базой данных. Попробуте позже",
											HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (IncorrectRoleException e)
		{
			return new ResponseEntity<>("Получены некорректные данные: роли полозователя указаны неверно", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (MailAuthenticationException | MailSendException | IOException e)
		{
			return new ResponseEntity<>("Ошибка отправки сообщения на электронную почту. Проверьте корректность " +
					"введенных данных и попробуйте еще раз", HttpStatus.BAD_GATEWAY);
		}
		catch (Exception e)
		{
			return new ResponseEntity<>("Неизвестная ошибка", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseEntity.ok().build();
	}

	@RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String confirmationToken,  HttpServletResponse response) throws IOException {
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

		if(token != null)
		{	response.sendRedirect("hospital.up.railway.app");
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
