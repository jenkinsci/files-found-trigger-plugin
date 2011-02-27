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
import hudson.Util;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import hudson.util.RobustReflectionConverter;

import java.io.File;

import net.sf.json.JSONObject;

import org.apache.tools.ant.types.FileSet;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Pattern of files to locate within a single directory.
 * 
 * @author Steven G. Brown
 */
public final class FilesFoundTriggerConfig implements
    Describable<FilesFoundTriggerConfig> {

  /**
   * Get the descriptor of this class.
   * 
   * @return the {@link FilesFoundTriggerConfig} descriptor
   */
  public static DescriptorImpl getClassDescriptor() {
    return (DescriptorImpl) Hudson.getInstance().getDescriptorOrDie(
        FilesFoundTriggerConfig.class);
  }

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
   * Create a new {@link FilesFoundTriggerConfig}.
   * 
   * @param directory
   *          the base directory to use when locating files
   * @param files
   *          the pattern of files to locate under the base directory
   * @param ignoredFiles
   *          the pattern of files to ignore when searching under the base
   *          directory
   */
  @DataBoundConstructor
  public FilesFoundTriggerConfig(String directory, String files,
      String ignoredFiles) {
    this.directory = fixNull(directory).trim();
    this.files = fixNull(files).trim();
    this.ignoredFiles = fixNull(ignoredFiles).trim();
  }

  /**
   * Constructor intended to be called by XStream only. Sets the default field
   * values, which will then be overridden if these fields exist in the build
   * configuration file.
   */
  @SuppressWarnings("unused")
  // called reflectively by XStream
  private FilesFoundTriggerConfig() {
    this.directory = "";
    this.files = "";
    this.ignoredFiles = "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Descriptor<FilesFoundTriggerConfig> getDescriptor() {
    return getClassDescriptor();
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
  public String toString() {
    JSONObject json = new JSONObject();
    json.element("directory", directory);
    json.element("files", files);
    json.element("ignoredFiles", ignoredFiles);
    return json.toString().replace('"', '\'');
  }

  /**
   * Search for the files.
   * 
   * @return whether at least one file was found
   */
  boolean filesFound() {
    if (directoryFound() && filesSpecified()) {
      FileSet fileSet = Util.createFileSet(new File(getDirectory()),
          getFiles(), getIgnoredFiles());
      fileSet.setDefaultexcludes(false);
      return fileSet.size() > 0;
    }
    return false;
  }

  /**
   * Determine whether the directory has been specified.
   * 
   * @return whether the directory has been specified
   */
  private boolean directorySpecified() {
    return directory.length() != 0;
  }

  /**
   * Determine whether the search directory exists.
   * 
   * @return {@code true} if the search directory exists, {@code false} if the
   *         search directory does not exist or has not been configured
   */
  private boolean directoryFound() {
    return directorySpecified() && new File(getDirectory()).isDirectory();
  }

  /**
   * Determine whether the file pattern has been specified.
   * 
   * @return whether the file pattern has been specified
   */
  private boolean filesSpecified() {
    return files.length() != 0;
  }

  /**
   * {@link Converter} implementation for XStream. This converter uses the
   * {@link PureJavaReflectionProvider}, which ensures that the default
   * constructor is called.
   */
  public static final class ConverterImpl extends RobustReflectionConverter {

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
   * Descriptor for the {@link FilesFoundTriggerConfig} class.
   */
  @Extension
  public static final class DescriptorImpl extends
      Descriptor<FilesFoundTriggerConfig> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
      // Not used.
      return "";
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
      FilesFoundTriggerConfig config = new FilesFoundTriggerConfig(directory,
          files, ignoredFiles);
      if (!config.directorySpecified()) {
        return FormValidation.error(Messages.DirectoryNotSpecified());
      }
      if (!config.filesSpecified()) {
        return FormValidation.error(Messages.FilesNotSpecified());
      }
      if (!config.directoryFound()) {
        return FormValidation.warning(Messages.DirectoryNotFound());
      }
      if (!config.filesFound()) {
        return FormValidation.ok(Messages.FilesNotFound());
      }
      return FormValidation.ok(Messages.FilesFound());
    }
  }
}
