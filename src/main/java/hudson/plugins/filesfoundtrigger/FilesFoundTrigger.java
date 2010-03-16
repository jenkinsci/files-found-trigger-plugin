/*
 * The MIT License
 * 
 * Copyright (c) 2010 Steven G. Brown
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

import hudson.Extension;
import hudson.Util;
import hudson.model.BuildableItem;
import hudson.model.Item;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;

import java.io.File;

import org.apache.tools.ant.types.FileSet;
import org.kohsuke.stapler.DataBoundConstructor;

import antlr.ANTLRException;

/**
 * Build trigger that schedules a build when certain files are found. These
 * files are declared using <a
 * href="http://ant.apache.org/manual/CoreTypes/fileset.html">Ant-style file
 * patterns</a>.
 * 
 * @author Steven G. Brown
 */
public class FilesFoundTrigger extends Trigger<BuildableItem> {

  /**
   * The base directory to use when locating files.
   */
  private final String directory;

  /**
   * The pattern of files to locate under the base directory.
   */
  private final String files;

  /**
   * The pattern of files to ignore when searching under the base directory.
   */
  private final String ignoredFiles;

  /**
   * Create a new {@link FilesFoundTrigger}.
   * 
   * @param timerSpec
   *          crontab specification that defines how often to poll the directory
   * @param directory
   *          the base directory to use when locating files
   * @param files
   *          the pattern of files to locate under the base directory
   * @param ignoredFiles
   *          the pattern of files to ignore when searching under the base
   *          directory
   * @throws ANTLRException
   *           if unable to parse the crontab specification
   */
  @DataBoundConstructor
  public FilesFoundTrigger(String timerSpec, String directory, String files,
      String ignoredFiles) throws ANTLRException {
    super(timerSpec);
    this.directory = Util.fixEmptyAndTrim(directory);
    this.files = Util.fixEmptyAndTrim(files);
    this.ignoredFiles = Util.fixEmptyAndTrim(ignoredFiles);
  }

  /**
   * Get the base directory to use when locating files.
   * 
   * @return the base directory
   */
  public String getDirectory() {
    return directory;
  }

  /**
   * Get the pattern of files to locate under the base directory.
   * 
   * @return the files to find
   */
  public String getFiles() {
    return files;
  }

  /**
   * Get the pattern of files to ignore when searching under the base directory.
   * 
   * @return the files to ignore
   */
  public String getIgnoredFiles() {
    return ignoredFiles;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    if (filesFound()) {
      job.scheduleBuild(0, new FilesFoundTriggerCause(this));
    }
  }

  /**
   * Search for the files.
   * 
   * @return {@code true} if at least one file was found matching this trigger's
   *         configuration, {@code false} if none were found
   */
  private boolean filesFound() {
    if (directory == null || files == null) {
      return false;
    }
    FileSet fileSet = Util.createFileSet(new File(directory), files,
        ignoredFiles);
    return fileSet.size() > 0;
  }

  /**
   * Registers {@link FilesFoundTrigger} as a {@link Trigger} extension.
   */
  @Extension
  public static class DescriptorImpl extends TriggerDescriptor {

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
