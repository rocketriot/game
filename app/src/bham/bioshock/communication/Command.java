package bham.bioshock.communication;

public enum Command {
	COMM_DISCOVER,
	COMM_DISCOVER_RESPONSE,
	TEST;
	
	public byte[] getBytes() {
		return this.toString().getBytes();
	}
}
