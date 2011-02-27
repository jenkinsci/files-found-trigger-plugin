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

/**
 * Provides XStream utility methods for use by the unit tests.
 * 
 * @author Steven G. Brown
 */
class XStreamUtil {

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

  private XStreamUtil() {
  }
}
