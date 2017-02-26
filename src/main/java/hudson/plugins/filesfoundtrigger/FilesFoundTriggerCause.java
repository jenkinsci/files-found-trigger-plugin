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

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.export.Exported;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;

import hudson.model.Cause;
import hudson.util.RobustReflectionConverter;

/**
 * The cause of a build that was started by a {@link FilesFoundTrigger}.
 * 
 * @author Steven G. Brown
 */
public final class FilesFoundTriggerCause extends Cause {

  /**
   * The slave node on which to look for files, or {@code null} if the master
   * will be used.
   */
  private final String node;

  /**
   * The base directory that was used when locating files.
   */
  private final String directory;

  /**
   * The pattern of files that were located under the base directory.
   */
  private final String files;

  /**
   * The pattern of files that were ignored when searching under the base
   * directory.
   */
  private final String ignoredFiles;

  /**
   * the minimum number of found files to trigger the build.
   */
  private final String triggerNumber;
  
  /**
   * Create a new {@link FilesFoundTriggerCause}.
   * 
   * @param config
   *          the configuration that has caused a build to be scheduled
   */
  FilesFoundTriggerCause(FilesFoundTriggerConfig config) {
    this.node = config.getNode();
    this.directory = config.getDirectory();
    this.files = config.getFiles();
    this.ignoredFiles = config.getIgnoredFiles();
	this.triggerNumber = config.getTriggerNumber();
  }

  /**
   * Constructor intended to be called by XStream only. Sets the default field
   * values, which will then be overridden if these fields exist in the build
   * configuration file.
   */
  @SuppressWarnings("unused")
  // called reflectively by XStream
  private FilesFoundTriggerCause() {
    this.node = null;
    this.directory = "";
    this.files = "";
    this.ignoredFiles = "";
	this.triggerNumber = "";
  }

  /**
   * Get the node on which the files were found.
   * 
   * @return the node
   */
  @Exported(visibility = 3)
  public String getNode() {
    return node == null ? "master" : node;
  }

  /**
   * Get the base directory that was used when locating files.
   * 
   * @return the base directory
   */
  @Exported(visibility = 3)
  public String getDirectory() {
    return directory;
  }

  /**
   * Get the pattern of files that were located under the base directory.
   * 
   * @return the located files
   */
  @Exported(visibility = 3)
  public String getFiles() {
    return files;
  }

  /**
   * Get the pattern of files that were ignored when searching under the base
   * directory.
   * 
   * @return the ignored files
   */
  @Exported(visibility = 3)
  public String getIgnoredFiles() {
    return ignoredFiles;
  }
  
  /**
   * Get the minimum number of found files to trigger the build.
   * 
   * @return the minimum number of found files to trigger the build
   */
  @Exported(visibility = 3)
  public String getTriggerNumber() {
    return triggerNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getShortDescription() {
    return ignoredFiles.length() == 0 ? Messages.Cause(getNode(), directory,
        files) : Messages.CauseWithIgnoredFiles(getNode(), directory, files,
        ignoredFiles);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(node, directory, files, ignoredFiles, triggerNumber);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FilesFoundTriggerCause) {
      FilesFoundTriggerCause other = (FilesFoundTriggerCause) obj;
      return Objects.equals(node, other.node)
          && directory.equals(other.directory) && files.equals(other.files)
          && ignoredFiles.equals(other.ignoredFiles) && triggerNumber.equals(other.triggerNumber);
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("node", getNode())
        .append("directory", directory).append("files", files).append("ignoredFiles", ignoredFiles)
        .append("triggerNumber", triggerNumber).toString();
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
}
