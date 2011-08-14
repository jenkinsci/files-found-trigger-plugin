/*
 * The MIT License
 * 
 * Copyright (c) 2011 Steven G. Brown
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.filesfoundtrigger;

import java.io.File;
import java.io.IOException;

import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

/**
 * Replacement for the {@link TemporaryFolder} JUnit rule, which (as of JUnit
 * 4.8.1) is not suitable for reliable unit testing. The {@link TemporaryFolder}
 * class allows file and directory creation to fail silently, which could lead
 * to false unit test results.
 * 
 * @author Steven G. Brown
 */
public class TemporaryFolderRule extends ExternalResource {

  /**
   * The temporary folder.
   */
  private File temporaryFolder;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void before() throws IOException {
    temporaryFolder = Files.createTempDir();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void after() {
    try {
      Files.deleteRecursively(temporaryFolder);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Create a new file with the given name under the temporary folder.
   * 
   * @param fileName
   *          the name that will be given to the new file
   * @return the new file
   * @throws IOException
   *           If an I/O error occurred
   */
  public File newFile(String fileName) throws IOException {
    File file = new File(temporaryFolder, fileName);
    Files.touch(file);
    return file;
  }

  /**
   * Get the location of this temporary folder.
   * 
   * @return the location of this temporary folder.
   */
  public File getRoot() {
    return temporaryFolder;
  }
}
