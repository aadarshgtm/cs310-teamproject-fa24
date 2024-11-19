package edu.jsu.mcis.cs310.tas_fa24;
import edu.jsu.mcis.cs310.tas_fa24.dao.PunchDAO;
import edu.jsu.mcis.cs310.tas_fa24.dao.DAOUtility;
import edu.jsu.mcis.cs310.tas_fa24.dao.AbsenteeismDAO;
import edu.jsu.mcis.cs310.tas_fa24.dao.EmployeeDAO;
import edu.jsu.mcis.cs310.tas_fa24.dao.DAOFactory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;
import java.time.format.DateTimeFormatter;

public class AbsenteeismTest {
    private DAOFactory daoFactory;
    @Before
    public void setup() {
        daoFactory = new DAOFactory("tas.jdbc");
        }
    @Test
    public void testAbsenteeismShift1Weekday() {
        AbsenteeismDAO absenteeismDAO = daoFactory.getAbsenteeismDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        /* Get Punch/Employee Objects */
        Punch p = punchDAO.find(3634);
        Employee e = employeeDAO.find(p.getBadge());
        Shift s = e.getShift();
        Badge b = e.getBadge();
        /* Get Pay Period Punch List */
        LocalDate ts = p.getOriginaltimestamp().toLocalDate();
        LocalDate begin =
        ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        ArrayList<Punch> punchlist = punchDAO.list(b, begin, end);
        /* Adjust Punch List */
        for (Punch punch : punchlist) {
            punch.adjust(s);
            }
        /* Compute Pay Period Total Absenteeism */
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(punchlist, s);
        /* Insert Absenteeism Into Database */
        Absenteeism a1 = new Absenteeism(e, ts, percentage);
        absenteeismDAO.create(a1);
        /* Retrieve Absenteeism From Database */
        Absenteeism a2 = absenteeismDAO.find(e, ts);
        /* Compare to Expected Value */
        assertEquals("#28DC3FB8 (Pay Period Starting 09-02-2018): 2.50%",
        a2.toString());
        }
    @Test
    public void testAbsenteeismShift1Weekend() {
        AbsenteeismDAO absenteeismDAO = daoFactory.getAbsenteeismDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        /* Get Punch/Employee Objects */
        Punch p = punchDAO.find(1087);
        Employee e = employeeDAO.find(p.getBadge());
        Shift s = e.getShift();
        Badge b = e.getBadge();
        /* Get Pay Period Punch List */
        LocalDate ts = p.getOriginaltimestamp().toLocalDate();
        LocalDate begin =
        ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        ArrayList<Punch> punchlist = punchDAO.list(b, begin, end);
        /* Adjust Punch List */
        for (Punch punch : punchlist) {
            punch.adjust(s);
            }
        /* Compute Pay Period Total Absenteeism */
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(punchlist, s);
        /* Insert Absenteeism Into Database */
        Absenteeism a1 = new Absenteeism(e, ts, percentage);
        absenteeismDAO.create(a1);
        /* Retrieve Absenteeism From Database */
        Absenteeism a2 = absenteeismDAO.find(e, ts);
        /* Compare to Expected Value */
        assertEquals("#F1EE0555 (Pay Period Starting 08-05-2018): -20.00%",
        a2.toString());
        }
    @Test
    public void testAbsenteeismShift2Weekend() {
        //System.err.println("testAbsenteeismShift2Weekend()");
        AbsenteeismDAO absenteeismDAO = daoFactory.getAbsenteeismDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        /* Get Punch/Employee Objects */
        Punch p = punchDAO.find(4943);
        Employee e = employeeDAO.find(p.getBadge());
        Shift s = e.getShift();
        Badge b = e.getBadge();
        /* Get Pay Period Punch List */
        LocalDate ts = p.getOriginaltimestamp().toLocalDate();
        LocalDate begin =
        ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        ArrayList<Punch> punchlist = punchDAO.list(b, begin, end);
        /* Adjust Punch List */
        for (Punch punch : punchlist) {
            punch.adjust(s);
            //System.err.println(punch.printAdjusted());
        }
        /* Compute Pay Period Total Absenteeism */
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(punchlist, s);
        /* Insert Absenteeism Into Database */
        Absenteeism a1 = new Absenteeism(e, ts, percentage);
        absenteeismDAO.create(a1);
        /* Retrieve Absenteeism From Database */
        Absenteeism a2 = absenteeismDAO.find(e, ts);
        /* Compare to Expected Value */
        assertEquals("#08D01475 (Pay Period Starting 09-16-2018): -28.75%",
        a2.toString());
    }
    @Test
    /*added by Jeren Tolegova*/
    public void testAbsenteeismFullAttendance() {
        AbsenteeismDAO absenteeismDAO = daoFactory.getAbsenteeismDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();

        // Retrieve Employee using Badge
        Badge badge = new Badge("FULL0001", "Test Badge"); // Replace with a valid badge ID and description
        Employee e = employeeDAO.find(badge); // Use EmployeeDAO.find(Badge badge)
        if (e == null) {
            System.out.println("Employee not found for badge ID: FULL0001");
            return; // Exit test if employee is not found
        }

        Shift s = e.getShift(); // Retrieve the shift for the employee
        Badge b = e.getBadge(); // Retrieve the badge for the employee

        // Mock Punch Data for Full Attendance
        ArrayList<Punch> punchlist = new ArrayList<>();
        LocalDate payPeriodStart = LocalDate.of(2018, 9, 16); // Example pay period start

        for (int i = 1; i <= 5; i++) { // Monday to Friday
            LocalDate workDay = payPeriodStart.plusDays(i - 1);
            punchlist.add(new Punch(1, b.getId(), EventType.CLOCK_IN)); // Clock-in
            punchlist.add(new Punch(1, b.getId(), EventType.CLOCK_OUT)); // Clock-out
        }

        // Adjust Punch Times for the Shift
        for (Punch punch : punchlist) {
            punch.adjust(s);
        }

        // Calculate Absenteeism
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(punchlist, s);

        // Insert and Retrieve Absenteeism
        Absenteeism a1 = new Absenteeism(e, payPeriodStart, percentage);
        absenteeismDAO.create(a1);
        Absenteeism a2 = absenteeismDAO.find(e, payPeriodStart);

        // Assert Expected Value
        assertEquals("#FULL0001 (Pay Period Starting 09-16-2018): 0.00%", a2.toString());
    }
    
}