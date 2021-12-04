package com.NetCracker.Controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.NetCracker.Repositories.UserRepository;
import com.NetCracker.Entities.Patient;
import com.NetCracker.Payload.Response.JwtResponse;
import com.NetCracker.Payload.Response.MessageResponse;
import com.NetCracker.Repositories.PatientRepository;
import com.NetCracker.Security.jwt.JwtUtils;
import com.NetCracker.Services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NetCracker.Entities.ERole;
import com.NetCracker.Entities.Role;
import com.NetCracker.Entities.User;
import com.NetCracker.Payload.Request.LoginRequest;
import com.NetCracker.Payload.Request.SignupRequest;
import com.NetCracker.Repositories.RoleRepository;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/auth")
@RestController
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
	PasswordEncoder encoder;

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

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getFirstName(),
				signUpRequest.getLastName(),
				signUpRequest.getPatronymic(),
				signUpRequest.getPhone(),
				signUpRequest.getUsername(),
				signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Patient patient = new Patient(signUpRequest.getPassport(),
				signUpRequest.getPolys());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);

						break;
					case "mod":
						Role modRole = roleRepository.findByName(ERole.ROLE_DOCTOR)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(modRole);

						break;
					default:
						Role userRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		patient.setUser(user);
		patientRepository.save(patient);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
