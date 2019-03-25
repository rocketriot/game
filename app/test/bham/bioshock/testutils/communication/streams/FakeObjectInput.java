package bham.bioshock.testutils.communication.streams;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import bham.bioshock.communication.messages.Message;

public class FakeObjectInput implements ObjectInput {

  private BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
  private boolean isOpen = true;
  
  public boolean isOpen() {
    return isOpen;
  }
  
  public void add(Message m) {
    queue.add(m);
  }
  
  @Override
  public boolean readBoolean() throws IOException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public byte readByte() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public char readChar() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double readDouble() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public float readFloat() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void readFully(byte[] arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void readFully(byte[] arg0, int arg1, int arg2) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int readInt() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String readLine() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long readLong() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public short readShort() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String readUTF() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int readUnsignedByte() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int readUnsignedShort() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int skipBytes(int arg0) throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int available() throws IOException {
    return 0;
  }

  @Override
  public void close() throws IOException {
    this.isOpen = false;
  }

  @Override
  public int read() throws IOException {
    return 0;
  }

  @Override
  public int read(byte[] arg0) throws IOException {
    return 0;
  }

  @Override
  public int read(byte[] arg0, int arg1, int arg2) throws IOException {
    return 0;
  }

  @Override
  public Object readObject() throws ClassNotFoundException, IOException {
    try {
      while(isOpen) {
        Object v = queue.poll(200, TimeUnit.MILLISECONDS);
        if(v != null) {
          return v;
        }
      }
      throw new IOException();
    } catch (InterruptedException e) {
    }
    return null;
  }

  @Override
  public long skip(long arg0) throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

}
