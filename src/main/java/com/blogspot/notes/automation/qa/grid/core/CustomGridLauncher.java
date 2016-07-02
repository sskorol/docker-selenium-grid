package com.blogspot.notes.automation.qa.grid.core;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.grid.selenium.GridLauncher;

import java.util.logging.Logger;

import static com.blogspot.notes.automation.qa.grid.utils.IOUtils.exportResourceToTmpDir;
import static com.blogspot.notes.automation.qa.grid.utils.VideoRecordingUtils.SEND_CTRL_C_TOOL_NAME;
import static com.blogspot.notes.automation.qa.grid.utils.VideoRecordingUtils.SEND_CTRL_C_TOOL_PATH;

/**
 * Author: Serhii Korol.
 */
public class CustomGridLauncher {

	private static final Logger LOGGER = Logger.getLogger(CustomGridLauncher.class.getName());

	public static void main(String[] args) throws Exception {
		if (SystemUtils.IS_OS_WINDOWS)
			exportResourceToTmpDir(SEND_CTRL_C_TOOL_NAME).ifPresent(path -> {
				SEND_CTRL_C_TOOL_PATH = path;
				LOGGER.info(SEND_CTRL_C_TOOL_PATH + " is set into global scope");
			});
		GridLauncher.main(args);
	}
}
