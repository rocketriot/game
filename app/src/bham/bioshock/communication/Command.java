package bham.bioshock.communication;

public enum Command {
	COMM_DISCOVER;
	
	public byte[] getBytes() {
		return this.toString().getBytes();
	}
}
