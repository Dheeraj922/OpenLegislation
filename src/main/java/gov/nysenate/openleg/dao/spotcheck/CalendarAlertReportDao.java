package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.spotcheck.elastic.AbstractSpotCheckReportElasticDao;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class CalendarAlertReportDao extends AbstractSpotCheckReportElasticDao<CalendarId> {

    @Override
    public CalendarId getKeyFromMap(Map<String, String> keyMap) {
        return new CalendarId(Integer.parseInt(keyMap.get("cal_no")), Integer.parseInt(keyMap.get("year")));
    }

    @Override
    public Map<String, String> getMapFromKey(CalendarId calendarId) {
        return ImmutableMap.<String, String>builder()
                .put("cal_no", String.valueOf(calendarId.getCalNo()))
                .put("year", String.valueOf(calendarId.getYear())).build();
    }

    public CalendarAlertReportDao() {
        super(SpotCheckRefType.LBDC_CALENDAR_ALERT);
    }

}
