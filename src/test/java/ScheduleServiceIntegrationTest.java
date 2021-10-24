import com.NetCracker.Entities.*;
import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.NetCracker.Main;
import com.NetCracker.Repositories.DoctorRepository;
import com.NetCracker.Repositories.DoctorScheduleRepository;
import com.NetCracker.Services.SchedulePatternFactory;
import com.NetCracker.Services.ScheduleService;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.time.Duration;

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
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private ScheduleService scheduleService;

    Doctor doctor1;
    Doctor doctor2;
    SchedulePattern commonWorkingPattern;
    LocalDate commonTimeStart;
    LocalDate modernTimeStart;
    DoctorSchedule commonSchedule;
    DoctorSchedule modernSchedule;

    ScheduleServiceIntegrationTest()
    {
        doctor1 = new Doctor();
        doctor2 = new Doctor();
        //TODO - refactor when doctor entity is ready to use
        commonWorkingPattern = SchedulePatternFactory.createCommonWorkingPattern("Common working pattern", 14, LocalTime.of(8, 0), LocalTime.of(17, 0));
        commonTimeStart = LocalDate.of(1970, 1, 1);
        commonSchedule = new DoctorSchedule(doctor1);
        modernTimeStart = LocalDate.of(2000, 1, 1);
        modernSchedule = new DoctorSchedule(doctor2);
    }

    @Transactional
    void initializeDatabaseWithTestData()
    {
        doctorRepository.save(doctor1);
        doctorRepository.save(doctor2);
        scheduleService.add(commonSchedule);
        scheduleService.add(modernSchedule);

        scheduleService.prolongScheduleByPattern(doctor1, commonWorkingPattern, commonTimeStart);
        scheduleService.prolongScheduleByPattern(doctor2, commonWorkingPattern, modernTimeStart);
    }

    @Test
    void testStatusGet()
    {
        initializeDatabaseWithTestData();
        ScheduleInterval status1 = scheduleService.getDoctorStatus(doctor1,
                LocalDateTime.of(1970, 1, 2, 11, 30, 0));
        ScheduleInterval status2 = scheduleService.getDoctorStatus(doctor1,
                LocalDateTime.of(1970, 1, 2, 17, 30, 0));

        Assertions.assertNotNull(status1);
        Assertions.assertFalse(status1.isAssigned());

        Assertions.assertNull(status2);
    }

    @Test
    void testStatusSet()
    {
        initializeDatabaseWithTestData();
        ScheduleInterval state = new ScheduleInterval(commonSchedule, commonTimeStart.atStartOfDay().plusHours(5).plusMinutes(30), true);

        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus st)
            {
                scheduleService.addInterval(state);
            }
        });

        ScheduleInterval actualStatus = scheduleService.getDoctorStatus(doctor1, commonTimeStart.atStartOfDay().plusHours(5).plusMinutes(30));

        Assertions.assertNotNull(actualStatus, "Status is null! Schedule was not updated!");
        Assertions.assertTrue(actualStatus.isAssigned());
    }

    TreeSet<ScheduleInterval> fillScheduleIntervalsTestValues(DoctorSchedule schedule, LocalDate startDate)
    {
        TreeSet<ScheduleInterval> expectedCommonSet = new TreeSet<>(ScheduleInterval.dateAscendComparator);
        for (LocalDateTime timeCnt = LocalDate.EPOCH.atStartOfDay(); timeCnt.isBefore(LocalDate.EPOCH.atStartOfDay().plusDays(14)); timeCnt = timeCnt.plusMinutes(30))
        {
            if (timeCnt.toLocalTime().compareTo(LocalTime.of(8, 0)) >= 0 && timeCnt.toLocalTime().compareTo(LocalTime.of(17, 0)) < 0)
            {
                expectedCommonSet.add(new ScheduleInterval(schedule, startDate.atStartOfDay().plus(Duration.between(LocalDate.EPOCH.atStartOfDay(), timeCnt)), true));
            }
        }

        return expectedCommonSet;
    }

    @Test
    void DBIntegrationTestCreate()
    {
        initializeDatabaseWithTestData();

        DoctorSchedule expectedSchedule = new DoctorSchedule(doctor1);

        TreeSet<ScheduleInterval> expectedSet = fillScheduleIntervalsTestValues(expectedSchedule, commonTimeStart);

        expectedSchedule.setStateSet(expectedSet);

        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                DoctorSchedule loadedSchedule = scheduleService.getDoctorSchedule(doctor1);
                expectedSchedule.setId(loadedSchedule.getId());
                Assertions.assertEquals(expectedSchedule, loadedSchedule);
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
                scheduleService.getAllDoctorSchedules().forEach(doctorSchedule -> doctorSchedule.getStateSet().forEach(state ->
                {
                    state.setAssigned(true);
                }));

                /*ScheduleInterval x = scheduleService.getDoctorSchedule(doctor1).getStateSet().iterator().next();

                ScheduleInterval y = new ScheduleInterval(commonSchedule, commonTimeStart.atStartOfDay().plusHours(8), true);

                var a = x.hashCode();
                var aa = y.hashCode();
                var aaa = x.equals(y);

                //scheduleService.getDoctorSchedule(doctor1).getStateSet().forEach(Hibernate::initialize);

                boolean c = scheduleService.getDoctorSchedule(doctor1).getStateSet().contains(y);*/


                DoctorSchedule commonLoadedSchedule = scheduleService.getDoctorSchedule(doctor1);
                DoctorSchedule modernLoadedSchedule = scheduleService.getDoctorSchedule(doctor2);

                Set<ScheduleInterval> expectedCommon = fillScheduleIntervalsTestValues(commonLoadedSchedule, commonTimeStart);
                Set<ScheduleInterval> expectedModern = fillScheduleIntervalsTestValues(modernLoadedSchedule, modernTimeStart);

                Assertions.assertEquals(expectedCommon.size(), commonLoadedSchedule.getStateSet().size());
                //Assertions.assertArrayEquals(expectedCommon.toArray(), commonLoadedSchedule.getStateSet().toArray());
                MatcherAssert.assertThat(commonLoadedSchedule.getStateSet(), containsInAnyOrder(expectedCommon.toArray()));

                Assertions.assertEquals(expectedModern.size(), modernLoadedSchedule.getStateSet().size());
                //Assertions.assertArrayEquals(expectedModern.toArray(), modernLoadedSchedule.getStateSet().toArray());
                MatcherAssert.assertThat(modernLoadedSchedule.getStateSet(), containsInAnyOrder(expectedModern.toArray()));

                /*Assertions.assertEquals(expectedCommon, commonLoadedSchedule.getStateSet());
                Assertions.assertEquals(expectedModern, modernLoadedSchedule.getStateSet());*/
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
                scheduleService.remove(scheduleService.getAllDoctorSchedules());
                List<DoctorSchedule> loadedSchedulesList = doctorScheduleRepository.findAll();
                Assertions.assertEquals(0, loadedSchedulesList.size());
            }
        });
    }
}
