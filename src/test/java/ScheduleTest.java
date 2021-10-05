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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ==========DISCLAIMER==========
 * WARNING! These are integration tests!
 * Do not launch them if you have valuable data in database, and you haven't changed application.properties file.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Main.class})
public class ScheduleTest
{
    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    TransactionTemplate txTemplate;

    @Autowired
    private ScheduleMaintenanceService scheduleService;

    @Test
    void testAddingDoctorToSchedule1()
    {
        Doctor doctor = new Doctor();
        DoctorWorkingPattern pattern = ScheduleMaintenanceService.createCommonWorkingPattern();
        DoctorSchedule doctorSchedule = new DoctorSchedule(LocalDate.EPOCH);
        ScheduleMaintenanceService.prolongScheduleByWorkingPattern(doctorSchedule, pattern);
        ScheduleStatus status = ScheduleMaintenanceService.getStatus(doctorSchedule, LocalDateTime.of(1970, 1, 2, 11, 30, 0));
        ScheduleStatus status2 = ScheduleMaintenanceService.getStatus(doctorSchedule, LocalDateTime.of(1970, 1, 2, 17, 30, 0));
        Assertions.assertEquals(Objects.requireNonNull(status).getStatus(), 1);
        Assertions.assertEquals(Objects.requireNonNull(status2).getStatus(), 0);

        Schedule schedule = new Schedule();
        schedule.addDoctorSchedule(doctor, doctorSchedule);

        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                scheduleService.forcePersistSchedule(schedule);
                DoctorSchedule loadedSchedule = doctorScheduleRepository.getById(doctorSchedule.getId());
                Assertions.assertEquals(doctorSchedule, loadedSchedule);
            }
        });
    }
}
