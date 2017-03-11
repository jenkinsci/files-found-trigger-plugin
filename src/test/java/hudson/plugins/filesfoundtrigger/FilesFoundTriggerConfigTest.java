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

import static hudson.plugins.filesfoundtrigger.Support.IGNORED_FILES;
import static hudson.plugins.filesfoundtrigger.Support.MASTER_NODE;
import static hudson.plugins.filesfoundtrigger.Support.TRIGGER_NUMBER;
import static hudson.util.FormValidation.Kind.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Objects;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import hudson.Util;
import hudson.model.Saveable;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Unit test for the {@link FilesFoundTriggerConfig} class.
 * 
 * @author Steven G. Brown
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class FilesFoundTriggerConfigTest {

  /**
   */
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private DescribableList<NodeProperty<?>, NodePropertyDescriptor> globalNodeProperties;

  /**
   */
  @Before
  public void setUp() {
    Jenkins jenkins = mock(Jenkins.class);
    globalNodeProperties = new DescribableList<NodeProperty<?>, NodePropertyDescriptor>(
        Saveable.NOOP);
    when(jenkins.getGlobalNodeProperties()).thenReturn(globalNodeProperties);
    mockStatic(Jenkins.class);
    when(Jenkins.getInstance()).thenReturn(jenkins);
  }

  /**
   */
  @Test
  public void testEqualsAndHashCode() {
    EqualsVerifier.forClass(FilesFoundTriggerConfig.class).verify();
  }

  /**
   * @throws Exception
   */
  @Test
  public void doTestConfiguration() throws Exception {
    folder.newFile("test");
    defineGlobalProperty("property", "test");

    FormValidation formValidation = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(MASTER_NODE, folder.getRoot().getAbsolutePath(), "$property",
            IGNORED_FILES, TRIGGER_NUMBER);

    assertThat(formValidation, is(validation(OK, Messages.SingleFileFound("test"))));
  }

  private void defineGlobalProperty(String name, String value) {
    EnvironmentVariablesNodeProperty.Entry entry = new EnvironmentVariablesNodeProperty.Entry(name,
        value);
    EnvironmentVariablesNodeProperty property = new EnvironmentVariablesNodeProperty(entry);
    globalNodeProperties.add(property);
  }

  private static Matcher<FormValidation> validation(final FormValidation.Kind kind,
      final String message) {
    return new CustomMatcher<FormValidation>(
        "FormValidation of kind " + kind + " with message " + message) {

      @Override
      public boolean matches(Object item) {
        if (item instanceof FormValidation) {
          FormValidation formValidation = (FormValidation) item;
          return formValidation.kind == kind
              && Objects.equals(formValidation.getMessage(), Util.escape(message));
        }
        return false;
      }
    };
  }
}
