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
import hudson.util.FormValidation;
import hudson.util.RobustReflectionConverter;

import java.io.File;

import org.apache.tools.ant.types.FileSet;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import antlr.ANTLRException;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;

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
    this.directory = directory.trim();
    this.files = files.trim();
    this.ignoredFiles = ignoredFiles.trim();
  }

  /**
   * Constructor intended to be called by XStream only. Sets the default field
   * values, which will then be overridden if these fields exist in the build
   * configuration file.
   * 
   * @throws ANTLRException
   */
  @SuppressWarnings("unused")
  // called reflectively by XStream
  private FilesFoundTrigger() throws ANTLRException {
    this.directory = "";
    this.files = "";
    this.ignoredFiles = "";
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
  boolean filesFound() {
    if (directoryFound() && filesSpecified()) {
      FileSet fileSet = Util.createFileSet(new File(directory), files,
          ignoredFiles);
      fileSet.setDefaultexcludes(false);
      return fileSet.size() > 0;
    }
    return false;
  }

  /**
   * Determine whether the directory has been specified.
   * 
   * @return {@code true} if the directory has been specified, {@code false}
   *         otherwise
   */
  private boolean directorySpecified() {
    return !directory.isEmpty();
  }

  /**
   * Determine whether the file pattern has been specified.
   * 
   * @return {@code true} if the file pattern has been specified, {@code false}
   *         otherwise
   */
  private boolean filesSpecified() {
    return !files.isEmpty();
  }

  /**
   * Determine whether the search directory exists.
   * 
   * @return {@code true} if the search directory exists, {@code false} if the
   *         search directory does not exist or has not been configured
   */
  private boolean directoryFound() {
    return directorySpecified() && new File(directory).isDirectory();
  }

  /**
   * {@link Converter} implementation for XStream. This converter uses the
   * {@link PureJavaReflectionProvider}, which ensures that the
   * {@link FilesFoundTrigger#FilesFoundTrigger()} constructor is called.
   */
  public static class ConverterImpl extends RobustReflectionConverter {

    /**
     * Class constructor.
     * 
     * @param mapper
     *          the mapper
     */
    public ConverterImpl(Mapper mapper) {
      super(mapper, new PureJavaReflectionProvider());
    }
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

    /**
     * Test the entered trigger configuration.
     * 
     * @param directory
     *          the base directory to use when locating files
     * @param files
     *          the pattern of files to locate under the base directory
     * @param ignoredFiles
     *          the pattern of files to ignore when searching under the base
     *          directory
     * @return the result
     */
    public FormValidation doTestConfiguration(
        @QueryParameter("directory") final String directory,
        @QueryParameter("files") final String files,
        @QueryParameter("ignoredFiles") final String ignoredFiles) {
      FilesFoundTrigger trigger;
      try {
        trigger = new FilesFoundTrigger("", directory, files, ignoredFiles);
      } catch (ANTLRException ex) {
        // ANTLRException is not expected to be thrown for an empty
        // string.
        throw new RuntimeException(ex);
      }
      if (!trigger.directorySpecified()) {
        return FormValidation.error(Messages.DirectoryNotSpecified());
      }
      if (!trigger.filesSpecified()) {
        return FormValidation.error(Messages.FilesNotSpecified());
      }
      if (!trigger.directoryFound()) {
        return FormValidation.warning(Messages.DirectoryNotFound());
      }
      if (!trigger.filesFound()) {
        return FormValidation.ok(Messages.FilesNotFound());
      }
      return FormValidation.ok(Messages.FilesFound());
    }
  }
}
