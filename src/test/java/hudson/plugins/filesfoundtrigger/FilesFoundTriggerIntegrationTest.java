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

import static hudson.plugins.filesfoundtrigger.Support.SPEC;
import static hudson.plugins.filesfoundtrigger.Support.masterConfig;
import static hudson.plugins.filesfoundtrigger.Support.trigger;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import hudson.ExtensionList;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;

import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Integration test for the Files Found Trigger plugin. Each of the test methods
 * is executed inside a separate Jenkins instance.
 * 
 * @author Steven G. Brown
 */
public class FilesFoundTriggerIntegrationTest {

  /**
   */
  @Rule
  public JenkinsRule j = new JenkinsRule();

  /**
   */
  @SuppressWarnings({ "deprecation", "rawtypes" })
  public void setUp() throws Exception {
    ExtensionList<Descriptor> descriptors = Hudson.getInstance()
        .getExtensionList(Descriptor.class);
    descriptors.add(new FilesFoundTrigger.DescriptorImpl());
    descriptors.add(new FilesFoundTriggerConfig.DescriptorImpl());
  }

  /**
   */
  public void testGetConfigClassDescriptor() {
    assertThat(FilesFoundTriggerConfig.getClassDescriptor(),
        isA(FilesFoundTriggerConfig.DescriptorImpl.class));
  }

  /**
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void testGetConfigInstanceDescriptor() {
    assertThat(masterConfig().getDescriptor(),
        isA((Class) FilesFoundTriggerConfig.DescriptorImpl.class));
  }

  /**
   * <pre>
   * http://wiki.jenkins-ci.org/display/JENKINS/Unit+Test#UnitTest-Configurationroundtriptesting
   * </pre>
   * 
   * @throws Exception
   *           on error
   */
  public void testSave() throws Exception {
    FreeStyleProject project = j.createFreeStyleProject();
    FilesFoundTrigger before = trigger(SPEC, masterConfig());
    project.addTrigger(before);

    j.submit(j.createWebClient().getPage(project, "configure")
        .getFormByName("config"));

    FilesFoundTrigger after = project.getTrigger(FilesFoundTrigger.class);

    assertThat(String.valueOf(after), is(String.valueOf(before)));
  }
}
