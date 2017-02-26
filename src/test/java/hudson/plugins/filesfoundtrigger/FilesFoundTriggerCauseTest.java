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
import static hudson.plugins.filesfoundtrigger.Support.MASTER_NODE;
import static hudson.plugins.filesfoundtrigger.Support.SLAVE_NODE;
import static hudson.plugins.filesfoundtrigger.Support.TRIGGER_NUMBER;
import static hudson.plugins.filesfoundtrigger.Support.cause;
import static hudson.plugins.filesfoundtrigger.Support.fromXml;
import static hudson.plugins.filesfoundtrigger.Support.toXml;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

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
  private static final String XML_MASTER = "<hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "  <triggerNumber>%s</triggerNumber>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>";

  /**
   */
  private static final String XML_SLAVE = "<hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>\n"
      + "  <node>%s</node>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "  <triggerNumber>%s</triggerNumber>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>";

  /**
   */
  @Test
  public void getNodeMaster() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).getNode(),
        is(MASTER_NODE));
  }

  /**
   */
  @Test
  public void getNodeSlave() {
    assertThat(cause(SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).getNode(),
        is(SLAVE_NODE));
  }

  /**
   */
  @Test
  public void getDirectory() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)
        .getDirectory(), is(DIRECTORY));
  }

  /**
   */
  @Test
  public void getFiles() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).getFiles(),
        is(FILES));
  }

  /**
   */
  @Test
  public void getIgnoredFiles() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)
        .getIgnoredFiles(), is(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void getIgnoredFilesNotSpecified() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).getIgnoredFiles(),
        is(""));
  }

  /**
   */
  @Test
  public void getShortDescriptionMaster() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).getShortDescription(),
        is(Messages.Cause(MASTER_NODE, DIRECTORY, FILES)));
  }

  /**
   */
  @Test
  public void getShortDescriptionSlave() {
    assertThat(cause(SLAVE_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).getShortDescription(),
        is(Messages.Cause(SLAVE_NODE, DIRECTORY, FILES)));
  }

  /**
   */
  @Test
  public void getShortDescriptionMasterWithIgnoredFiles() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)
        .getShortDescription(), is(Messages.CauseWithIgnoredFiles(MASTER_NODE,
        DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void getShortDescriptionSlaveWithIgnoredFiles() {
    assertThat(cause(SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)
        .getShortDescription(), is(Messages.CauseWithIgnoredFiles(SLAVE_NODE,
        DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void hashCodeMaster() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).hashCode(),
        is(cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).hashCode()));
  }

  /**
   */
  @Test
  public void hashCodeSlave() {
    assertThat(cause(SLAVE_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).hashCode(),
        is(cause(SLAVE_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).hashCode()));
  }

  /**
   */
  @Test
  public void hashCodeMasterWithIgnoredFiles() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).hashCode(),
        is(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).hashCode()));
  }

  /**
   */
  @Test
  public void hashCodeSlaveWithIgnoredFiles() {
    assertThat(cause(SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).hashCode(),
        is(cause(SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).hashCode()));
  }

  /**
   */
  @Test
  public void equalsNull() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER),
        not(equalTo(null)));
  }

  /**
   */
  @Test
  public void equalsObjectOfDifferentClass() {
    assertThat(
        cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)
            .equals(new Object()), is(false));
  }

  /**
   */
  @Test
  public void equalsNodeDiffers() {
    assertThat(
        cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).equals(
            cause(SLAVE_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER)), is(false));
  }

  /**
   */
  @Test
  public void equalsDirectoryDiffers() {
    assertThat(
        cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).equals(
            cause(MASTER_NODE, ALTERNATE_DIRECTORY, FILES, "", TRIGGER_NUMBER)), is(false));
  }

  /**
   */
  @Test
  public void equalsFilesDiffers() {
    assertThat(
        cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).equals(
            cause(MASTER_NODE, DIRECTORY, ALTERNATE_FILES, "", TRIGGER_NUMBER)), is(false));
  }

  /**
   */
  @Test
  public void equalsIgnoredFilesDiffers() {
    assertThat(
        cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).equals(
            cause(MASTER_NODE, DIRECTORY, FILES, ALTERNATE_IGNORED_FILES, TRIGGER_NUMBER)),
        is(false));
  }

  /**
   */
  @Test
  public void equalsObjectsAreEqual() {
    assertThat(
        cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER).equals(
            cause(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER)), is(true));
  }

  /**
   */
  @Test
  public void equalsObjectsAreEqualWithIgnoredFiles() {
    assertThat(
        cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).equals(
            cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)), is(true));
  }

  /**
   */
  @Test
  public void toStringContainsNodeMaster() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).toString(),
        containsString(MASTER_NODE));
  }

  /**
   */
  @Test
  public void toStringContainsNodeSlave() {
    assertThat(cause(SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).toString(),
        containsString(SLAVE_NODE));
  }

  /**
   */
  @Test
  public void toStringContainsDirectory() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).toString(),
        containsString(DIRECTORY));
  }

  /**
   */
  @Test
  public void toStringContainsFiles() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).toString(),
        containsString(FILES));
  }

  /**
   */
  @Test
  public void toStringContainsIgnoredFiles() {
    assertThat(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER).toString(),
        containsString(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void writeToXmlMaster() {
    String xml = toXml(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(xml,
        is(String.format(XML_MASTER, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)));
  }

  /**
   */
  @Test
  public void writeToXmlSlave() {
    String xml = toXml(cause(SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(xml, is(String.format(XML_SLAVE, SLAVE_NODE, DIRECTORY, FILES,
        IGNORED_FILES, TRIGGER_NUMBER)));
  }

  /**
   */
  @Test
  public void readFromXmlMaster() {
    FilesFoundTriggerCause cause = fromXml(String.format(XML_MASTER, DIRECTORY,
        FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(String.valueOf(cause),
        is(String.valueOf(cause(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER))));
  }

  /**
   */
  @Test
  public void readFromXmlSlave() {
    FilesFoundTriggerCause cause = fromXml(String.format(XML_SLAVE, SLAVE_NODE,
        DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(String.valueOf(cause),
        is(String.valueOf(cause(SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER))));
  }

  /**
   */
  @Test
  public void readFromXmlWithMissingFields() {
    FilesFoundTriggerCause cause = fromXml(String
        .format("<hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>\n"
            + "</hudson.plugins.filesfoundtrigger.FilesFoundTriggerCause>"));
    assertThat(String.valueOf(cause), is(String.valueOf(cause("", "", "", "", ""))));
  }
}
