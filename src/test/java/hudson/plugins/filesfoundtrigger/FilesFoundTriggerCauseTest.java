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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import antlr.ANTLRException;

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
  public void testGetIgnoredFilesNotSpecified() {
    assertThat(create(DIRECTORY, FILES, "").getIgnoredFiles(), is(""));
  }

  /**
   */
  @Test
  public void testGetShortDescription() {
    assertThat(create(DIRECTORY, FILES, "").getShortDescription(), is(Messages
        .Cause(DIRECTORY, FILES)));
  }

  /**
   */
  @Test
  public void testGetShortDescriptionWithIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).getShortDescription(),
        is(Messages.CauseWithIgnoredFiles(DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void testHashCode() {
    assertThat(create(DIRECTORY, FILES, "").hashCode(), is(create(DIRECTORY,
        FILES, "").hashCode()));
  }

  /**
   */
  @Test
  public void testHashCodeWithIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES).hashCode(), is(create(
        DIRECTORY, FILES, IGNORED_FILES).hashCode()));
  }

  /**
   */
  @Test
  public void testEqualsDirectoryDiffers() {
    assertThat(create(DIRECTORY, FILES, ""), not(equalTo(create(
        ALTERNATE_DIRECTORY, FILES, ""))));
  }

  /**
   */
  @Test
  public void testEqualsFilesDiffers() {
    assertThat(create(DIRECTORY, FILES, ""), not(equalTo(create(DIRECTORY,
        ALTERNATE_FILES, ""))));
  }

  /**
   */
  @Test
  public void testEqualsIgnoredFilesDiffers() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES), not(equalTo(create(
        DIRECTORY, FILES, ALTERNATE_IGNORED_FILES))));
  }

  /**
   */
  @Test
  public void testEqualsObjectsAreEqual() {
    assertThat(create(DIRECTORY, FILES, ""), equalTo(create(DIRECTORY, FILES,
        "")));
  }

  /**
   */
  @Test
  public void testEqualsObjectsAreEqualWithIgnoredFiles() {
    assertThat(create(DIRECTORY, FILES, IGNORED_FILES), equalTo(create(
        DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void testUnmarshal() throws Exception {
    String xml = String.format(XML_TEMPLATE, DIRECTORY, FILES, IGNORED_FILES);
    FilesFoundTriggerCause cause = TestAssistant.unmarshal(xml);
    assertThat(String.format(XML_TEMPLATE, cause.getDirectory(), cause
        .getFiles(), cause.getIgnoredFiles()), is(xml));
  }

  /**
   */
  @Test
  public void testUnmarshalWithMissingFields() throws Exception {
    String xmlTemplateWithMissingFields = "<hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>\n"
        + "</hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>";
    String xml = String.format(xmlTemplateWithMissingFields);
    FilesFoundTriggerCause cause = TestAssistant.unmarshal(xml);
    assertThat(String.format(XML_TEMPLATE, cause.getDirectory(), cause
        .getFiles(), cause.getIgnoredFiles()), is(String.format(XML_TEMPLATE,
        "", "", "")));
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
    try {
      FilesFoundTrigger trigger = new FilesFoundTrigger("", directory, files,
          ignoredFiles);
      FilesFoundTriggerCause triggerCause = new FilesFoundTriggerCause(trigger);
      return triggerCause;
    } catch (ANTLRException ex) {
      throw new RuntimeException(ex);
    }
  }
}
