package bham.bioshock.testutils.communication.streams;

import java.io.IOException;
import java.io.InputStream;

public class FakeInputStream extends InputStream {

  @Override
  public int read() throws IOException {
    return 0;
  }

}
