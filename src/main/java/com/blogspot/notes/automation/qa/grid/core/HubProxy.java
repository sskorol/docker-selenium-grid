package com.blogspot.notes.automation.qa.grid.core;

import java.io.*;
import java.util.Optional;
import java.util.logging.Logger;

import com.blogspot.notes.automation.qa.grid.enums.Command;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

public class HubProxy extends DefaultRemoteProxy {

	private static final Logger LOGGER = Logger.getLogger(HubProxy.class.getName());

	public HubProxy(final RegistrationRequest request, final Registry registry) throws IOException {
		super(request, registry);
	}

	@Override
	public void beforeSession(final TestSession session) {
		super.beforeSession(session);
		processRecording(session, Command.START_RECORDING);
	}

	@Override
	public void afterSession(final TestSession session) {
		super.afterSession(session);
		processRecording(session, Command.STOP_RECORDING);
	}

	private void processRecording(final TestSession session, final Command command) {
		final String videoInfo = getCapability(session, "videoInfo");

		if (!videoInfo.isEmpty()) {
			final String url = "http://" + this.getRemoteHost().getHost() + ":" + this.getRemoteHost().getPort() +
					"/extra/" + VideoRecordingServlet.class.getSimpleName() + "?command=" + command;

			switch (command) {
				case START_RECORDING:
					sendRecordingRequest(url, videoInfo);
					break;
				case STOP_RECORDING:
					sendRecordingRequest(url, "");
					break;
			}
		}
	}

	private void sendRecordingRequest(final String url, final String entity) {
		CloseableHttpResponse response = null;

		try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
			final HttpPost post = new HttpPost(url);

			if (!entity.isEmpty()) {
				post.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON));
			}

			response = client.execute(post);
			LOGGER.info("Node response: " + response);
		} catch (Exception ex) {
			LOGGER.severe("Unable to send recording request to node: " + ex);
		} finally {
			HttpClientUtils.closeQuietly(response);
		}
	}

	private String getCapability(final TestSession session, final String capability) {
		return Optional.ofNullable(session.getRequestedCapabilities().get(capability))
				.map(cap -> (String) cap)
				.orElse("");
	}
}