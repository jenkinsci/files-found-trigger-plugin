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

import hudson.model.Cause;
import hudson.plugins.filesfoundtrigger.xstream.DefaultProvider;
import hudson.plugins.filesfoundtrigger.xstream.DefaultingConverter;
import hudson.plugins.filesfoundtrigger.xstream.XStreamDefault;
import hudson.util.XStream2;

import java.util.Arrays;

import org.apache.commons.lang.ObjectUtils;
import org.kohsuke.stapler.export.Exported;

import com.thoughtworks.xstream.converters.Converter;

/**
 * The cause of a build that was started by a {@link FilesFoundTrigger}.
 */
public class FilesFoundTriggerCause extends Cause {

  /**
   * The base directory that was used when locating files.
   */
  @XStreamDefault(DefaultProvider.EmptyString.class)
  private final String directory;

  /**
   * The pattern of files that were located under the base directory.
   */
  @XStreamDefault(DefaultProvider.EmptyString.class)
  private final String files;

  /**
   * The pattern of files that were ignored when searching under the base
   * directory.
   */
  @XStreamDefault(DefaultProvider.EmptyString.class)
  private final String ignoredFiles;

  /**
   * Create a new {@link FilesFoundTriggerCause}.
   * 
   * @param trigger
   *          the trigger that has scheduled a build
   */
  FilesFoundTriggerCause(FilesFoundTrigger trigger) {
    this.directory = trigger.getDirectory();
    this.files = trigger.getFiles();
    this.ignoredFiles = trigger.getIgnoredFiles();
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
   * {@inheritDoc}
   */
  @Override
  public String getShortDescription() {
    return ignoredFiles.isEmpty() ? Messages.Cause(directory, files) : Messages
        .CauseWithIgnoredFiles(directory, files, ignoredFiles);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Arrays.hashCode(new String[] { directory, files, ignoredFiles });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof FilesFoundTriggerCause) {
      FilesFoundTriggerCause other = (FilesFoundTriggerCause) obj;
      return ObjectUtils.equals(directory, other.directory)
          && ObjectUtils.equals(files, other.files)
          && ObjectUtils.equals(ignoredFiles, other.ignoredFiles);
    }
    return false;
  }

  /**
   * {@link Converter} implementation for XStream.
   */
  public static class ConverterImpl extends DefaultingConverter {

    /**
     * Class constructor.
     * 
     * @param xstream
     *          reference to the XStream library
     */
    public ConverterImpl(XStream2 xstream) {
      super(xstream);
    }
  }
}
