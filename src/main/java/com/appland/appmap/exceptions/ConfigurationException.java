package com.appland.appmap.exceptions;

/**
 * Exception class to thrown when a configuration happens in the config file.
 */
public class ConfigurationException extends RuntimeException {

  /**
   * Set the exception message correctly.

   * @param path Config file path
   */
  public ConfigurationException(String path) {
    super("Appmap ConfigurationException on {"
        + "configFilepath='" + path + '\''
        + '}');
  }
}
