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

import static hudson.plugins.filesfoundtrigger.Support.ALTERNATE_DIRECTORY;
import static hudson.plugins.filesfoundtrigger.Support.ALTERNATE_FILES;
import static hudson.plugins.filesfoundtrigger.Support.ALTERNATE_IGNORED_FILES;
import static hudson.plugins.filesfoundtrigger.Support.DIRECTORY;
import static hudson.plugins.filesfoundtrigger.Support.FILES;
import static hudson.plugins.filesfoundtrigger.Support.IGNORED_FILES;
import static hudson.plugins.filesfoundtrigger.Support.cause;
import static hudson.plugins.filesfoundtrigger.Support.fromXml;
import static hudson.plugins.filesfoundtrigger.Support.toXml;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

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
  private static final String XML = "<hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>";

  /**
   */
  @Test
  public void getDirectory() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).getDirectory(),
        is(DIRECTORY));
  }

  /**
   */
  @Test
  public void getFiles() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).getFiles(), is(FILES));
  }

  /**
   */
  @Test
  public void getIgnoredFiles() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).getIgnoredFiles(),
        is(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void getIgnoredFilesNotSpecified() {
    assertThat(cause(DIRECTORY, FILES, "").getIgnoredFiles(), is(""));
  }

  /**
   */
  @Test
  public void getShortDescription() {
    assertThat(cause(DIRECTORY, FILES, "").getShortDescription(), is(Messages
        .Cause(DIRECTORY, FILES)));
  }

  /**
   */
  @Test
  public void getShortDescriptionWithIgnoredFiles() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).getShortDescription(),
        is(Messages.CauseWithIgnoredFiles(DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void hashCodeWithoutIgnoredFiles() {
    assertThat(cause(DIRECTORY, FILES, "").hashCode(), is(cause(DIRECTORY,
        FILES, "").hashCode()));
  }

  /**
   */
  @Test
  public void hashCodeWithIgnoredFiles() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).hashCode(), is(cause(
        DIRECTORY, FILES, IGNORED_FILES).hashCode()));
  }

  /**
   */
  @Test
  public void equalsNull() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES), not(equalTo(null)));
  }

  /**
   */
  @Test
  public void equalsObjectOfDifferentClass() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).equals(new Object()),
        is(false));
  }

  /**
   */
  @Test
  public void equalsDirectoryDiffers() {
    assertThat(cause(DIRECTORY, FILES, "").equals(
        cause(ALTERNATE_DIRECTORY, FILES, "")), is(false));
  }

  /**
   */
  @Test
  public void equalsFilesDiffers() {
    assertThat(cause(DIRECTORY, FILES, "").equals(
        cause(DIRECTORY, ALTERNATE_FILES, "")), is(false));
  }

  /**
   */
  @Test
  public void equalsIgnoredFilesDiffers() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).equals(
        cause(DIRECTORY, FILES, ALTERNATE_IGNORED_FILES)), is(false));
  }

  /**
   */
  @Test
  public void equalsObjectsAreEqual() {
    assertThat(cause(DIRECTORY, FILES, "").equals(cause(DIRECTORY, FILES, "")),
        is(true));
  }

  /**
   */
  @Test
  public void equalsObjectsAreEqualWithIgnoredFiles() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).equals(
        cause(DIRECTORY, FILES, IGNORED_FILES)), is(true));
  }

  /**
   */
  @Test
  public void toStringContainsDirectory() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(DIRECTORY));
  }

  /**
   */
  @Test
  public void toStringContainsFiles() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(FILES));
  }

  /**
   */
  @Test
  public void toStringContainsIgnoredFiles() {
    assertThat(cause(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void writeToXml() {
    String xml = toXml(cause(DIRECTORY, FILES, IGNORED_FILES));
    assertThat(xml, is(String.format(XML, DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void readFromXml() {
    FilesFoundTriggerCause cause = fromXml(String.format(XML, DIRECTORY, FILES,
        IGNORED_FILES));
    assertThat(String.valueOf(cause), is(String.valueOf(cause(DIRECTORY, FILES,
        IGNORED_FILES))));
  }

  /**
   */
  @Test
  public void readFromXmlWithMissingFields() {
    FilesFoundTriggerCause cause = fromXml(String
        .format("<hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>\n"
            + "</hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>"));
    assertThat(String.valueOf(cause), is(String.valueOf(cause("", "", ""))));
  }
}
