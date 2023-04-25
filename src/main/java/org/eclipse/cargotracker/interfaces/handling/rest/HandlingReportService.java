package org.eclipse.cargotracker.interfaces.handling.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.cargotracker.application.ApplicationEvents;
import org.eclipse.cargotracker.domain.model.cargo.TrackingId;
import org.eclipse.cargotracker.domain.model.handling.HandlingEvent;
import org.eclipse.cargotracker.domain.model.location.UnLocode;
import org.eclipse.cargotracker.domain.model.voyage.VoyageNumber;
import org.eclipse.cargotracker.interfaces.handling.HandlingEventRegistrationAttempt;

/**
 * This REST end-point implementation performs basic validation and parsing of
 * incoming data, and in case of a valid registration attempt, sends an
 * asynchronous message with the information to the handling event registration
 * system for proper registration.
 */
@Stateless
@Path("/handling")
public class HandlingReportService {

	public static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm";

	@Inject
	private ApplicationEvents applicationEvents;

	public HandlingReportService() {
	}

	@POST
	@Path("/reports")
	@Consumes(MediaType.APPLICATION_JSON)
	public void submitReport(@NotNull @Valid HandlingReport handlingReport) {
		try {
			Date completionTime = new SimpleDateFormat(ISO_8601_FORMAT).parse(handlingReport.getCompletionTime());
			VoyageNumber voyageNumber = null;

			if (handlingReport.getVoyageNumber() != null) {
				voyageNumber = new VoyageNumber(handlingReport.getVoyageNumber());
			}

			HandlingEvent.Type type = HandlingEvent.Type.valueOf(handlingReport.getEventType());
			UnLocode unLocode = new UnLocode(handlingReport.getUnLocode());

			TrackingId trackingId = new TrackingId(handlingReport.getTrackingId());

			Date registrationTime = new Date();
			HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(registrationTime,
					completionTime, trackingId, voyageNumber, type, unLocode);

			applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
		} catch (ParseException ex) {
			throw new RuntimeException("Error parsing completion time", ex);
		}
	}
}
