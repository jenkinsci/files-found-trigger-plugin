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

import static hudson.Util.fixNull;
import hudson.Extension;
import hudson.model.BuildableItem;
import hudson.model.Item;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;

import antlr.ANTLRException;

/**
 * Build trigger that schedules a build when certain files are found. These
 * files are declared using <a
 * href="http://ant.apache.org/manual/dirtasks.html">Ant-style file
 * patterns</a>.
 * 
 * @author Steven G. Brown
 */
public final class FilesFoundTrigger extends Trigger<BuildableItem> {

  /**
   * The list of configured file patterns.
   */
  private List<FilesFoundTriggerConfig> configs;

  /**
   * The base directory to use when locating files.
   */
  @Deprecated
  private String directory;

  /**
   * The pattern of files to locate under the base directory.
   */
  @Deprecated
  private String files;

  /**
   * The pattern of files to ignore when searching under the base directory.
   */
  @Deprecated
  private String ignoredFiles;

  /**
   * Create a new {@link FilesFoundTrigger}.
   * 
   * @param spec
   *          crontab specification that defines how often to poll the directory
   * @param configs
   *          the list of configured file patterns
   * @throws ANTLRException
   *           if unable to parse the crontab specification
   */
  @DataBoundConstructor
  public FilesFoundTrigger(String spec, List<FilesFoundTriggerConfig> configs)
      throws ANTLRException {
    super(spec);
    this.configs = fixNull(configs);
  }

  /**
   * Get the list of configured file patterns.
   * 
   * @return a list of {@link FilesFoundTriggerConfig}
   */
  public List<FilesFoundTriggerConfig> getConfigs() {
    return configs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    for (FilesFoundTriggerConfig config : configs) {
      if (config.filesFound()) {
        job.scheduleBuild(0, new FilesFoundTriggerCause(config));
        return;
      }
    }
  }

  /**
   */
  @Override
  public String toString() {
    JSONObject json = new JSONObject();
    json.element("spec", spec);
    json.element("tabs", tabs);
    List<String> configStrings = new ArrayList<String>();
    for (FilesFoundTriggerConfig config : configs) {
      configStrings.add(config.toString());
    }
    json.element("configs", configStrings);
    return json.toString().replace('"', '\'');
  }

  /**
   * Read resolve method that upgrades data stored in previous formats.
   * 
   * @return the replacement object
   * @throws ObjectStreamException
   */
  @Override
  protected Object readResolve() throws ObjectStreamException {
    FilesFoundTrigger trigger = (FilesFoundTrigger) super.readResolve();
    if (trigger.configs == null) {
      // Upgrade trigger created prior to v1.2.
      trigger.configs = Collections.singletonList(new FilesFoundTriggerConfig(
          directory, files, ignoredFiles));
    }
    return trigger;
  }

  /**
   * Registers {@link FilesFoundTrigger} as a {@link Trigger} extension.
   */
  @Extension
  public static final class DescriptorImpl extends TriggerDescriptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Item item) {
      return item instanceof BuildableItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
      return Messages.DisplayName();
    }
  }
}
