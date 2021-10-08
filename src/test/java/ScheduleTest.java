import com.NetCracker.Entities.*;
import com.NetCracker.Main;
import com.NetCracker.Repositories.DoctorScheduleRepository;
import com.NetCracker.Services.ScheduleMaintenanceService;
import org.junit.jupiter.api.Assertions;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <br>==========DISCLAIMER==========<br/>
 *
 * WARNING! These tests contain integration tests!
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

    Doctor doctor1;
    Doctor doctor2;
    DoctorSchedulePattern schedulePattern_8_17;
    DoctorSchedule commonSchedule;
    DoctorSchedule modernSchedule;
    Schedule unifiedSchedule;

    ScheduleTest()
    {
        //TODO - refactor when doctor entity is ready to use
        doctor1 = new Doctor();
        doctor2 = new Doctor();
        schedulePattern_8_17 = ScheduleMaintenanceService.createCommonWorkingPattern();
        commonSchedule = new DoctorSchedule(LocalDate.EPOCH);
        modernSchedule = new DoctorSchedule(LocalDate.of(2000, 1, 1));
        ScheduleMaintenanceService.prolongScheduleByWorkingPattern(commonSchedule, schedulePattern_8_17);
        ScheduleMaintenanceService.prolongScheduleByWorkingPattern(modernSchedule, schedulePattern_8_17);

        unifiedSchedule = new Schedule();
        unifiedSchedule.addDoctorSchedule(doctor1, commonSchedule);
        unifiedSchedule.addDoctorSchedule(doctor2, modernSchedule);
    }

    @Test
    void testStatusGet()
    {
        ScheduleStatus status1 = ScheduleMaintenanceService.getStatus(commonSchedule,
                LocalDateTime.of(1970, 1, 2, 11, 30, 0));
        ScheduleStatus status2 = ScheduleMaintenanceService.getStatus(commonSchedule,
                LocalDateTime.of(1970, 1, 2, 17, 30, 0));

        Assertions.assertNotNull(status1);
        Assertions.assertEquals(status1.getStatus(), 1);

        Assertions.assertNotNull(status2);
        Assertions.assertEquals(Objects.requireNonNull(status2).getStatus(), 0);
    }

    @Test
    void testStatusSet()
    {
        ScheduleStatus status = new ScheduleStatus(true, true);

        commonSchedule.getIntervalStatusList().set(11, status);

        ScheduleStatus actualStatus = ScheduleMaintenanceService.getStatus(commonSchedule,
                LocalDateTime.of(1970, 1, 1, 5, 30, 0));

        Assertions.assertNotNull(actualStatus, "Status is null!");
        Assertions.assertTrue(actualStatus.getIsWorking());
        Assertions.assertTrue(actualStatus.getIsBusy());
    }

    /**
     * These functions will change your database state!
     * Use them wisely
     */
    @Test
    void DBIntegrationTestCreate()
    {
        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                doctorScheduleRepository.saveAll(unifiedSchedule.getAllDoctorSchedules());
                DoctorSchedule loadedSchedule = doctorScheduleRepository.getById(commonSchedule.getId());
                Assertions.assertEquals(commonSchedule, loadedSchedule);
            }
        });
    }

    @Test
    void DBIntegrationTestUpdate()
    {
        DBIntegrationTestCreate();
        // Updating contents, setting both doctors to always work and be busy
        unifiedSchedule.getAllDoctorSchedules().forEach(doctorSchedule -> doctorSchedule.getIntervalStatusList().forEach(status ->
                                        {
                                            status.setWorking(true);
                                            status.setBusy(true);
                                        }));

        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                doctorScheduleRepository.saveAll(unifiedSchedule.getAllDoctorSchedules());
                DoctorSchedule commonLoadedSchedule = doctorScheduleRepository.getById(commonSchedule.getId());
                DoctorSchedule modernLoadedSchedule = doctorScheduleRepository.getById(modernSchedule.getId());

                List<ScheduleStatus> expected = new ArrayList<>();
                for (int i = 0; i < 672; i++)
                {
                    expected.add(new ScheduleStatus(true, true));
                }

                Assertions.assertEquals(expected, commonLoadedSchedule.getIntervalStatusList());
                Assertions.assertEquals(expected, modernLoadedSchedule.getIntervalStatusList());
            }
        });
    }

    @Test
    void DBIntegrationTestDelete()
    {
        DBIntegrationTestCreate();
        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                doctorScheduleRepository.deleteAll(unifiedSchedule.getAllDoctorSchedules());
                List<DoctorSchedule> loadedSchedulesList = doctorScheduleRepository.findAll();
                Assertions.assertEquals(0, loadedSchedulesList.size());
            }
        });
    }
}
