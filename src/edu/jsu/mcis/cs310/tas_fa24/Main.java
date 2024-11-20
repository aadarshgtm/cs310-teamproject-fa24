package edu.jsu.mcis.cs310.tas_fa24;

import edu.jsu.mcis.cs310.tas_fa24.dao.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class Main {

    public static void main(String[] args) {
        
        // test database connectivity; get DAOs

        DAOFactory daoFactory = new DAOFactory("tas.jdbc");
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        // find badge

        //Badge b = badgeDAO.find("C4F37EFF");
        
        
        
        // output should be "Test Badge: #C4F37EFF (Welch, Travis C)"
        
        //System.err.println("Test Badge: " + b.toString());
        // Mikes comment for github testing 
        // Jeren added the comment 
        // Mishan is here guys!
        // Aadarsh added comment
        
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
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
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
        
        System.out.println("New Absenteeism Entry: " + a1);
        
        absenteeismDAO.create(a1);
        /* Retrieve Absenteeism From Database */
        
        Absenteeism a2 = absenteeismDAO.find(e, ts);
        
        System.out.println("Timestamp: " + ts);
        
        System.out.println("Existing Absenteeism Entry: " + a2);
        

    }

}
