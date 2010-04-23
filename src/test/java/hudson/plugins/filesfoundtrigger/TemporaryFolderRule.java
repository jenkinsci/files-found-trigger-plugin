/*
 * The MIT License
 * 
 * Copyright (c) 2010 Steven G. Brown
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
   * The {@link TemporaryFolder} delegate.
   */
  private final TemporaryFolder folder;

  /**
   * Create a new {@link TemporaryFolderRule}.
   */
  public TemporaryFolderRule() {
    this.folder = new TemporaryFolder();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void before() throws Throwable {
    folder.create();
    if (!folder.getRoot().isDirectory()) {
      throw new IOException("Unable to create directory: " + folder.getRoot());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void after() {
    folder.delete();
  }

  /**
   * Create a new file with the given name under the temporary folder.
   * 
   * @param fileName
   *          the name that will be given to the new file
   * @return the new file
   */
  public File newFile(String fileName) {
    File file;
    try {
      file = folder.newFile(fileName);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    if (!file.isFile()) {
      throw new RuntimeException("Unable to create file: " + file);
    }
    return file;
  }

  /**
   * Create a new folder with the given name under the temporary folder.
   * 
   * @param folderName
   *          the name that will be given to the new folder
   * @return the new folder
   */
  public File newFolder(String folderName) {
    File directory = folder.newFolder(folderName);
    if (!directory.isDirectory()) {
      throw new RuntimeException("Unable to create directory: " + directory);
    }
    return directory;
  }

  /**
   * Get the location of this temporary folder.
   * 
   * @return the location of this temporary folder.
   */
  public File getRoot() {
    return folder.getRoot();
  }
}
