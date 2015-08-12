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

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.TaskListener;
import hudson.model.EnvironmentContributor;
import hudson.model.Run;

/**
 * Contributes environment variables to builds that were caused by the
 * {@link FilesFoundTrigger}.
 * 
 * @author Steven G. Brown
 * @since 1.3
 */
@Extension
public final class FilesFoundEnvironmentContributor extends
    EnvironmentContributor {

  /**
   * {@inheritDoc}
   */
  @Override
  public void buildEnvironmentFor(@SuppressWarnings("rawtypes") Run r,
      EnvVars envs, TaskListener listener) {
    buildEnvironmentFor(r, envs);
  }

  private void buildEnvironmentFor(Run<?, ?> run, EnvVars envVars) {
    FilesFoundTriggerCause cause = run.getCause(FilesFoundTriggerCause.class);
    if (cause != null) {
      envVars.put(name("node"), cause.getNode());
      envVars.put(name("directory"), cause.getDirectory());
      envVars.put(name("files"), cause.getFiles());
      envVars.put(name("ignoredfiles"), cause.getIgnoredFiles());
    }
  }

  private String name(String envVar) {
    return "filesfound_setting_" + envVar;
  }
}
