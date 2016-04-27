package com.blogspot.notes.automation.qa.grid.utils;

import com.blogspot.notes.automation.qa.grid.entities.VideoInfo;
import javaslang.control.Try;
import org.zeroturnaround.exec.ProcessExecutor;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by Serhii Korol
 */
public final class VideoRecordingUtils {

	private static final Logger LOGGER = Logger.getLogger(VideoRecordingUtils.class.getName());

	public static void startVideoRecording(final VideoInfo info) {
		final String outputPath = parseFileName(info.getStoragePath() + "/tmp", info.getFileName(), "mp4");
		final String display = System.getenv("DISPLAY");

		CompletableFuture.supplyAsync(() -> runCommand("avconv",
						"-f", "x11grab",
						"-an",
						"-s", "1360x1020",
						"-i", display,
						"-vcodec", "libx264",
						"-crf", String.valueOf(info.getQuality()),
						"-r", String.valueOf(info.getFrameRate()),
						outputPath))
				.whenCompleteAsync((output, errors) -> {
					LOGGER.info("Start recording output log: " + output + (errors != null ? "; ex: " + errors : ""));
					LOGGER.info("Trying to copy " + outputPath + " to the main folder.");
					copyFile(info.getStoragePath(), info.getFileName());
				});
	}

	public static void stopVideoRecording() {
		LOGGER.info("Stop recording output log: " + runCommand("pkill", "-INT", "avconv"));
	}

	private static void copyFile(final String storagePath, final String fileName) {
		final String inputFileName = parseFileName(storagePath + "/tmp", fileName, "mp4");
		final String outputFileName = parseFileName(storagePath, fileName, "mp4");
		LOGGER.info("File copy output log: " + runCommand("cp", inputFileName, outputFileName));
	}

	private static String runCommand(final String... args) {
		return Try.of(() -> new ProcessExecutor()
				.command(args)
				.readOutput(true)
				.execute()
				.outputUTF8())
				.getOrElseThrow(ex -> new IllegalArgumentException("Unable to execute command", ex));
	}

	private static String parseFileName(final String storagePath, final String fileName, final String extension) {
		if (!fileName.isEmpty() && !storagePath.isEmpty()) {
			return storagePath + "/" + fileName + "." + extension;
		} else {
			return "/e2e/uploads/tmp/tmp." + extension;
		}
	}

	private VideoRecordingUtils() {
		throw new UnsupportedOperationException("Illegal access to private constructor");
	}
}
