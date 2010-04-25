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
package hudson.plugins.filesfoundtrigger.xstream;

/**
 * Provides default values for fields marked with the {@link XStreamDefault}
 * annotation.
 * 
 * @param <T>
 *          the type of values supported by this provider
 * 
 * @author Steven G. Brown
 */
public interface DefaultProvider<T> {

  /**
   * Get a default value.
   * 
   * @param name
   *          the field name
   * @param type
   *          the field type
   * @param definedIn
   *          the class in which the field is defined
   * @return the default value
   */
  T getDefault(String name, Class<?> type, Class<?> definedIn);

  /**
   * Get the type of values supported by this provider.
   * 
   * @return the supported type
   */
  Class<T> getSupportedType();

  /**
   * Implementation of {@link DefaultProvider} that allows fields to default to
   * an empty string.
   */
  public static class EmptyString implements DefaultProvider<String> {

    /**
     * {@inheritDoc}
     */
    public String getDefault(String name, Class<?> type, Class<?> definedIn) {
      return "";
    }

    /**
     * {@inheritDoc}
     */
    public Class<String> getSupportedType() {
      return String.class;
    }
  }
}
