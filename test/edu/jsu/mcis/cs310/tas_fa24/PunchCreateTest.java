package edu.jsu.mcis.cs310.tas_fa24;

import edu.jsu.mcis.cs310.tas_fa24.dao.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.*;
import static org.junit.Assert.*;

public class PunchCreateTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testCreatePunch1() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PunchDAO punchDAO = daoFactory.getPunchDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Punch Object */
        
        Punch p1 = new Punch(103, badgeDAO.find("021890C0").getId(), EventType.CLOCK_IN);

        /* Create Timestamp Objects */
        
        LocalDateTime ots, rts;

        /* Get Punch Properties */
        
        String badgeid = p1.getBadgeBadge().getId();
        ots = p1.getOriginaltimestamp();
        int terminalid = p1.getTerminalid();
        EventType punchtype = p1.getPunchtype();

        /* Insert Punch Into Database */
        
        int punchid = punchDAO.create(p1);

        /* Retrieve New Punch */
        
        Punch p2 = punchDAO.find(punchid);

        /* Compare Punches */
        
        assertEquals(badgeid, p2.getBadgeBadge().getId());

        rts = p2.getOriginaltimestamp();

        assertEquals(terminalid, p2.getTerminalid());
        assertEquals(punchtype, p2.getPunchtype());
        assertEquals(ots.format(dtf), rts.format(dtf));

    }

}
