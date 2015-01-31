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
import static hudson.plugins.filesfoundtrigger.Support.SPEC;
import static hudson.plugins.filesfoundtrigger.Support.config;
import static hudson.plugins.filesfoundtrigger.Support.emptyConfig;
import static hudson.plugins.filesfoundtrigger.Support.fromXml;
import static hudson.plugins.filesfoundtrigger.Support.toXml;
import static hudson.plugins.filesfoundtrigger.Support.trigger;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import hudson.model.BuildableItem;
import hudson.model.Item;
import hudson.model.Saveable;
import hudson.model.Cause;
import hudson.model.Hudson;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.util.DescribableList;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test for the {@link FilesFoundTrigger} class.
 * 
 * @author Steven G. Brown
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Hudson.class)
@SuppressWarnings("boxing")
public class FilesFoundTriggerTest {

  /**
   */
  private static final String XML = "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
      + "  <spec>%s</spec>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>";

  /**
   */
  private static final String XML_ADDITIONAL_CONFIGS = "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
      + "  <spec>%s</spec>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "  <additionalConfigs>\n"
      + "    <hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
      + "      <directory>%s</directory>\n"
      + "      <files>%s</files>\n"
      + "      <ignoredFiles>%s</ignoredFiles>\n"
      + "    </hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
      + "  </additionalConfigs>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>";

  /**
   */
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private DescribableList<NodeProperty<?>, NodePropertyDescriptor> globalNodeProperties;

  private BuildableItem job;

  /**
   */
  @Before
  public void setUp() {
    Hudson hudson = mock(Hudson.class);
    globalNodeProperties = new DescribableList<NodeProperty<?>, NodePropertyDescriptor>(
        Saveable.NOOP);
    when(hudson.getGlobalNodeProperties()).thenReturn(globalNodeProperties);
    mockStatic(Hudson.class);
    when(Hudson.getInstance()).thenReturn(hudson);

    job = mock(BuildableItem.class);
    when(job.getFullName()).thenReturn(
        FilesFoundTriggerTest.class.getSimpleName());
  }

  /**
   */
  @Test
  public void getSpec() {
    assertThat(trigger(SPEC).getSpec(), is(SPEC));
  }

  /**
   */
  @Test
  public void getConfigsNull() {
    assertThat(String.valueOf(trigger(SPEC, (FilesFoundTriggerConfig[]) null)
        .getConfigs()), is(singletonList(emptyConfig()).toString()));
  }

  /**
   */
  @Test
  public void getConfigsEmpty() {
    assertThat(String.valueOf(trigger(SPEC).getConfigs()), is(singletonList(
        emptyConfig()).toString()));
  }

  /**
   */
  @Test
  public void getConfigsOne() {
    assertThat(String.valueOf(trigger(SPEC, config()).getConfigs()), is(Arrays
        .asList(config()).toString()));
  }

  /**
   */
  @Test
  public void getConfigsTwo() {
    assertThat(String.valueOf(trigger(SPEC, config(), config()).getConfigs()),
        is(Arrays.asList(config(), config()).toString()));
  }

  /**
   * @throws IOException
   *           If an I/O error occurred
   */
  @Test
  public void runAndScheduleBuild() throws IOException {
    FilesFoundTriggerConfig config = foundConfig();
    FilesFoundTrigger trigger = trigger(SPEC, config);
    trigger.start(job, true);
    trigger.run();
    verify(job, times(1)).scheduleBuild(0, new FilesFoundTriggerCause(config));
  }

  /**
   * @throws IOException
   *           If an I/O error occurred
   */
  @Test
  public void runAndScheduleBuildWithProperties() throws IOException {
    FilesFoundTriggerConfig expandedConfig = foundConfig();
    defineGlobalProperty("directory", expandedConfig.getDirectory());
    defineGlobalProperty("files", expandedConfig.getFiles());
    defineGlobalProperty("ignoredFiles", expandedConfig.getIgnoredFiles());
    FilesFoundTriggerConfig config = new FilesFoundTriggerConfig("$directory",
        "$files", "$ignoredFiles");
    FilesFoundTrigger trigger = trigger(SPEC, config);
    trigger.start(job, true);
    trigger.run();
    verify(job, times(1)).scheduleBuild(0,
        new FilesFoundTriggerCause(expandedConfig));
  }

  /**
   */
  @Test
  public void runAndDontScheduleBuild() {
    FilesFoundTriggerConfig config = notFoundConfig();
    FilesFoundTrigger trigger = trigger(SPEC, config);
    trigger.start(job, true);
    trigger.run();
    verify(job, never()).scheduleBuild(anyInt(), any(Cause.class));
  }

  /**
   */
  @Test
  public void runWithNoConfigs() {
    FilesFoundTrigger trigger = trigger(SPEC);
    trigger.start(job, true);
    trigger.run();
    verify(job, never()).scheduleBuild(anyInt(), any(Cause.class));
  }

  /**
   */
  @Test
  public void toStringContainsSpec() {
    assertThat(trigger(SPEC).toString(), containsString(SPEC));
  }

  /**
   */
  @Test
  public void toStringContainsConfig() {
    assertThat(trigger(SPEC, config()).toString(), containsString(config()
        .toString()));
  }

  /**
   */
  @Test
  public void writeToXml() {
    String xml = toXml(trigger(SPEC, config()));
    assertThat(xml, is(String
        .format(XML, SPEC, DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void writeToXmlWithAdditionalConfigs() {
    String xml = toXml(trigger(SPEC, config(), config()));
    assertThat(xml, is(String.format(XML_ADDITIONAL_CONFIGS, SPEC, DIRECTORY,
        FILES, IGNORED_FILES, DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void readFromXml() {
    FilesFoundTrigger trigger = fromXml(String.format(XML, SPEC, DIRECTORY,
        FILES, IGNORED_FILES));
    assertThat(String.valueOf(trigger), is(String.valueOf(trigger(SPEC,
        config()))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlWithAdditionalConfigs() {
    FilesFoundTrigger trigger = fromXml(String.format(XML_ADDITIONAL_CONFIGS,
        SPEC, DIRECTORY, FILES, IGNORED_FILES, DIRECTORY, FILES, IGNORED_FILES));
    assertThat(String.valueOf(trigger), is(String.valueOf(trigger(SPEC,
        config(), config()))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlWithMissingFields() {
    FilesFoundTrigger trigger = fromXml(String.format(
        "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
            + "  <spec>%s</spec>\n"
            + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>", SPEC));
    assertThat(String.valueOf(trigger), is(String.valueOf(trigger(SPEC,
        new FilesFoundTriggerConfig("", "", "")))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlWithAdditionalConfigsAndMissingFields() {
    FilesFoundTrigger trigger = fromXml(String
        .format(
            "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
                + "  <spec>%s</spec>\n"
                + "  <additionalConfigs>\n"
                + "    <hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
                + "    </hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
                + "  </additionalConfigs>\n"
                + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>", SPEC));
    assertThat(String.valueOf(trigger), is(String.valueOf(trigger(SPEC,
        emptyConfig(), emptyConfig()))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void isApplicableNull() {
    assertThat(new FilesFoundTrigger.DescriptorImpl().isApplicable(null),
        is(false));
  }

  /**
   */
  @Test
  public void isApplicableItem() {
    Item item = mock(Item.class);
    assertThat(new FilesFoundTrigger.DescriptorImpl().isApplicable(item),
        is(false));
  }

  /**
   */
  @Test
  public void isApplicableBuildableItem() {
    assertThat(new FilesFoundTrigger.DescriptorImpl().isApplicable(job),
        is(true));
  }

  /**
   */
  @Test
  public void getDisplayName() {
    assertThat(new FilesFoundTrigger.DescriptorImpl().getDisplayName(),
        not(nullValue()));
  }

  /**
   * Create a new {@link FilesFoundTriggerConfig} that will find files.
   * 
   * @return a new configuration that will find files
   * @throws IOException
   */
  private FilesFoundTriggerConfig foundConfig() throws IOException {
    folder.newFile("test");
    return new FilesFoundTriggerConfig(folder.getRoot().getAbsolutePath(),
        FILES, IGNORED_FILES);
  }

  /**
   * Create a new {@link FilesFoundTriggerConfig} that will not find files.
   * 
   * @return a new configuration that will not find files
   */
  private FilesFoundTriggerConfig notFoundConfig() {
    return new FilesFoundTriggerConfig("", "", "");
  }

  /**
   * Get the value of the tabs field from the given trigger object.
   * 
   * @param trigger
   *          the trigger to inspect
   * @return the value of the tabs field
   */
  private Object getTabs(FilesFoundTrigger trigger) {
    try {
      Field field = FilesFoundTrigger.class.getSuperclass().getDeclaredField(
          "tabs");
      field.setAccessible(true);
      return field.get(trigger);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private void defineGlobalProperty(String name, String value) {
    EnvironmentVariablesNodeProperty.Entry entry = new EnvironmentVariablesNodeProperty.Entry(
        name, value);
    EnvironmentVariablesNodeProperty property = new EnvironmentVariablesNodeProperty(
        entry);
    globalNodeProperties.add(property);
  }
}
