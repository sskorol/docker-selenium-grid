package com.blogspot.notes.automation.qa.grid.utils;

import com.blogspot.notes.automation.qa.grid.entities.VideoInfo;
import org.apache.commons.lang3.SystemUtils;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static org.apache.commons.io.FilenameUtils.separatorsToSystem;

/**
 * Created by Serhii Korol
 */
public final class VideoRecordingUtils {

	public static String SEND_CTRL_C_TOOL_PATH;
	public static final String SEND_CTRL_C_TOOL_NAME = "SendSignalCtrlC.exe";

	private static final Logger LOGGER = Logger.getLogger(VideoRecordingUtils.class.getName());
	private static final String RECORDING_TOOL = "ffmpeg";
	private static final String TMP_DIR_NAME = "tmp";

	private VideoRecordingUtils() {
		throw new UnsupportedOperationException("Illegal access to private constructor");
	}

	public static void startVideoRecording(final VideoInfo info) {
		final String tmpPath = info.getStoragePath() + File.separator + TMP_DIR_NAME;
		createTmpDirectory(tmpPath);
		final String outputPath = parseFileName(tmpPath, info.getFileName(), "mp4");
		final String display = SystemUtils.IS_OS_LINUX ? System.getenv("DISPLAY") : "desktop";
		final String recorder = SystemUtils.IS_OS_LINUX ? "x11grab" : "gdigrab";
		final String[] commandsSequence = new String[]{
				RECORDING_TOOL, "-y",
				"-video_size", info.getResolution(),
				"-f", recorder,
				"-i", display,
				"-an",
				"-vcodec", "libx264",
				"-crf", String.valueOf(info.getQuality()),
				"-r", String.valueOf(info.getFrameRate()),
				"-t", String.valueOf(info.getTimeout().getSeconds()),
				outputPath
		};

		CompletableFuture.supplyAsync(() -> runCommand(commandsSequence))
				.whenCompleteAsync((output, errors) -> {
					LOGGER.info("Start recording output log: " + output + (errors != null ? "; ex: " + errors : ""));
					LOGGER.info("Trying to copy " + outputPath + " to the main folder.");
					copyFile(info.getStoragePath(), info.getFileName());
				});
	}

	public static void stopVideoRecording() {
		final String output = SystemUtils.IS_OS_LINUX ?
				runCommand("pkill", "-INT", RECORDING_TOOL) :
				runCommand(Optional.ofNullable(SEND_CTRL_C_TOOL_PATH).orElse(SEND_CTRL_C_TOOL_NAME), getPidOf(RECORDING_TOOL));
		LOGGER.info("Stop recording output log: " + output);
	}

	private static void copyFile(final String storagePath, final String fileName) {
		final String inputFileName = parseFileName(storagePath + File.separator + TMP_DIR_NAME, fileName, "mp4");
		final String outputFileName = parseFileName(storagePath, fileName, "mp4");
		final String output = SystemUtils.IS_OS_LINUX ? runCommand("cp", inputFileName, outputFileName) :
				runCommand("cmd", "/c", "copy", inputFileName, outputFileName);
		LOGGER.info("File copy output log: " + output);
	}

	private static String runCommand(final String... args) {
		LOGGER.info("Trying to execute the following command: " + Arrays.asList(args));
		try {
			return new ProcessExecutor()
					.command(args)
					.readOutput(true)
					.execute()
					.outputUTF8();
		} catch (IOException | InterruptedException | TimeoutException e) {
			LOGGER.severe("Unable to execute command: " + e);
			return "PROCESS_EXECUTION_ERROR";
		}
	}

	private static String parseFileName(final String storagePath, final String fileName, final String extension) {
		if (!fileName.isEmpty() && !storagePath.isEmpty()) {
			return separatorsToSystem(storagePath + File.separator + fileName + "." + extension);
		}

		throw new IllegalArgumentException("Unable to determine output file path");
	}

	private static void createTmpDirectory(final String directoryName) {
		final String formattedDirectoryName = separatorsToSystem(directoryName);
		final File tmpDir = new File(formattedDirectoryName);

		if (!tmpDir.exists()) {
			LOGGER.info("Creating " + formattedDirectoryName);
			tmpDir.mkdirs();
		}
	}

	private static String getPidOf(final String processName) {
		return runCommand("cmd", "/c", "for /f \"tokens=2\" %i in ('tasklist ^| findstr \"" + processName +
				"\"') do @echo %i").trim();
	}
}
