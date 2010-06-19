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

import hudson.util.XStream2;

import org.dom4j.DocumentHelper;

import com.thoughtworks.xstream.io.xml.Dom4JReader;

/**
 * Provides utility methods for use by the unit tests.
 * 
 * @author Steven G. Brown
 */
class TestAssistant {

  /**
   * Construct an object from the given XML element using {@link XStream2}.
   * 
   * @param <T>
   *          the type of object to construct
   * @param xml
   *          the XML element as a string
   * @return the newly constructed object
   * @throws Exception
   */
  static <T> T unmarshal(String xml) throws Exception {
    XStream2 xStream2 = new XStream2();
    Dom4JReader reader = null;
    try {
      reader = new Dom4JReader(DocumentHelper.parseText(xml));
      return cast(xStream2.unmarshal(reader));
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  /**
   * Cast the given object to type {@code T}.
   * 
   * @param <T>
   *          the target type
   * @param object
   *          the object to cast
   * @return the object as type {@code T}
   */
  @SuppressWarnings("unchecked")
  private static <T> T cast(Object object) {
    return (T) object;
  }
}
