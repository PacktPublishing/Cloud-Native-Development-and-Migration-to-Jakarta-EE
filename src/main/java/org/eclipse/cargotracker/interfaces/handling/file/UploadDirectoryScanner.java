package org.eclipse.cargotracker.interfaces.handling.file;

import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.BatchRuntime;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;

/**
 * Periodically scans a certain directory for files and attempts to parse
 * handling event registrations from the contents by calling a batch job.
 * <p/>
 * Files that fail to parse are moved into a separate directory, successful
 * files are deleted.
 */
@Stateless
public class UploadDirectoryScanner {

	@Schedule(minute = "*/2", hour = "*") // Runs every fifteen minutes
	public void processFiles() {
		JobOperator jobOperator = BatchRuntime.getJobOperator();
		jobOperator.start("EventFilesProcessorJob", null);
	}
}
