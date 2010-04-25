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

import hudson.util.XStream2;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

/**
 * XStream converter that applies default values to fields marked with the
 * {@link XStreamDefault} annotation when unmarshalling an object. Only fields
 * that do not appear in the input XML data are assigned a default value.
 * Ordinarily, these fields default to {@code null}.
 * <p>
 * To use, create a nested {@code ConverterImpl} subclass that extends this
 * class. Example:
 * 
 * <pre>
 * public static class ConverterImpl extends DefaultingConverter {
 *   public ConverterImpl(XStream2 xstream) {
 *     super(xstream);
 *   }
 * }
 * </pre>
 * 
 * @author Steven G. Brown
 */
public class DefaultingConverter extends XStream2.PassthruConverter<Object> {

  /**
   * Reference to the XStream library.
   */
  private XStream2 xstream;

  /**
   * Create a new {@link DefaultingConverter}.
   * 
   * @param xstream
   *          reference to the XStream library
   */
  public DefaultingConverter(XStream2 xstream) {
    super(xstream);
    this.xstream = xstream;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void callback(final Object obj, UnmarshallingContext context) {
    final ReflectionProvider reflectionProvider = xstream
        .getReflectionProvider();
    reflectionProvider.visitSerializableFields(obj,
        new ReflectionProvider.Visitor() {

          /**
           * {@inheritDoc}
           */
          @SuppressWarnings("unchecked")
          public void visit(String name, Class type, Class definedIn,
              Object value) {
            visitImpl(name, type, definedIn, value);
          }

          private void visitImpl(String name, Class<?> type,
              Class<?> definedIn, Object value) {
            if (value == null) {
              Field field = reflectionProvider.getField(definedIn, name);
              XStreamDefault defaultAnnotation = field
                  .getAnnotation(XStreamDefault.class);
              if (defaultAnnotation != null) {
                DefaultProvider<?> defaultProvider = (DefaultProvider<?>) reflectionProvider
                    .newInstance(defaultAnnotation.value());
                if (!defaultProvider.getSupportedType().isAssignableFrom(type)) {
                  throw new ConversionException(defaultProvider.getClass()
                      .getName()
                      + " does not support " + type.getName());
                }
                Object defaultValue = defaultProvider.getDefault(name, type,
                    definedIn);
                reflectionProvider.writeField(obj, name, defaultValue,
                    definedIn);
              }
            }
          }
        });
  }
}
