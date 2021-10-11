import com.NetCracker.Entities.*;
import com.NetCracker.Main;
import com.NetCracker.Repositories.DoctorRepository;
import com.NetCracker.Repositories.DoctorScheduleRepository;
import com.NetCracker.Services.SchedulePatternFactory;
import com.NetCracker.Services.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <br>DISCLAIMER<br/>
 * These are an integration tests.
 * That means that functions below WILL CHANGE your DATABASE state!
 * Use them wisely.
 * Best way to test your application if database already has important information is to create a new one and change
 * file application.properties to point at a new test database
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Main.class})
public class ScheduleServiceIntegrationTest
{
    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    TransactionTemplate txTemplate;

    @Autowired
    private ScheduleService scheduleService;

    Doctor doctor1;
    Doctor doctor2;
    SchedulePattern commonWorkingPattern;
    DoctorSchedule commonSchedule;
    DoctorSchedule modernSchedule;

    ScheduleServiceIntegrationTest()
    {
        commonWorkingPattern = SchedulePatternFactory.createCommonWorkingPattern(8, 17);
        commonSchedule = new DoctorSchedule(LocalDate.EPOCH);
        modernSchedule = new DoctorSchedule(LocalDate.of(2000, 1, 1));
    }

    @Transactional
    void initializeDatabaseWithTestData()
    {
        doctor1 = new Doctor();
        doctor2 = new Doctor();
        //TODO - refactor when doctor entity is ready to use
        doctorRepository.save(doctor1);
        doctorRepository.save(doctor2);
        scheduleService.addDoctorSchedule(doctor1, commonSchedule);
        scheduleService.addDoctorSchedule(doctor2, modernSchedule);

        scheduleService.prolongScheduleByPattern(doctor1, commonWorkingPattern);
        scheduleService.prolongScheduleByPattern(doctor2, commonWorkingPattern);
    }

    @Test
    void testStatusGet()
    {
        initializeDatabaseWithTestData();
        ScheduleState status1 = scheduleService.getStatus(doctor1,
                LocalDateTime.of(1970, 1, 2, 11, 30, 0));
        ScheduleState status2 = scheduleService.getStatus(doctor1,
                LocalDateTime.of(1970, 1, 2, 17, 30, 0));

        Assertions.assertNotNull(status1);
        Assertions.assertEquals(status1.getState(), 1);

        Assertions.assertNotNull(status2);
        Assertions.assertEquals(status2.getState(), 0);
    }

    @Test
    void testStatusSet()
    {
        initializeDatabaseWithTestData();
        ScheduleState status = new ScheduleState(true, true);

        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus st)
            {
                scheduleService.getDoctorSchedule(doctor1).getIntervalStatusList().set(11, status);
            }
        });

        ScheduleState actualStatus = scheduleService.getStatus(doctor1,
                LocalDateTime.of(1970, 1, 1, 5, 30, 0));

        Assertions.assertNotNull(actualStatus, "Status is null!");
        Assertions.assertTrue(actualStatus.getIsWorking());
        Assertions.assertTrue(actualStatus.getIsBusy());
    }

    @Test
    void DBIntegrationTestCreate()
    {
        initializeDatabaseWithTestData();
        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                doctorScheduleRepository.saveAll(scheduleService.getAllDoctorSchedules());
                DoctorSchedule loadedSchedule = doctorScheduleRepository.getById(scheduleService.getDoctorSchedule(doctor1).getId());
                Assertions.assertEquals(scheduleService.getDoctorSchedule(doctor1), loadedSchedule);
            }
        });
    }

    @Test
    void DBIntegrationTestUpdate()
    {
        initializeDatabaseWithTestData();

        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus st)
            {
                // Updating contents, setting both doctors to always work and be busy
                scheduleService.getAllDoctorSchedules().forEach(doctorSchedule -> doctorSchedule.getIntervalStatusList().forEach(status ->
                {
                    status.setWorking(true);
                    status.setBusy(true);
                }));

                doctorScheduleRepository.saveAll(scheduleService.getAllDoctorSchedules());
                DoctorSchedule commonLoadedSchedule = doctorScheduleRepository.findByRelatedDoctor(doctor1);
                DoctorSchedule modernLoadedSchedule = doctorScheduleRepository.findByRelatedDoctor(doctor2);

                List<ScheduleState> expected = new ArrayList<>();
                for (int i = 0; i < 672; i++)
                {
                    expected.add(new ScheduleState(true, true));
                }

                Assertions.assertEquals(expected, commonLoadedSchedule.getIntervalStatusList());
                Assertions.assertEquals(expected, modernLoadedSchedule.getIntervalStatusList());
            }
        });
    }

    @Test
    void DBIntegrationTestDelete()
    {
        initializeDatabaseWithTestData();
        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                doctorScheduleRepository.deleteAll(scheduleService.getAllDoctorSchedules());
                List<DoctorSchedule> loadedSchedulesList = doctorScheduleRepository.findAll();
                Assertions.assertEquals(0, loadedSchedulesList.size());
            }
        });
    }
}
