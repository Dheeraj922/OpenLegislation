package gov.nysenate.openleg.controller.api.hearing;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.view.hearing.PublicHearingIdView;
import gov.nysenate.openleg.client.view.hearing.PublicHearingPdfView;
import gov.nysenate.openleg.client.view.hearing.PublicHearingView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.service.hearing.data.PublicHearingDataService;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET)
public class PublicHearingGetCtrl extends BaseCtrl
{

    @Autowired
    private PublicHearingDataService hearingDataService;

    /**
     * Public Hearing Listing API.
     *
     * Retrieve public hearings for a year: (GET) /api/3/hearings/{year}
     * Request Parameters : order - return results in ASC or DESC order
     *
     */
    @RequestMapping(value = "/{year:[\\d]{4}}")
    public BaseResponse getHearingsByYear(@PathVariable int year,
                                          WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.DESC);
        return ListViewResponse.of(
            hearingDataService.getPublicHearingIds(year, sortOrder, limOff).stream()
                .map(hid -> new PublicHearingIdView(hid))
                .collect(Collectors.toList()), 0, limOff);
    }

    /**
     * Single Public Hearing Retrieval API.
     *
     * Retrieve a singe public hearing by its title and dateTime:
     * (GET) /api/3/hearings/{title}/{dateTime}
     *
     * i.e. /api/3/hearings/ROUNDTABLE DISCUSSION ON THE COMPASSIONATE CARE ACT/2014-03-12T10:00
     *
     */
    @RequestMapping(value = "/{title}/{dateTime}")
    public BaseResponse getHearing(@PathVariable String title,
                                   @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime dateTime) {
        return new ViewObjectResponse<>(
                new PublicHearingView(hearingDataService.getPublicHearing(new PublicHearingId(title, dateTime))));
    }

    /**
     *  Single Public Hearing PDF retrieval API.
     *
     * Retrieve a single public hearing text pdf: (GET) /api/3/hearings/{title}/{dateTime}.pdf
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */
    @RequestMapping(value = "/{title}/{dateTime}.pdf")
    public void getHearingPdf(@PathVariable String title,
                              @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime dateTime,
                              HttpServletResponse response) throws IOException, COSVisitorException {

        PublicHearingId hearingId = new PublicHearingId(title, dateTime);
        PublicHearing hearing = hearingDataService.getPublicHearing(hearingId);
        new PublicHearingPdfView(hearing, response.getOutputStream());
        response.setContentType("application/pdf");
    }
}
