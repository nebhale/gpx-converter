/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nebhale.gpxconverter;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import reactor.core.Exceptions;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static javax.xml.transform.OutputKeys.INDENT;

interface RouteCodec {

    /**
     * Decode an XML {@link Document} into a {@link Route}
     *
     * @param document the document to decode
     * @return the route representation of the XML document
     */
    Route decodeDocument(Document document);

    /**
     * Decode an XML {@link String} into a {@link Route}
     *
     * @param s the string to decode
     * @return the route representation of the XML document
     */
    default Route decodeString(String s) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(s)));

            return decodeDocument(document);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw Exceptions.propagate(e);
        }
    }

    /**
     * Encode a {@link Route} into an XML {@link Document}
     *
     * @param route the route to encode
     * @return the XML {@link Document} representation of the route
     */
    Document encodeDocument(Route route);

    /**
     * Encode a {@link Route} into an XML {@link String}
     *
     * @param route the route to encode
     * @return the XML string representation of the route
     */
    default String encodeString(Route route) {
        try (StringWriter out = new StringWriter()) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            DOMSource sources = new DOMSource(encodeDocument(route));
            StreamResult result = new StreamResult(out);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(sources, result);
            return out.toString();
        } catch (IOException | TransformerException e) {
            throw Exceptions.propagate(e);
        }
    }

}
