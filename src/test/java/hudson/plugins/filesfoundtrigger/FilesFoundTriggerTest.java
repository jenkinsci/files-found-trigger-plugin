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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import hudson.model.BuildableItem;
import hudson.model.Item;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Rule;
import org.junit.Test;

import antlr.ANTLRException;

/**
 * Unit test for the {@link FilesFoundTrigger} class.
 * 
 * @author Steven G. Brown
 */
@SuppressWarnings("boxing")
public class FilesFoundTriggerTest {

  /**
   */
  private static final String SPEC = "* * * * *";

  /**
   */
  private static final String DIRECTORY = "C:/";

  /**
   */
  private static final String FILES = "**";

  /**
   */
  private static final String IGNORED_FILES = "ignore";

  /**
   */
  private static final String XML_TEMPLATE = "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
      + "  <spec>%s</spec>\n"
      + "  <configs>\n"
      + "    <hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
      + "      <directory>%s</directory>\n"
      + "      <files>%s</files>\n"
      + "      <ignoredFiles>%s</ignoredFiles>\n"
      + "    </hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
      + "  </configs>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>";

  /**
   */
  private static final String XML_TEMPLATE_EMPTY_CONFIGS = "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
      + "  <spec>%s</spec>\n"
      + "  <configs/>\n"
      + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>";

  /**
   */
  @Rule
  public TemporaryFolderRule folder = new TemporaryFolderRule();

  /**
   */
  @Test
  public void getSpec() {
    assertThat(create(SPEC).getSpec(), is(SPEC));
  }

  /**
   */
  @Test
  public void getConfigsNull() {
    assertThat(create(SPEC, (FilesFoundTriggerConfig[]) null).getConfigs(),
        is(Collections.<FilesFoundTriggerConfig> emptyList()));
  }

  /**
   */
  @Test
  public void getConfigsEmpty() {
    assertThat(create(SPEC).getConfigs(), is(Collections
        .<FilesFoundTriggerConfig> emptyList()));
  }

  /**
   */
  @Test
  public void getConfigs() {
    assertThat(create(SPEC, config(), config()).getConfigs().size(), is(2));
  }

  /**
   */
  @Test
  public void runAndScheduleBuild() {
    FilesFoundTriggerConfig config = foundConfig();
    FilesFoundTrigger trigger = create(SPEC, config);
    BuildableItem job = mock(BuildableItem.class);
    trigger.start(job, true);
    trigger.run();
    verify(job, times(1)).scheduleBuild(0, new FilesFoundTriggerCause(config));
  }

  /**
   */
  @Test
  public void runAndDontScheduleBuild() {
    FilesFoundTriggerConfig config = notFoundConfig();
    FilesFoundTrigger trigger = create(SPEC, config);
    BuildableItem job = mock(BuildableItem.class);
    trigger.start(job, true);
    trigger.run();
    verifyZeroInteractions(job);
  }

  /**
   */
  @Test
  public void runWithNoConfigs() {
    FilesFoundTrigger trigger = create(SPEC);
    BuildableItem job = mock(BuildableItem.class);
    trigger.start(job, true);
    trigger.run();
    verifyZeroInteractions(job);
  }

  /**
   */
  @Test
  public void toStringContainsSpec() {
    assertThat(create(SPEC).toString(), containsString(SPEC));
  }

  /**
   */
  @Test
  public void toStringContainsConfig() {
    assertThat(create(SPEC, config()).toString(), containsString(config()
        .toString()));
  }

  /**
   */
  @Test
  public void writeToXml() {
    String xml = XStreamUtil.toXml(create(SPEC, config()));
    assertThat(xml, is(String.format(XML_TEMPLATE, SPEC, DIRECTORY, FILES,
        IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void writeToXmlEmptyConfigs() {
    String xml = XStreamUtil.toXml(create(SPEC));
    assertThat(xml, is(String.format(XML_TEMPLATE_EMPTY_CONFIGS, SPEC,
        DIRECTORY, FILES, IGNORED_FILES)));
  }

  /**
   */
  @Test
  public void readFromXml() {
    FilesFoundTrigger trigger = XStreamUtil.fromXml(String.format(XML_TEMPLATE,
        SPEC, DIRECTORY, FILES, IGNORED_FILES));
    assertThat(ObjectUtils.toString(trigger), is(ObjectUtils.toString(create(
        SPEC, config()))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlWithEmptyConfigs() {
    FilesFoundTrigger trigger = XStreamUtil.fromXml(String.format(
        XML_TEMPLATE_EMPTY_CONFIGS, SPEC));
    assertThat(ObjectUtils.toString(trigger), is(ObjectUtils
        .toString(create(SPEC))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlWithMissingConfigFields() {
    FilesFoundTrigger trigger = XStreamUtil
        .fromXml(String
            .format(
                "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
                    + "  <spec>%s</spec>\n"
                    + "  <configs>\n"
                    + "    <hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
                    + "      <directory>%s</directory>\n"
                    + "    </hudson.plugins.filesfoundtrigger.FilesFoundTriggerConfig>\n"
                    + "  </configs>\n"
                    + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>",
                SPEC, DIRECTORY));
    assertThat(ObjectUtils.toString(trigger), is(ObjectUtils.toString(create(
        SPEC, new FilesFoundTriggerConfig(DIRECTORY, "", "")))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlV1_1Format() {
    FilesFoundTrigger trigger = XStreamUtil.fromXml(String.format(
        "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
            + "  <spec>%s</spec>\n" + "  <directory>%s</directory>\n"
            + "  <files>%s</files>\n" + "  <ignoredFiles>%s</ignoredFiles>\n"
            + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>", SPEC,
        DIRECTORY, FILES, IGNORED_FILES));
    assertThat(ObjectUtils.toString(trigger), is(ObjectUtils.toString(create(
        SPEC, config()))));
    assertThat("tabs", getTabs(trigger), not(nullValue()));
  }

  /**
   */
  @Test
  public void readFromXmlV1_1FormatWithMissingFields() {
    FilesFoundTrigger trigger = XStreamUtil.fromXml(String.format(
        "<hudson.plugins.filesfoundtrigger.FilesFoundTrigger>\n"
            + "  <spec>%s</spec>\n"
            + "</hudson.plugins.filesfoundtrigger.FilesFoundTrigger>", SPEC));
    assertThat(ObjectUtils.toString(trigger), is(ObjectUtils.toString(create(
        SPEC, emptyConfig()))));
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
    BuildableItem buildableItem = mock(BuildableItem.class);
    assertThat(new FilesFoundTrigger.DescriptorImpl()
        .isApplicable(buildableItem), is(true));
  }

  /**
   */
  @Test
  public void getDisplayName() {
    assertThat(new FilesFoundTrigger.DescriptorImpl().getDisplayName(),
        not(nullValue()));
  }

  /**
   * Create a new {@link FilesFoundTrigger}.
   * 
   * @param spec
   *          crontab specification that defines how often to poll
   * @param configs
   *          the list of configured file patterns
   * @return a new trigger
   */
  private FilesFoundTrigger create(String spec,
      FilesFoundTriggerConfig... configs) {
    List<FilesFoundTriggerConfig> configsList = configs == null ? null : Arrays
        .asList(configs);
    try {
      return new FilesFoundTrigger(spec, configsList);
    } catch (ANTLRException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Create a new {@link FilesFoundTriggerConfig}.
   * 
   * @return a new configuration
   */
  private FilesFoundTriggerConfig config() {
    return new FilesFoundTriggerConfig(DIRECTORY, FILES, IGNORED_FILES);
  }

  /**
   * Create an empty {@link FilesFoundTriggerConfig}.
   * 
   * @return an empty configuration
   */
  private FilesFoundTriggerConfig emptyConfig() {
    return new FilesFoundTriggerConfig("", "", "");
  }

  /**
   * Create a new {@link FilesFoundTriggerConfig} that will find files.
   * 
   * @return a new configuration that will find files
   */
  private FilesFoundTriggerConfig foundConfig() {
    folder.newFile("test");
    return new FilesFoundTriggerConfig(folder.getRoot().getAbsolutePath(),
        "**", "");
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
}
