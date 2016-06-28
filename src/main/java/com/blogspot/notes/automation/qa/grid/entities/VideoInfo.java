package com.blogspot.notes.automation.qa.grid.entities;

import java.awt.*;

/**
 * Author: Serhii Korol.
 */
public class VideoInfo {

	private String storagePath;
	private String fileName;
	private int quality;
	private int frameRate;

	private final Dimension screenSize;

	public VideoInfo() {
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	}

	public VideoInfo(final String storagePath, final String fileName, final int quality, final int frameRate) {
		this();
		this.storagePath = storagePath;
		this.fileName = fileName;
		this.quality = quality;
		this.frameRate = frameRate;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(final String storagePath) {
		this.storagePath = storagePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(final int quality) {
		this.quality = quality;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(final int frameRate) {
		this.frameRate = frameRate;
	}

	public String getResolution() {
		return (int) screenSize.getWidth() + "x" + (int) screenSize.getHeight();
	}
}
