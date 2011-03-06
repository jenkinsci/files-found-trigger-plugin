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
  static final String DIRECTORY = "C:/";

  /**
   */
  static final String ALTERNATE_DIRECTORY = "D:/";

  /**
   */
  static final String FILES = "**";

  /**
   */
  static final String ALTERNATE_FILES = "files";

  /**
   */
  static final String IGNORED_FILES = "ignored";

  /**
   */
  static final String ALTERNATE_IGNORED_FILES = "alt_ignored";

  /**
   * Create a new cause with the given values.
   * 
   * @param directory
   *          the base directory
   * @param files
   *          the pattern of files
   * @param ignoredFiles
   *          the pattern of ignored files
   * @return a new {@link FilesFoundTriggerCause}
   */
  static FilesFoundTriggerCause cause(String directory, String files,
      String ignoredFiles) {
    return new FilesFoundTriggerCause(config(directory, files, ignoredFiles));
  }

  /**
   * Create a new configuration with the given values.
   * 
   * @param directory
   *          the base directory
   * @param files
   *          the pattern of files
   * @param ignoredFiles
   *          the pattern of ignored files
   * @return a new {@link FilesFoundTriggerConfig}
   */
  static FilesFoundTriggerConfig config(String directory, String files,
      String ignoredFiles) {
    return new FilesFoundTriggerConfig(directory, files, ignoredFiles);
  }

  /**
   * Create a new configuration.
   * 
   * @return a new {@link FilesFoundTriggerConfig}
   */
  static FilesFoundTriggerConfig config() {
    return new FilesFoundTriggerConfig(DIRECTORY, FILES, IGNORED_FILES);
  }

  /**
   * Create an empty configuration.
   * 
   * @return a new {@link FilesFoundTriggerConfig}
   */
  static FilesFoundTriggerConfig emptyConfig() {
    return new FilesFoundTriggerConfig("", "", "");
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
