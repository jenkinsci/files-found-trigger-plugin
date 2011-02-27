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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import hudson.util.FormValidation;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;

/**
 * Unit test for the {@link FilesFoundTriggerConfig} class.
 * 
 * @author Steven G. Brown
 */
@SuppressWarnings("boxing")
public class FilesFoundTriggerConfigTest {

  /**
   */
  private static final String DIRECTORY = "C:/";

  /**
   */
  private static final String FILES = "**";

  /**
   */
  private static final String IGNORED_FILES = "ignore";

  /**
   */
  @Rule
  public TemporaryFolderRule folder = new TemporaryFolderRule();

  /**
   */
  @Test
  public void getDirectory() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getDirectory(),
        is(DIRECTORY));
  }

  /**
   */
  @Test
  public void getDirectoryTrimmed() {
    assertThat(create("  " + DIRECTORY + "  ", FILES, IGNORED_FILES)
        .getDirectory(), is(DIRECTORY));
  }

  /**
   */
  @Test
  public void getFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getFiles(), is(FILES));
  }

  /**
   */
  @Test
  public void getFilesTrimmed() {
    assertThat(
        create(DIRECTORY, "  " + FILES + "  ", IGNORED_FILES).getFiles(),
        is(FILES));
  }

  /**
   */
  @Test
  public void getIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getIgnoredFiles(),
        is(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void getIgnoredFilesTrimmed() {
    assertThat(create(DIRECTORY, FILES, "  " + IGNORED_FILES + "  ")
        .getIgnoredFiles(), is(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void filesFoundDirectoryNotSpecified() {
    FilesFoundTriggerConfig config = create("", FILES, IGNORED_FILES);
    assertThat(config.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void filesFoundDirectoryNotFound() {
    File nonExistentDirectory = new File(folder.getRoot(), "nonexistent");
    FilesFoundTriggerConfig config = create(nonExistentDirectory
        .getAbsolutePath(), FILES, IGNORED_FILES);
    assertThat(config.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void filesFoundFilesNotSpecified() {
    FilesFoundTriggerConfig config = create(folder.getRoot().getAbsolutePath(),
        "", IGNORED_FILES);
    assertThat(config.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void filesFoundSuccess() {
    folder.newFile("test");
    FilesFoundTriggerConfig config = create(folder.getRoot().getAbsolutePath(),
        FILES, IGNORED_FILES);
    assertThat(config.filesFound(), is(true));
  }

  /**
   */
  @Test
  public void filesFoundNoFiles() {
    FilesFoundTriggerConfig config = create(folder.getRoot().getAbsolutePath(),
        FILES, IGNORED_FILES);
    assertThat(config.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void filesFoundNoUnignoredFiles() {
    folder.newFile("test");
    FilesFoundTriggerConfig config = create(folder.getRoot().getAbsolutePath(),
        FILES, "**");
    assertThat(config.filesFound(), is(false));
  }

  /**
   */
  @Test
  public void toStringContainsDirectory() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(DIRECTORY));
  }

  /**
   */
  @Test
  public void toStringContainsFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(FILES));
  }

  /**
   */
  @Test
  public void toStringContainsIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void getDisplayName() {
    assertThat(new FilesFoundTriggerConfig.DescriptorImpl().getDisplayName(),
        not(nullValue()));
  }

  /**
   */
  @Test
  public void doTestConfigurationDirectoryNotSpecified() {
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration("", FILES, IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.ERROR));
  }

  /**
   */
  @Test
  public void doTestConfigurationFilesNotSpecified() {
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(DIRECTORY, "", IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.ERROR));
  }

  /**
   */
  @Test
  public void doTestConfigurationDirectoryNotFound() {
    File nonExistentDirectory = new File(folder.getRoot(), "nonexistent");
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(nonExistentDirectory.getAbsolutePath(), FILES,
            IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.WARNING));
  }

  /**
   */
  @Test
  public void doTestConfigurationFilesNotFound() {
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(folder.getRoot().getAbsolutePath(), FILES,
            IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.OK));
  }

  /**
   */
  @Test
  public void doTestConfigurationFilesFound() {
    folder.newFile("test");
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(folder.getRoot().getAbsolutePath(), FILES,
            IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.OK));
  }

  /**
   * Create a new {@link FilesFoundTriggerConfig}.
   * 
   * @param directory
   *          the base directory
   * @param files
   *          the pattern of files
   * @param ignoredFiles
   *          the pattern of ignored files
   * @return a new config
   */
  private FilesFoundTriggerConfig create(String directory, String files,
      String ignoredFiles) {
    return new FilesFoundTriggerConfig(directory, files, ignoredFiles);
  }
}
