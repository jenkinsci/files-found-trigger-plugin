/*
 * The MIT License
 * 
 * Copyright (c) 2017 Steven G. Brown
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

import static hudson.plugins.filesfoundtrigger.Support.DIRECTORY;
import static hudson.plugins.filesfoundtrigger.Support.FILES;
import static hudson.plugins.filesfoundtrigger.Support.IGNORED_FILES;
import static hudson.plugins.filesfoundtrigger.Support.MASTER_NODE;
import static hudson.plugins.filesfoundtrigger.Support.TRIGGER_NUMBER;
import static hudson.util.FormValidation.Kind.ERROR;
import static hudson.util.FormValidation.Kind.OK;
import static hudson.util.FormValidation.Kind.WARNING;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ImmutableList;

import hudson.Util;
import hudson.util.FormValidation;

/**
 * Unit test for the {@link FileSearch} class.
 * 
 * @author Steven G. Brown
 */
@SuppressWarnings("boxing")
public class FileSearchTest {

  /**
   */
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private String userName = System.getProperty("user.name");

  /**
   * @throws Exception
   */
  @Test
  public void directoryNotSpecified() throws Exception {
    assertThat(search("", FILES, IGNORED_FILES, TRIGGER_NUMBER),
        is(result(ERROR, Messages.DirectoryNotSpecified(), Collections.emptyList())));
  }

  /**
   * @throws Exception
   */
  @Test
  public void filesNotSpecified() throws Exception {
    assertThat(search(DIRECTORY, "", IGNORED_FILES, TRIGGER_NUMBER),
        is(result(ERROR, Messages.FilesNotSpecified(), Collections.emptyList())));
  }

  /**
   * @throws Exception
   */
  @Test
  public void directoryNotFound() throws Exception {
    File nonExistentDirectory = new File(folder.getRoot(), "nonexistent");
    assertThat(search(nonExistentDirectory.getAbsolutePath(), FILES, IGNORED_FILES, TRIGGER_NUMBER),
        is(result(WARNING, Messages.DirectoryNotFound(userName), Collections.emptyList())));
  }

  /**
   * @throws Exception
   */
  @Test
  public void noFilesFound() throws Exception {
    assertThat(search(folder.getRoot().getAbsolutePath(), FILES, IGNORED_FILES, TRIGGER_NUMBER),
        is(result(OK, Messages.NoFilesFound(), Collections.emptyList())));
  }

  /**
   * @throws Exception
   */
  @Test
  public void oneFileFound() throws Exception {
    folder.newFile("test");
    assertThat(search(folder.getRoot().getAbsolutePath(), FILES, IGNORED_FILES, TRIGGER_NUMBER),
        is(result(OK, Messages.SingleFileFound("test"), ImmutableList.of("test"))));
  }

  /**
   * @throws Exception
   */
  @Test
  public void twoFilesFound() throws Exception {
    folder.newFile("test");
    folder.newFile("test2");
    assertThat(search(folder.getRoot().getAbsolutePath(), FILES, IGNORED_FILES, TRIGGER_NUMBER),
        is(result(OK, Messages.MultipleFilesFound(2), ImmutableList.of("test", "test2"))));
  }

  /**
   * @throws Exception
   */
  @Test
  public void allFilesIgnored() throws Exception {
    folder.newFile("test");
    assertThat(search(folder.getRoot().getAbsolutePath(), FILES, "**", TRIGGER_NUMBER),
        is(result(OK, Messages.NoFilesFound(), Collections.emptyList())));
  }

  private FileSearch.Result search(String directory, String files, String ignoredFiles,
      String triggerNumber) throws Exception {
    FilesFoundTriggerConfig config = new FilesFoundTriggerConfig(MASTER_NODE, directory, files,
        ignoredFiles, triggerNumber);
    return FileSearch.perform(config);
  }

  private Matcher<FileSearch.Result> result(final FormValidation.Kind kind, final String message,
      final List<?> files) {
    return new CustomMatcher<FileSearch.Result>(
        "Search result of kind " + kind + " with message " + message + " matching files " + files) {

      @Override
      public boolean matches(Object item) {
        if (item instanceof FileSearch.Result) {
          FileSearch.Result result = (FileSearch.Result) item;
          FormValidation formValidation = result.formValidation;
          return formValidation.kind == kind
              && Objects.equals(formValidation.getMessage(), Util.escape(message))
              && Objects.equals(result.files, files);
        }
        return false;
      }
    };
  }
}
