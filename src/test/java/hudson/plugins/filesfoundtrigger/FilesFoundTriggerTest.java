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
import static hudson.plugins.filesfoundtrigger.Support.SLAVE_NODE;
import static hudson.plugins.filesfoundtrigger.Support.SPEC;
import static hudson.plugins.filesfoundtrigger.Support.TRIGGER_NUMBER;
import static hudson.plugins.filesfoundtrigger.Support.emptyConfig;
import static hudson.plugins.filesfoundtrigger.Support.fromXml;
import static hudson.plugins.filesfoundtrigger.Support.masterConfig;
import static hudson.plugins.filesfoundtrigger.Support.slaveConfig;
import static hudson.plugins.filesfoundtrigger.Support.toXml;
import static hudson.plugins.filesfoundtrigger.Support.trigger;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

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

import hudson.model.BuildableItem;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.Saveable;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;

/**
 * Unit test for the {@link FilesFoundTrigger} class.
 * 
 * @author Steven G. Brown
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
@SuppressWarnings("boxing")
public class FilesFoundTriggerTest {

  /**
   */
  private static final String XML_MASTER = "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
      + "  <spec>%s</spec>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "  <triggerNumber>%s</triggerNumber>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>";

  /**
   */
  private static final String XML_SLAVE = "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
      + "  <spec>%s</spec>\n"
      + "  <node>%s</node>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "  <triggerNumber>%s</triggerNumber>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>";

  /**
   */
  private static final String XML_MASTER_ADDITIONAL_CONFIGS = "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
      + "  <spec>%s</spec>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "  <triggerNumber>%s</triggerNumber>\n"
      + "  <additionalConfigs>\n"
      + "    <hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
      + "      <directory>%s</directory>\n"
      + "      <files>%s</files>\n"
      + "      <ignoredFiles>%s</ignoredFiles>\n"
      + "      <triggerNumber>%s</triggerNumber>\n"
      + "    </hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
      + "  </additionalConfigs>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>";

  /**
   */
  private static final String XML_SLAVE_ADDITIONAL_CONFIGS = "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
      + "  <spec>%s</spec>\n"
      + "  <node>%s</node>\n"
      + "  <directory>%s</directory>\n"
      + "  <files>%s</files>\n"
      + "  <ignoredFiles>%s</ignoredFiles>\n"
      + "  <triggerNumber>%s</triggerNumber>\n"
      + "  <additionalConfigs>\n"
      + "    <hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
      + "      <node>%s</node>\n"
      + "      <directory>%s</directory>\n"
      + "      <files>%s</files>\n"
      + "      <ignoredFiles>%s</ignoredFiles>\n"
      + "      <triggerNumber>%s</triggerNumber>\n"
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
    Jenkins jenkins = mock(Jenkins.class);
    globalNodeProperties = new DescribableList<NodeProperty<?>, NodePropertyDescriptor>(
        Saveable.NOOP);
    when(jenkins.getGlobalNodeProperties()).thenReturn(globalNodeProperties);
    mockStatic(Jenkins.class);
    when(Jenkins.getInstance()).thenReturn(jenkins);

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
    assertThat(String.valueOf(trigger(SPEC).getConfigs()),
        is(singletonList(emptyConfig()).toString()));
  }

  /**
   */
  @Test
  public void getConfigsOne() {
    assertThat(String.valueOf(trigger(SPEC, masterConfig()).getConfigs()),
        is(Arrays.asList(masterConfig()).toString()));
  }

  /**
   */
  @Test
  public void getConfigsTwo() {
    assertThat(String.valueOf(trigger(SPEC, masterConfig(), masterConfig())
        .getConfigs()), is(Arrays.asList(masterConfig(), masterConfig())
        .toString()));
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
    defineGlobalProperty("node", "master");
    defineGlobalProperty("directory", expandedConfig.getDirectory());
    defineGlobalProperty("files", expandedConfig.getFiles());
    defineGlobalProperty("ignoredFiles", expandedConfig.getIgnoredFiles());
	defineGlobalProperty("triggerNumber", expandedConfig.getTriggerNumber());
    FilesFoundTriggerConfig config = new FilesFoundTriggerConfig("$node",
        "$directory", "$files", "$ignoredFiles", "$triggerNumber");
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
    assertThat(trigger(SPEC, masterConfig()).toString(),
        containsString(masterConfig().toString()));
  }

  /**
   */
  @Test
  public void writeToXmlMaster() {
    String xml = toXml(trigger(SPEC, masterConfig()));
    assertThat(xml,
        is(String.format(XML_MASTER, SPEC, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)));
  }

  /**
   */
  @Test
  public void writeToXmlSlave() {
    String xml = toXml(trigger(SPEC, slaveConfig()));
    assertThat(xml, is(String.format(XML_SLAVE, SPEC, SLAVE_NODE, DIRECTORY,
        FILES, IGNORED_FILES, TRIGGER_NUMBER)));
  }

  /**
   */
  @Test
  public void writeToXmlMasterWithAdditionalConfigs() {
    String xml = toXml(trigger(SPEC, masterConfig(), masterConfig()));
    assertThat(xml, is(String.format(XML_MASTER_ADDITIONAL_CONFIGS, SPEC,
        DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER)));
  }

  /**
   */
  @Test
  public void writeToXmlSlaveWithAdditionalConfigs() {
    String xml = toXml(trigger(SPEC, slaveConfig(), slaveConfig()));
    assertThat(xml, is(String.format(XML_SLAVE_ADDITIONAL_CONFIGS, SPEC,
        SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER, SLAVE_NODE, DIRECTORY,
        FILES, IGNORED_FILES, TRIGGER_NUMBER)));
  }

  /**
   */
  @Test
  public void readFromXmlMaster() {
    FilesFoundTrigger trigger = fromXml(String.format(XML_MASTER, SPEC,
        DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(String.valueOf(trigger),
        is(String.valueOf(trigger(SPEC, masterConfig()))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlSlave() {
    FilesFoundTrigger trigger = fromXml(String.format(XML_SLAVE, SPEC,
        SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(String.valueOf(trigger),
        is(String.valueOf(trigger(SPEC, slaveConfig()))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlMasterWithAdditionalConfigs() {
    FilesFoundTrigger trigger = fromXml(String.format(
        XML_MASTER_ADDITIONAL_CONFIGS, SPEC, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER, 
        DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(String.valueOf(trigger),
        is(String.valueOf(trigger(SPEC, masterConfig(), masterConfig()))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlSlaveWithAdditionalConfigs() {
    FilesFoundTrigger trigger = fromXml(String.format(
        XML_SLAVE_ADDITIONAL_CONFIGS, SPEC, SLAVE_NODE, DIRECTORY, FILES,
        IGNORED_FILES, TRIGGER_NUMBER, SLAVE_NODE, DIRECTORY, FILES, IGNORED_FILES, TRIGGER_NUMBER));
    assertThat(String.valueOf(trigger),
        is(String.valueOf(trigger(SPEC, slaveConfig(), slaveConfig()))));
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
        new FilesFoundTriggerConfig("", "", "", "", "1")))));
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
    assertThat(String.valueOf(trigger),
        is(String.valueOf(trigger(SPEC, emptyConfig(), emptyConfig()))));
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
    return new FilesFoundTriggerConfig(MASTER_NODE, folder.getRoot()
        .getAbsolutePath(), FILES, IGNORED_FILES, TRIGGER_NUMBER);
  }

  /**
   * Create a new {@link FilesFoundTriggerConfig} that will not find files.
   * 
   * @return a new configuration that will not find files
   */
  private FilesFoundTriggerConfig notFoundConfig() {
    return new FilesFoundTriggerConfig(MASTER_NODE, "", "", "", TRIGGER_NUMBER);
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
