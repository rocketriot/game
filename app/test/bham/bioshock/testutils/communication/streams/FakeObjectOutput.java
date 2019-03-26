package bham.bioshock.testutils.communication.streams;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.LinkedList;

public class FakeObjectOutput implements ObjectOutput {

  public LinkedList<Object> messages = new LinkedList<>();
  public boolean isOpen = true;
  
  public boolean isOpen() {
    return isOpen;
  }
  
  @Override
  public void writeBoolean(boolean arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeByte(int arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeBytes(String arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeChar(int arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeChars(String arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeDouble(double arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeFloat(float arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeInt(int arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeLong(long arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeShort(int arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeUTF(String arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void close() throws IOException {
    this.isOpen = false;
  }

  @Override
  public void flush() throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void write(int b) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void write(byte[] b) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void writeObject(Object obj) throws IOException {
    messages.add(obj);
  }

}
