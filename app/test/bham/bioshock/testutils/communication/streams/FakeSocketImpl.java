package bham.bioshock.testutils.communication.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class FakeSocketImpl extends SocketImpl {

  public BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
  
  public void clear() {
    queue.clear();
  }
  
  @Override
  public Object getOption(int arg0) throws SocketException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setOption(int arg0, Object arg1) throws SocketException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void accept(SocketImpl arg0) throws IOException {
    Object value = null;
    try {
      value = queue.poll(100, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
    }
    if(value == null) {
      throw new SocketTimeoutException();
    }
  }

  @Override
  protected int available() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected void bind(InetAddress arg0, int arg1) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void close() throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void connect(String arg0, int arg1) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void connect(InetAddress arg0, int arg1) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void connect(SocketAddress arg0, int arg1) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void create(boolean arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected InputStream getInputStream() throws IOException {
    return new FakeInputStream();
  }

  @Override
  protected OutputStream getOutputStream() throws IOException {
    return new FakeOutputStream();
  }

  @Override
  protected void listen(int arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void sendUrgentData(int arg0) throws IOException {
    // TODO Auto-generated method stub
    
  }


}
