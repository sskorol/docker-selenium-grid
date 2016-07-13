package com.blogspot.notes.automation.qa.grid.core;

import com.blogspot.notes.automation.qa.grid.entities.VideoInfo;
import com.blogspot.notes.automation.qa.grid.enums.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import static com.blogspot.notes.automation.qa.grid.utils.VideoRecordingUtils.startVideoRecording;
import static com.blogspot.notes.automation.qa.grid.utils.VideoRecordingUtils.stopVideoRecording;

/**
 * Created by Serhii Korol
 */
public class VideoRecordingServlet extends HttpServlet
{

	private static final long serialVersionUID = -8308677302003045967L;

	private static final Logger LOGGER = Logger.getLogger(VideoRecordingServlet.class.getName());

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, IllegalArgumentException {
		doPost(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, IllegalArgumentException {
		process(request, response);
	}

	private void process(final HttpServletRequest request, final HttpServletResponse response) throws IOException, IllegalArgumentException {
		final Command command = Optional.ofNullable(request.getParameter("command"))
				.map(Command::valueOf)
				.orElseThrow(() -> new IllegalArgumentException("Unable to find command for video recording"));

		try {
			switch (command) {
				case START_RECORDING:
					stopVideoRecording();
					startVideoRecording(getVideoInfo(request));
					updateResponse(response, HttpStatus.SC_OK, "Started recording");
					break;
				case STOP_RECORDING:
					stopVideoRecording();
					updateResponse(response, HttpStatus.SC_OK, "Stopped recording");
					break;
			}
		} catch (Exception ex) {
			LOGGER.severe("Unable to process recording: " + ex);
			updateResponse(response, HttpStatus.SC_INTERNAL_SERVER_ERROR,
					"Internal server error occurred while trying to start / stop recording: " + ex);
		}
	}

	private VideoInfo getVideoInfo(final HttpServletRequest request) throws IOException {
		final StringBuilder jsonBuilder = new StringBuilder();
		String line;

		try (BufferedReader reader = request.getReader()) {
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}
		}

		return new ObjectMapper().findAndRegisterModules().readValue(jsonBuilder.toString(), VideoInfo.class);
	}

	private void updateResponse(final HttpServletResponse response, final int status, final String message) throws IOException {
		response.setStatus(status);
		response.getWriter().write(message);
	}
}
