package com.blogspot.notes.automation.qa.grid.utils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Author: Serhii Korol.
 */
public final class IOUtils {

	private static final Logger LOGGER = Logger.getLogger(IOUtils.class.getName());

	private IOUtils() {
		throw new UnsupportedOperationException("Illegal access to private constructor");
	}

	public static Optional<String> exportResourceToTmpDir(final String name) {
		final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		final File resourceCopy = new File(tmpDir.getAbsolutePath() + File.separator + name);
		Optional<String> outputPath;

		if (!tmpDir.exists())
			tmpDir.mkdirs();

		if (!resourceCopy.exists()) {
			try (final InputStream resourceStream = ClassLoader.getSystemResourceAsStream(name)) {
				Files.copy(resourceStream, resourceCopy.getAbsoluteFile().toPath());
				outputPath = Optional.of(resourceCopy.getAbsolutePath());
			} catch (Exception ex) {
				LOGGER.severe("Unable to copy " + name + " into " + tmpDir + ": " + ex);
				outputPath = Optional.empty();
			}
		} else {
			outputPath = Optional.of(resourceCopy.getAbsolutePath());
		}

		return outputPath;
	}
}
