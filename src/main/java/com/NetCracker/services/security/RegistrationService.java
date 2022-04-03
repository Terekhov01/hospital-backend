package com.NetCracker.services.security;

import com.NetCracker.entities.ConfirmationToken;
import com.NetCracker.entities.MedCard;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Room;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.entities.schedule.DoctorSchedule;
import com.NetCracker.entities.user.User;
import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.role.Role;
import com.NetCracker.payload.Request.DoctorDTO;
import com.NetCracker.payload.Request.PatientDTO;
import com.NetCracker.payload.Request.UserDTO;
import com.NetCracker.payload.Response.MessageResponse;
import com.NetCracker.repositories.ConfirmationTokenRepository;
import com.NetCracker.repositories.MedCardRepo;
import com.NetCracker.repositories.RoleRepository;
import com.NetCracker.repositories.patient.PatientRepository;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.services.EmailSenderService;
import com.NetCracker.services.PatientService;
import com.NetCracker.services.doctor.DoctorUserServiceImpl;
import com.NetCracker.services.doctor.RoomService;
import com.NetCracker.services.doctor.SpecialistService;
import com.NetCracker.services.schedule.ScheduleService;
import com.NetCracker.services.user.RoleService;
import com.NetCracker.services.user.UserService;
import com.NetCracker.utils.IncorrectRoleException;
import com.NetCracker.utils.IncorrectRoomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class RegistrationService
{
    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private DoctorUserServiceImpl doctorUserService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private MedCardRepo medCardRepo;

    public ResponseEntity<?> validateUserRegistrationData(UserDTO userSignUpRequest)
    {
        if (userRepository.existsByUserName(userSignUpRequest.getUserName()))
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Имя пользователя уже занято!"));
        }

        if (userRepository.existsByEmail(userSignUpRequest.getEmail()))
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Почтовый адрес уже используется. "));
        }

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendRegistrationEmail(String destinationMail, ConfirmationToken token, ModelAndView modelAndView) throws IOException
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(destinationMail);
        mailMessage.setSubject("Подтверждение регистрации!");
        mailMessage.setFrom("netclinictech@mail.ru");
        mailMessage.setText("Чтобы подтвердить аккаунт перейдите по ссылке "
                + "http://localhost:8080/api/auth/confirm-account?token="
                + token.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);

        modelAndView.addObject("email", destinationMail);

        modelAndView.setViewName("successfulRegistration");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User registerUser(UserDTO userDTO) throws IncorrectRoleException
    {
        Set<Role> persistedRoles = new HashSet<>();
        for (var roleStr : userDTO.getRoles())
        {
            ERole enumRole = null;
            try
            {
                enumRole = ERole.valueOf(roleStr);
            }
            catch (IllegalArgumentException e)
            {
                throw new IncorrectRoleException("Не удалось найти роль, соответствующую строке " + roleStr + ", " +
                        "требуемую для регистрауции");
            }

            Role role = null;
            role = roleService.findByName(enumRole);

            if (role == null)
            {
                role = new Role(enumRole);
                roleRepository.save(role);
            }
            persistedRoles.add(role);
        }

        User user = new User(userDTO.getFirstName(), userDTO.getLastName(),
                userDTO.getMiddleName(), userDTO.getPhone(), userDTO.getUserName(),
                userDTO.getEmail(), encoder.encode(userDTO.getPassword()), persistedRoles);
        userService.saveUser(user);

        return user;
    }

    @Transactional(propagation = Propagation.NESTED)
    public Doctor registerDoctor(DoctorDTO doctorSignupRequest, ModelAndView modelAndView) throws DataAccessException, IOException, IncorrectRoleException, IncorrectRoomException
    {
        Room room = null;
        if (doctorSignupRequest.getRoomNumber() != null)
        {
            room = roomService.findByNum(doctorSignupRequest.getRoomNumber());
            if (room == null)
            {
                throw new IncorrectRoleException("Нет информации о кабинете " + doctorSignupRequest.getRoomNumber() +
                        ". Создайте его, а затем повторите регистрацию.");
            }
        }

        Set<Specialist> specializationSet = new HashSet<>();
        for (var specializationStr : doctorSignupRequest.getSpecializations())
        {
            var specialization = specialistService.findBySpecialization(specializationStr);

            if (specialization == null)
            {
                throw new IllegalArgumentException("Нет информации об одной из указанных специализаций. " +
                        "Убедитесь в корректности данных, а затем повторите регистрацию.");
            }

            specializationSet.add(specialization);
        }

        var persistedUser = registerUser((UserDTO) doctorSignupRequest);
        var newDoctor = new Doctor(doctorSignupRequest.getEducation(), room, specializationSet, persistedUser, persistedUser.getId());
        //doctorUserService.save(newDoctor);

        var newSchedule = new DoctorSchedule(newDoctor);
        scheduleService.save(newSchedule);

        specializationSet.forEach(specialization -> specialization.getDoctors().add(newDoctor));

        sendRegistrationEmail(persistedUser.getEmail(), createToken(persistedUser), modelAndView);
        return newDoctor;
    }


    @Transactional
    public Patient registerPatient(PatientDTO patientSignupRequest, ModelAndView modelAndView) throws DataAccessException, IOException, IncorrectRoleException
    {
        var persistedUser = registerUser((UserDTO) patientSignupRequest);
        var newPatient = new Patient(persistedUser, patientSignupRequest.getPassport(), patientSignupRequest.getPolys());

        patientService.savePatient(newPatient);

        MedCard medCard = new MedCard(newPatient, "Отсутствуют", "Отсутствуют");
        medCardRepo.save(medCard);


        sendRegistrationEmail(persistedUser.getEmail(), createToken(persistedUser), modelAndView);

        return newPatient;
    }

    /*@Transactional
    public PatientRegistrationData registerPatient(PatientSignupRequest signUpRequestPatient) throws DataAccessException
    {
        // Create new user's account
        User user = new User(signUpRequestPatient.getFirstName(),
                signUpRequestPatient.getLastName(),
                signUpRequestPatient.getMiddleName(),
                signUpRequestPatient.getPhone(),
                signUpRequestPatient.getUsername(),
                signUpRequestPatient.getEmail(),
                encoder.encode(signUpRequestPatient.getPassword()));

        Set<String> strRoles = signUpRequestPatient.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role ->
            {
                switch (role)
                {
                    case "admin" ->
                    {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    case "mod" ->
                    {
                        Role modRole = roleRepository.findByName(ERole.ROLE_DOCTOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                    }
                    default ->
                    {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_DOCTOR)))
        {
            var doctor = new Doctor();
            doctorUserService.save(doctor);
            var doctorSchedule = new DoctorSchedule(doctor);
            scheduleService.save(doctorSchedule);
            doctor.setSchedule(doctorSchedule);
        }

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_PATIENT)))
        {
            Patient patient = new Patient(signUpRequestPatient.getPassport(),
                    signUpRequestPatient.getPolys());
            patient.setUser(user);
            patientRepository.save(patient);
        }

        var confirmationToken = createToken(user);

        return new PatientRegistrationData(user, confirmationToken);
    }*/

    @Transactional(propagation = Propagation.REQUIRED)
    public ConfirmationToken createToken(User user)
    {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        return confirmationToken;
    }
}
