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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Test;

/**
 * Unit test for the {@link FilesFoundTriggerCause} class.
 * 
 * @author Steven G. Brown
 */
@SuppressWarnings("boxing")
public class FilesFoundTriggerCauseTest {

  /**
   */
  private static final String DIRECTORY = "C:/";

  /**
   */
  private static final String ALTERNATE_DIRECTORY = "D:/";

  /**
   */
  private static final String FILES = "**";

  /**
   */
  private static final String ALTERNATE_FILES = "files";

  /**
   */
  private static final String IGNORED_FILES = "ignored";

  /**
   */
  private static final String ALTERNATE_IGNORED_FILES = "alt_ignored";

  /**
   */
  private static final String XML_TEMPLATE = "<hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>";

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
  public void getFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getFiles(), is(FILES));
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
  public void getIgnoredFilesNotSpecified() {
    assertThat(create(DIRECTORY, FILES, "").getIgnoredFiles(), is(""));
  }

  /**
   */
  @Test
  public void getShortDescription() {
    assertThat(create(DIRECTORY, FILES, "").getShortDescription(), is(Messages
        .Cause(DIRECTORY, FILES)));
  }

  /**
   */
  @Test
  public void getShortDescriptionWithIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getShortDescription(),
        is(Messages.CauseWithIgnoredFiles(DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void hashCodeWithoutIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, "").hashCode(), is(create(DIRECTORY,
        FILES, "").hashCode()));
  }

  /**
   */
  @Test
  public void hashCodeWithIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).hashCode(), is(create(
        DIRECTORY, FILES, IGNORED_FILES).hashCode()));
  }

  /**
   */
  @Test
  public void equalsNull() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES), not(equalTo(null)));
  }

  /**
   */
  @Test
  public void equalsObjectOfDifferentClass() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).equals(new Object()),
        is(false));
  }

  /**
   */
  @Test
  public void equalsDirectoryDiffers() {
    assertThat(create(DIRECTORY, FILES, "").equals(
        create(ALTERNATE_DIRECTORY, FILES, "")), is(false));
  }

  /**
   */
  @Test
  public void equalsFilesDiffers() {
    assertThat(create(DIRECTORY, FILES, "").equals(
        create(DIRECTORY, ALTERNATE_FILES, "")), is(false));
  }

  /**
   */
  @Test
  public void equalsIgnoredFilesDiffers() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).equals(
        create(DIRECTORY, FILES, ALTERNATE_IGNORED_FILES)), is(false));
  }

  /**
   */
  @Test
  public void equalsObjectsAreEqual() {
    assertThat(create(DIRECTORY, FILES, "")
        .equals(create(DIRECTORY, FILES, "")), is(true));
  }

  /**
   */
  @Test
  public void equalsObjectsAreEqualWithIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).equals(
        create(DIRECTORY, FILES, IGNORED_FILES)), is(true));
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
  public void writeToXml() {
    String xml = XStreamUtil.toXml(create(DIRECTORY, FILES, IGNORED_FILES));
    assertThat(xml, is(String.format(XML_TEMPLATE, DIRECTORY, FILES,
        IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void readFromXml() {
    FilesFoundTriggerCause cause = XStreamUtil.fromXml(String.format(
        XML_TEMPLATE, DIRECTORY, FILES, IGNORED_FILES));
    assertThat(ObjectUtils.toString(cause), is(ObjectUtils.toString(create(
        DIRECTORY, FILES, IGNORED_FILES))));
  }

  /**
   */
  @Test
  public void readFromXmlWithMissingFields() {
    FilesFoundTriggerCause cause = XStreamUtil.fromXml(String
        .format("<hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>\n"
            + "</hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>"));
    assertThat(ObjectUtils.toString(cause), is(ObjectUtils.toString(create("",
        "", ""))));
  }

  /**
   * Create a new {@link FilesFoundTriggerCause} instance.
   * 
   * @param directory
   *          the base directory
   * @param files
   *          the pattern of files
   * @param ignoredFiles
   *          the pattern of ignored files
   * @return a new {@link FilesFoundTriggerCause}
   */
  private FilesFoundTriggerCause create(String directory, String files,
      String ignoredFiles) {
    FilesFoundTriggerConfig config = new FilesFoundTriggerConfig(directory,
        files, ignoredFiles);
    return new FilesFoundTriggerCause(config);
  }
}
