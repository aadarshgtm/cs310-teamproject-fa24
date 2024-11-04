package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class Punch {
    private int id;
    private int terminalid;
    private Badge badge;
    private LocalDateTime originaltimestamp;
    private LocalDateTime adjustedtimestamp;
    private int punchtype;
    private EventType punchtype1; 
    private PunchAdjustmentType adjustmenttype;

    public Punch(int terminalid, String badgeId, EventType punchtype1) {
        this.terminalid = terminalid;
        this.badge = new Badge(badgeId, "");
        this.punchtype = punchtype1.ordinal();
        this.punchtype1 = punchtype1;
    }

    public Punch(int id, int terminalid, String badgeId, LocalDateTime originaltimestamp, int punchtype) {
        this.id = id; 
        this.terminalid = terminalid;
        this.badge = new Badge(badgeId, "");
        this.originaltimestamp = originaltimestamp;
        this.punchtype = punchtype;
    }

    // Getters
    public int getId() { return id; }
    public EventType getPunchType1() { return punchtype1; }
    public int getTerminalid() { return terminalid; }
    public String getBadge() { return badge.getId(); }
    public Badge getBadgeBadge() { return badge; }
    public LocalDateTime getOriginaltimestamp() { return originaltimestamp; }
    public int getPunchtype() { return punchtype; }
    public LocalDateTime getAdjustedtimestamp() { return adjustedtimestamp; }
    public PunchAdjustmentType getAdjustmenttype() { return adjustmenttype; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTerminalid(int terminalid) { this.terminalid = terminalid; }
    public void setBadge(String badgeId) { this.badge = new Badge(badgeId, ""); }
    public void setOriginaltimestamp(LocalDateTime originaltimestamp) { this.originaltimestamp = originaltimestamp; }
    public void setAdjustedtimestamp(LocalDateTime adjustedtimestamp) { this.adjustedtimestamp = adjustedtimestamp; }
    public void setPunchtype(int punchtype) { this.punchtype = punchtype; }
    public void setAdjustmenttype(PunchAdjustmentType adjustmenttype) { this.adjustmenttype = adjustmenttype; }

    public String printOriginal() {
        String badgeId = getBadge();
        String punchTypeVal = switch (this.punchtype) {
            case 0 -> "CLOCK OUT";
            case 1 -> "CLOCK IN";
            case 2 -> "TIME OUT";
            default -> "Unknown type";
        };

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("EEE MM/dd/yyyy HH:mm:ss")
            .toFormatter(Locale.ENGLISH);

        String formattedDateTime = originaltimestamp.format(formatter);
        String dayOfWeekUpper = formattedDateTime.substring(0, 3).toUpperCase();
        formattedDateTime = dayOfWeekUpper + formattedDateTime.substring(3);

        return "#" + badgeId + " " + punchTypeVal + ": " + formattedDateTime;
    }

    public String printAdjusted() {
        if (this.adjustedtimestamp == null) { return ""; }

        String badgeId = getBadge();
        String punchTypeVal = switch (this.punchtype) {
            case 0 -> "CLOCK OUT";
            case 1 -> "CLOCK IN";
            case 2 -> "TIME OUT";
            default -> "Unknown type";
        };

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("EEE MM/dd/yyyy HH:mm:ss")
            .toFormatter(Locale.ENGLISH);

        String formattedDateTime = adjustedtimestamp.format(formatter);
        String dayOfWeekUpper = formattedDateTime.substring(0, 3).toUpperCase();
        formattedDateTime = dayOfWeekUpper + formattedDateTime.substring(3);

        return "#" + badgeId + " " + punchTypeVal + ": " + formattedDateTime + " (" + adjustmenttype + ")";
    }

public void adjust(Shift s) {
    // Initialize adjusted timestamp
    this.adjustedtimestamp = this.originaltimestamp;
    
    // Handle TIME OUT
    if (this.punchtype == 2) {
        this.adjustedtimestamp = this.originaltimestamp;
        this.adjustmenttype = PunchAdjustmentType.NONE;
        return;
    }

    // Handle weekends (Saturday = 6, Sunday = 7)
    int dayOfWeek = this.originaltimestamp.getDayOfWeek().getValue();
    if (dayOfWeek == 6 || dayOfWeek == 7) {
        roundToNearestInterval(s);
        this.adjustmenttype = PunchAdjustmentType.INTERVAL_ROUND;
        return;
    }

    // Get all relevant times
    LocalDateTime shiftStart = getShiftStartForDay(s);
    LocalDateTime shiftStop = getShiftStopForDay(s);
    LocalDateTime lunchStart = getLunchStartForDay(s);
    LocalDateTime lunchStop = getLunchStopForDay(s);

    // Calculate time differences
    long minutesFromStart = java.time.Duration.between(shiftStart, this.originaltimestamp).toMinutes();
    long minutesFromStop = java.time.Duration.between(this.originaltimestamp, shiftStop).toMinutes();
    long minutesFromLunchStart = java.time.Duration.between(lunchStart, this.originaltimestamp).toMinutes();
    long minutesFromLunchStop = java.time.Duration.between(this.originaltimestamp, lunchStop).toMinutes();

    // CLOCK IN
    if (this.punchtype == 1) {
        // Check lunch period first
        if (isWithinInterval(minutesFromLunchStop, s.getRoundingInterval())) {
            this.adjustedtimestamp = lunchStop;
            this.adjustmenttype = PunchAdjustmentType.LUNCH_STOP;
        }
        // Early arrival within interval
        else if (minutesFromStart >= -s.getRoundingInterval() && minutesFromStart < 0) {
            this.adjustedtimestamp = shiftStart;
            this.adjustmenttype = PunchAdjustmentType.SHIFT_START;
        }
        // Within grace period
        else if (minutesFromStart >= 0 && minutesFromStart <= s.getGracePeriod()) {
            this.adjustedtimestamp = shiftStart;
            this.adjustmenttype = PunchAdjustmentType.SHIFT_START;
        }
        // Dock penalty
        else if (minutesFromStart > s.getGracePeriod() && 
                 minutesFromStart <= (s.getGracePeriod() + s.getRoundingInterval())) {
            this.adjustedtimestamp = shiftStart.plusMinutes(s.getDockPenalty());
            this.adjustmenttype = PunchAdjustmentType.SHIFT_DOCK;
        }
        // Default interval round
        else {
            roundToNearestInterval(s);
            this.adjustmenttype = PunchAdjustmentType.INTERVAL_ROUND;
        }
    }
    // CLOCK OUT
    // CLOCK OUT
    // CLOCK OUT
    else if (this.punchtype == 0) {
        // Check lunch period first
        if (isWithinInterval(minutesFromLunchStart, s.getRoundingInterval())) {
            this.adjustedtimestamp = lunchStart;
            this.adjustmenttype = PunchAdjustmentType.LUNCH_START;
            return;
        }
        
        // Exact shift stop match
        if (this.originaltimestamp.getHour() == shiftStop.getHour() && 
            this.originaltimestamp.getMinute() == shiftStop.getMinute()) {
            this.adjustedtimestamp = shiftStop;
            this.adjustmenttype = PunchAdjustmentType.NONE;
            return;
        }

        // Early departure - dock penalty
        if (minutesFromStop > s.getGracePeriod() && 
            minutesFromStop <= (s.getGracePeriod() + s.getRoundingInterval())) {
            this.adjustedtimestamp = shiftStop.minusMinutes(s.getDockPenalty());
            this.adjustmenttype = PunchAdjustmentType.SHIFT_DOCK;
            return;
        }
        
        // Within grace period of shift stop or interval of shift stop
        if (Math.abs(minutesFromStop) <= s.getRoundingInterval()) {
            this.adjustedtimestamp = shiftStop;
            this.adjustmenttype = PunchAdjustmentType.SHIFT_STOP;
            return;
        }
        
        // If close to shift stop but after grace period
        if (minutesFromStop < 0 && Math.abs(minutesFromStop) <= (s.getRoundingInterval() * 2)) {
            this.adjustedtimestamp = shiftStop;
            this.adjustmenttype = PunchAdjustmentType.SHIFT_STOP;
            return;
        }

        // Default interval round
        roundToNearestInterval(s);
        
        // Check if rounded time matches shift stop
        if (this.adjustedtimestamp.getHour() == shiftStop.getHour() && 
            this.adjustedtimestamp.getMinute() == shiftStop.getMinute()) {
            this.adjustmenttype = PunchAdjustmentType.SHIFT_STOP;
        }
        // Check if no adjustment needed
        else if (this.adjustedtimestamp.getHour() == this.originaltimestamp.getHour() && 
                 this.adjustedtimestamp.getMinute() == this.originaltimestamp.getMinute()) {
            this.adjustmenttype = PunchAdjustmentType.NONE;
        }
        // Otherwise it's an interval round
        else {
            this.adjustmenttype = PunchAdjustmentType.INTERVAL_ROUND;
        }
    }


    // Clear seconds and nanoseconds
    if (this.adjustedtimestamp != null) {
        this.adjustedtimestamp = this.adjustedtimestamp.withSecond(0).withNano(0);
    }
}

private boolean isWithinInterval(long minutes, int interval) {
    return Math.abs(minutes) <= interval;
}

private void roundToNearestInterval(Shift s) {
    long originalMinutes = this.originaltimestamp.getHour() * 60 + this.originaltimestamp.getMinute();
    long intervalMinutes = s.getRoundingInterval();    
    long previousInterval = (originalMinutes / intervalMinutes) * intervalMinutes;
    long minutesIntoInterval = originalMinutes - previousInterval;
    long roundedMinutes;
    
    if (minutesIntoInterval >= intervalMinutes/2) {
        roundedMinutes = previousInterval + intervalMinutes;
    } else {
        roundedMinutes = previousInterval;
    }
    
    this.adjustedtimestamp = this.originaltimestamp
        .withHour((int) (roundedMinutes / 60))
        .withMinute((int) (roundedMinutes % 60))
        .withSecond(0)
        .withNano(0);
}
    private LocalDateTime getShiftStartForDay(Shift s) {
        return this.originaltimestamp
            .withHour(s.getShiftStart().getHour())
            .withMinute(s.getShiftStart().getMinute())
            .withSecond(0)
            .withNano(0);
    }

    private LocalDateTime getShiftStopForDay(Shift s) {
        return this.originaltimestamp
            .withHour(s.getShiftEnd().getHour())
            .withMinute(s.getShiftEnd().getMinute())
            .withSecond(0)
            .withNano(0);
    }

    private LocalDateTime getLunchStartForDay(Shift s) {
        return this.originaltimestamp
            .withHour(s.getLunchStart().getHour())
            .withMinute(s.getLunchStart().getMinute())
            .withSecond(0)
            .withNano(0);
    }

    private LocalDateTime getLunchStopForDay(Shift s) {
        return this.originaltimestamp
            .withHour(s.getLunchEnd().getHour())
            .withMinute(s.getLunchEnd().getMinute())
            .withSecond(0)
            .withNano(0);
    }
}