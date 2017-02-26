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

import static hudson.plugins.filesfoundtrigger.Support.DIRECTORY;
import static hudson.plugins.filesfoundtrigger.Support.FILES;
import static hudson.plugins.filesfoundtrigger.Support.IGNORED_FILES;
import static hudson.plugins.filesfoundtrigger.Support.MASTER_NODE;
import static hudson.plugins.filesfoundtrigger.Support.TRIGGER_NUMBER;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Unit test for the {@link FilesFoundTriggerCause} class.
 * 
 * @author Steven G. Brown
 */
@SuppressWarnings("boxing")
public class FilesFoundTriggerCauseTest {

  /**
   */
  @Test
  public void testEqualsAndHashCode() {
    EqualsVerifier.forClass(FilesFoundTriggerCause.class).verify();
  }

  /**
   */
  @Test
  public void getShortDescription() {
    FilesFoundTriggerCause cause = new FilesFoundTriggerCause(
        new FilesFoundTriggerConfig(MASTER_NODE, DIRECTORY, FILES, "", TRIGGER_NUMBER));
    assertThat(cause.getShortDescription(), is(Messages.Cause(MASTER_NODE, DIRECTORY, FILES)));
  }

  /**
   */
  @Test
  public void getShortDescriptionWithIgnoredFiles() {
    FilesFoundTriggerCause cause = new FilesFoundTriggerCause(
        new FilesFoundTriggerConfig(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(cause.getShortDescription(),
        is(Messages.CauseWithIgnoredFiles(MASTER_NODE, DIRECTORY, FILES, IGNORED_FILES)));
  }
}
