package jamsa.rcp.downloader.http;

/**
 * 远程文件信息
 * 
 * @author 朱杰
 * 
 */
public class RemoteFileInfo {
	private String fileName;

	private long fileSize = 0;

	private String hostname;

	private int port = 80;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		result.append("hostname:").append(hostname).append("\n");
		result.append("port:").append(port).append("\n");
		result.append("fileName:").append(fileName).append("\n");
		result.append("fileSize:").append(fileSize).append("\n");

		return String.valueOf(result);
	}

}
