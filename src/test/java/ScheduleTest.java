import com.NetCracker.Entities.*;
import com.NetCracker.Main;
import com.NetCracker.Repositories.DoctorScheduleRepository;
import com.NetCracker.Services.ScheduleMaintenanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Objects;

/**
 * <br>==========DISCLAIMER==========<br/>
 *
 * WARNING! These tests contain an integration test!
 * Do not launch them if you have valuable data in database, and you haven't changed application.properties file.
 * Or you can simply comment "DBIntegrationTest"
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Main.class})
public class ScheduleTest
{
    @Autowired
    DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    TransactionTemplate txTemplate;

    @Autowired
    ScheduleMaintenanceService scheduleService;

    Doctor doctor;
    DoctorSchedulePattern schedulePattern_8_17;
    DoctorSchedule doctorCommonSchedule;
    Schedule unifiedSchedule;

    ScheduleTest()
    {
        //TODO - refactor when doctor entity is ready to use
        doctor = new Doctor();
        schedulePattern_8_17 = ScheduleMaintenanceService.createCommonWorkingPattern();
        doctorCommonSchedule = new DoctorSchedule(LocalDate.EPOCH);
        ScheduleMaintenanceService.prolongScheduleByWorkingPattern(doctorCommonSchedule, schedulePattern_8_17);

        unifiedSchedule = new Schedule();
        unifiedSchedule.addDoctorSchedule(doctor, doctorCommonSchedule);
    }

    @Test
    void testStatusGet()
    {
        ScheduleStatus status = ScheduleMaintenanceService.getStatus(doctorCommonSchedule,
                LocalDateTime.of(1970, 1, 2, 11, 30, 0));
        ScheduleStatus status2 = ScheduleMaintenanceService.getStatus(doctorCommonSchedule,
                LocalDateTime.of(1970, 1, 2, 17, 30, 0));

        Assertions.assertEquals(Objects.requireNonNull(status).getStatus(), 1);
        Assertions.assertEquals(Objects.requireNonNull(status2).getStatus(), 0);
    }

    @Test
    void testStatusSet()
    {
        ScheduleStatus status = new ScheduleStatus(true, true);

        doctorCommonSchedule.getIntervalStatusList().set(11, status);

        Assertions.assertTrue(Objects.requireNonNull(ScheduleMaintenanceService.getStatus(doctorCommonSchedule,
                LocalDateTime.of(1970, 1, 1, 5, 30, 0))).getIsWorking());

        Assertions.assertTrue(Objects.requireNonNull(ScheduleMaintenanceService.getStatus(doctorCommonSchedule,
                LocalDateTime.of(1970, 1, 1, 5, 30, 0))).getIsBusy());
    }

    /**
     * This function will change your database state!
     * Use it wisely
     */
    @Test
    void DBIntegrationTest()
    {
        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                scheduleService.forcePersistSchedule(unifiedSchedule);
                DoctorSchedule loadedSchedule = doctorScheduleRepository.getById(doctorCommonSchedule.getId());
                Assertions.assertEquals(doctorCommonSchedule, loadedSchedule);
            }
        });
    }
}
