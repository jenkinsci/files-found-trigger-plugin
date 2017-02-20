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

import hudson.util.XStream2;

import java.util.Arrays;
import java.util.List;

import antlr.ANTLRException;

/**
 * Provides values and utility methods for use by the unit tests.
 * 
 * @author Steven G. Brown
 */
class Support {

  /**
   */
  static final String SPEC = "* * * * *";

  /**
   */
  static final String MASTER_NODE = "master";

  /**
   */
  static final String DIRECTORY = "C:/";

  /**
   */
  static final String ALTERNATE_DIRECTORY = "D:/";

  /**
   */
  static final String FILES = "**";

  /**
   */
  static final String SLAVE_NODE = "slave";

  /**
   */
  static final String ALTERNATE_FILES = "files";

  /**
   */
  static final String IGNORED_FILES = "ignored";
  
  /**
   */
  static final String TRIGGER_NUMBER = "1";

  /**
   */
  static final String ALTERNATE_IGNORED_FILES = "alt_ignored";

  /**
   * Create a new cause with the given values.
   * 
   * @param node
   *          the node
   * @param directory
   *          the base directory
   * @param files
   *          the pattern of files
   * @param ignoredFiles
   *          the pattern of ignored files
   * @param triggerNumber
   *          the minimum number of files to trigger the build
   * @return a new {@link FilesFoundTriggerCause}
   */
  static FilesFoundTriggerCause cause(String node, String directory,
      String files, String ignoredFiles, String triggerNumber) {
    return new FilesFoundTriggerCause(config(node, directory, files,
        ignoredFiles, triggerNumber));
  }

  /**
   * Create a new configuration with the given values.
   * 
   * @param node
   *          the node
   * @param directory
   *          the base directory
   * @param files
   *          the pattern of files
   * @param ignoredFiles
   *          the pattern of ignored files
   * @param triggerNumber
   *           the minimum number of found files to trigger the build
   * @return a new {@link FilesFoundTriggerConfig}
   */
  static FilesFoundTriggerConfig config(String node, String directory,
      String files, String ignoredFiles, String triggerNumber) {
    return new FilesFoundTriggerConfig(node, directory, files, ignoredFiles, triggerNumber);
  }

  /**
   * Create a new configuration for finding files on the master.
   * 
   * @return a new {@link FilesFoundTriggerConfig}
   */
  static FilesFoundTriggerConfig masterConfig() {
    return new FilesFoundTriggerConfig(MASTER_NODE, DIRECTORY, FILES,
        IGNORED_FILES, TRIGGER_NUMBER);
  }

  /**
   * Create a new configuration for finding files on a slave.
   * 
   * @return a new {@link FilesFoundTriggerConfig}
   */
  static FilesFoundTriggerConfig slaveConfig() {
    return new FilesFoundTriggerConfig(SLAVE_NODE, DIRECTORY, FILES,
        IGNORED_FILES, TRIGGER_NUMBER);
  }

  /**
   * Create an empty configuration.
   * 
   * @return a new {@link FilesFoundTriggerConfig}
   */
  static FilesFoundTriggerConfig emptyConfig() {
    return new FilesFoundTriggerConfig("", "", "", "", "1");
  }

  /**
   * Create a new trigger.
   * 
   * @param spec
   *          crontab specification that defines how often to poll
   * @param configs
   *          the list of configured file patterns
   * @return a new {@link FilesFoundTrigger}
   */
  static FilesFoundTrigger trigger(String spec,
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
   * Convert the given object to an XML string using XStream.
   * 
   * @param obj
   *          the object to convert
   * @return the XML string
   */
  static String toXml(Object obj) {
    XStream2 xStream2 = new XStream2();
    return xStream2.toXML(obj);
  }

  /**
   * Construct an object from the given XML element using XStream.
   * 
   * @param <T>
   *          the type of object to construct
   * @param xml
   *          the XML element as a string
   * @return the newly constructed object
   */
  static <T> T fromXml(String xml) {
    XStream2 xStream2 = new XStream2();
    @SuppressWarnings("unchecked")
    T obj = (T) xStream2.fromXML(xml);
    return obj;
  }

  private Support() {
  }
}
