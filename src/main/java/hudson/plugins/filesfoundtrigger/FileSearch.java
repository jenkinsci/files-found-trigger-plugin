/*
 * The MIT License
 * 
 * Copyright (c) 2015 Steven G. Brown
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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.tools.ant.types.FileSet;

import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Util;
import hudson.model.Node;
import hudson.remoting.VirtualChannel;
import hudson.util.FormValidation;
import jenkins.MasterToSlaveFileCallable;
import jenkins.model.Jenkins;

/**
 * Class that is responsible for performing the file search.
 * 
 * @author Steven G. Brown
 */
class FileSearch {

  /**
   * The search result.
   */
  static class Result {
    final FormValidation formValidation;
    final List<String> files;

    private Result(FormValidation formValidation) {
      this.formValidation = formValidation;
      this.files = Collections.emptyList();
    }

    private Result(FormValidation formValidation, String[] files) {
      this.formValidation = formValidation;
      this.files = ImmutableList.copyOf(files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
          .append("formValidation", formValidation).append("files", files).toString();
    }
  }

  /**
   * {@link FileCallable} which scans for matching files on a slave.
   */
  private static class FindFilesOnSlaveFileCallable extends MasterToSlaveFileCallable<String[]> {

    private static final long serialVersionUID = 1L;

    private final String files;

    private final String ignoredFiles;

    FindFilesOnSlaveFileCallable(FilesFoundTriggerConfig config) {
      this.files = config.getFiles();
      this.ignoredFiles = config.getIgnoredFiles();
    }

    @Override
    public String[] invoke(File f, VirtualChannel channel)
        throws IOException, InterruptedException {
      return scan(f, files, ignoredFiles);
    }
  }

  /**
   * Perform a file search with the given configuration.
   * 
   * @param config
   *          the configuration
   * @return the search result
   * @throws IOException
   * @throws InterruptedException
   */
  static Result perform(FilesFoundTriggerConfig config) throws IOException, InterruptedException {

    // Check for an incomplete configuration.
    if (config.getDirectory().isEmpty()) {
      return new Result(FormValidation.error(Messages.DirectoryNotSpecified()));
    }
    if (config.getFiles().isEmpty()) {
      return new Result(FormValidation.error(Messages.FilesNotSpecified()));
    }

    // Search for the files on the master or on a slave.
    String nodeName = config.getNode();
    String[] found;
    if (nodeName == null) {
      // master
      found = scan(new File(config.getDirectory()), config.getFiles(), config.getIgnoredFiles());
    } else {
      // slave
      Node slaveNode = null;
      Jenkins jenkins = Jenkins.getInstance();
      if (jenkins != null) {
        slaveNode = jenkins.getNode(nodeName);
      }
      if (slaveNode == null) {
        return new Result(FormValidation.error(Messages.NodeNotFound(nodeName)));
      }
      VirtualChannel channel = slaveNode.getChannel();
      if (channel == null) {
        return new Result(FormValidation.error(Messages.NodeOffline(nodeName)));
      }
      FilePath filePath = new FilePath(channel, config.getDirectory());
      found = filePath.act(new FindFilesOnSlaveFileCallable(config));
    }

    // Check for missing directory.
    if (found == null) {
      String userName = System.getProperty("user.name");
      return new Result(FormValidation.warning(Messages.DirectoryNotFound(userName)));
    }

    // Search was successful.
    FormValidation formValidation;
    if (found.length == 0) {
      formValidation = FormValidation.ok(Messages.NoFilesFound());
    } else if (found.length == 1) {
      formValidation = FormValidation.ok(Messages.SingleFileFound(found[0]));
    } else {
      formValidation = FormValidation
          .ok(Messages.MultipleFilesFound(Integer.valueOf(found.length)));
    }
    return new Result(formValidation, found);
  }

  @CheckForNull
  @SuppressFBWarnings(value = "PZLA_PREFER_ZERO_LENGTH_ARRAYS")
  private static String[] scan(File f, String files, String ignoredFiles) {
    if (!f.isDirectory()) {
      // Return null to indicate that the directory does not exist.
      return null;
    }
    FileSet fileSet = Util.createFileSet(f, files, ignoredFiles);
    fileSet.setDefaultexcludes(false);
    return fileSet.getDirectoryScanner().getIncludedFiles();
  }

  private FileSearch() {
  }
}
