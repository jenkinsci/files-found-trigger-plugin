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
import static hudson.plugins.filesfoundtrigger.Support.cause;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.EnvVars;
import hudson.model.BuildListener;
import hudson.model.Run;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the {@link FilesFoundEnvironmentContributor} class.
 * 
 * @author Steven G. Brown
 */
public class FilesFoundEnvironmentContributorTest {

  private Run<?, ?> run;

  /**
   */
  @Before
  public void setUp() {
    run = mock(Run.class);
  }

  /**
   */
  @Test
  public void filesFoundCause() {
    Map<String, String> expected = new HashMap<String, String>();
    expected.put("filesfound_setting_directory", DIRECTORY);
    expected.put("filesfound_setting_files", FILES);
    expected.put("filesfound_setting_ignoredfiles", IGNORED_FILES);

    FilesFoundTriggerCause cause = cause(DIRECTORY, FILES, IGNORED_FILES);
    when(run.getCause(FilesFoundTriggerCause.class)).thenReturn(cause);
    assertThat(contributeEnvVars(), is(expected));
  }

  /**
   */
  @Test
  public void noFilesFoundCause() {
    assertThat(contributeEnvVars(), is(Collections.<String, String> emptyMap()));
  }

  private Map<String, String> contributeEnvVars() {
    EnvVars envVars = new EnvVars();
    BuildListener buildListener = mock(BuildListener.class);
    new FilesFoundEnvironmentContributor().buildEnvironmentFor(run, envVars,
        buildListener);
    return envVars;
  }
}
