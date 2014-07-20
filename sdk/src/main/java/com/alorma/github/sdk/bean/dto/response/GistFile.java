package com.alorma.github.sdk.bean.dto.response;

public class GistFile {

	private int size;

	private String content;

	private String filename;

	private String rawUrl;

	/**
	 * @return size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size
	 * @return this gist file
	 */
	public GistFile setSize(int size) {
		this.size = size;
		return this;
	}

	/**
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 * @return this gist file
	 */
	public GistFile setContent(String content) {
		this.content = content;
		return this;
	}

	/**
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 * @return this gist file
	 */
	public GistFile setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	/**
	 * @return rawUrl
	 */
	public String getRawUrl() {
		return rawUrl;
	}

	/**
	 * @param rawUrl
	 * @return this gist file
	 */
	public GistFile setRawUrl(String rawUrl) {
		this.rawUrl = rawUrl;
		return this;
	}
}