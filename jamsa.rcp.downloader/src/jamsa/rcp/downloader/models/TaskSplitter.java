package jamsa.rcp.downloader.models;

import java.io.Serializable;

/**
 * �������Ϣ
 * 
 * @author ���
 * 
 */
public class TaskSplitter implements Serializable {
	// /**
	// * ��Ϣ
	// */
	// private List messages = new ArrayList(20);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1466354623780325343L;

	/**
	 * ������
	 */
	private String name;

	/**
	 * ����ʼλ��
	 */
	private long startPos;

	/**
	 * �������
	 */
	private long finished;

	/**
	 * �����λ��
	 */
	private long endPos;

	public long getEndPos() {
		return endPos;
	}

	public void setEndPos(long endPos) {
		this.endPos = endPos;
	}

	public long getFinished() {
		return finished;
	}

	public void setFinished(long finished) {
		this.finished = finished;
	}

	public long getStartPos() {
		return startPos;
	}

	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskSplitter(long startPos, long endPos, long finished, String name) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.finished = finished;
		this.name = name;
	}

	public boolean isFinish() {
		return (this.getFinished() - (this.getEndPos() - this.getStartPos())) >= 0;
	}

	// public List getMessages() {
	// return messages;
	// }
	//
	// public void setMessages(List messages) {
	// this.messages = messages;
	// }
	//	
	// public void addMessage(String message){
	// this.messages.add(message);
	// }
	//	
	// public void removeMessage(String message){
	// this.messages.remove(message);
	// }
}
