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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;

import antlr.ANTLRException;

/**
 * Unit test for the {@link FilesFoundTrigger} class.
 * 
 * @author Steven G. Brown
 */
public class FilesFoundTriggerTest {

  /**
   */
  private static final String DIRECTORY = "C:/";

  /**
   */
  private static final String FILES = "**";

  /**
   */
  private static final String IGNORED_FILES = null;

  /**
   */
  @Rule
  public TemporaryFolderRule folder = new TemporaryFolderRule();

  /**
   */
  @Test
  public void testGetDirectory() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getDirectory(),
        is(DIRECTORY));
  }

  /**
   */
  @Test
  public void testGetFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getFiles(), is(FILES));
  }

  /**
   */
  @Test
  public void testGetIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getIgnoredFiles(),
        is(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void testFilesFoundDirectoryNotSpecified() {
    FilesFoundTrigger trigger = create("", FILES, IGNORED_FILES);
    assertThat(trigger.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void testFilesFoundDirectoryNotFound() {
    File nonExistentDirectory = new File(folder.getRoot(), "nonexistent");
    FilesFoundTrigger trigger = create(nonExistentDirectory.getAbsolutePath(),
        FILES, IGNORED_FILES);
    assertThat(trigger.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void testFilesFoundFilesNotSpecified() {
    FilesFoundTrigger trigger = create(folder.getRoot().getAbsolutePath(), "",
        IGNORED_FILES);
    assertThat(trigger.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void testFilesFoundSuccess() {
    folder.newFile("test");
    FilesFoundTrigger trigger = create(folder.getRoot().getAbsolutePath(),
        FILES, IGNORED_FILES);
    assertThat(trigger.filesFound(), is(true));
  }

  /**
   */
  @Test
  public void testFilesFoundNoFiles() {
    FilesFoundTrigger trigger = create(folder.getRoot().getAbsolutePath(),
        FILES, IGNORED_FILES);
    assertThat(trigger.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void testFilesFoundNoUnignoredFiles() {
    folder.newFile("test");
    FilesFoundTrigger trigger = create(folder.getRoot().getAbsolutePath(),
        FILES, "**");
    assertThat(trigger.filesFound(), is(false));
  }

  /**
   * Create a new {@link FilesFoundTrigger} instance.
   * 
   * @param directory
   *          the base directory
   * @param files
   *          the pattern of files
   * @param ignoredFiles
   *          the pattern of ignored files
   * @return a new {@link FilesFoundTrigger}
   */
  private FilesFoundTrigger create(String directory, String files,
      String ignoredFiles) {
    try {
      FilesFoundTrigger trigger = new FilesFoundTrigger("", directory, files,
          ignoredFiles);
      return trigger;
    } catch (ANTLRException ex) {
      throw new RuntimeException(ex);
    }
  }
}
