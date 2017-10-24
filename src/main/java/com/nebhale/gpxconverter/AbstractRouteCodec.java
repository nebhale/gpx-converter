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

import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

abstract class AbstractRouteCodec implements RouteCodec {

    final <T> void addElementIfPresent(Node parent, String tagName, T item, BiConsumer<Element, T> consumer) {
        Optional.ofNullable(item)
            .ifPresent(i -> {
                Element element = getDocument(parent).createElement(tagName);
                parent.appendChild(element);

                consumer.accept(element, i);
            });
    }

    final void ifAttributePresentDouble(Node root, String attributeName, Consumer<Double> consumer) {
        getAttribute((Element) root, attributeName)
            .map(Double::parseDouble)
            .ifPresent(consumer);
    }

    final void ifAttributePresentString(Node root, String attributeName, Consumer<String> consumer) {
        getAttribute((Element) root, attributeName)
            .ifPresent(consumer);
    }

    final void ifNodePresent(Node root, String tagName, Consumer<Node> consumer) {
        getElements(root, tagName)
            .findFirst()
            .ifPresent(consumer);
    }

    final void ifValuePresentDouble(Node root, String tagName, Consumer<Double> consumer) {
        getElements(root, tagName)
            .findFirst()
            .map(Node::getTextContent)
            .map(Double::parseDouble)
            .ifPresent(consumer);
    }

    final void ifValuePresentString(Node root, String tagName, Consumer<String> consumer) {
        getElements(root, tagName)
            .findFirst()
            .map(Node::getTextContent)
            .ifPresent(consumer);
    }

    final void withNodes(Node root, String tagName, Consumer<Node> consumer) {
        getElements(root, tagName)
            .forEach(consumer);
    }

    private Optional<String> getAttribute(Element root, String attributeName) {
        return Optional.ofNullable(root.getAttribute(attributeName))
            .filter(StringUtils::hasText);
    }

    private Document getDocument(Node parent) {
        return parent instanceof Document ? (Document) parent : parent.getOwnerDocument();
    }

    private Stream<Node> getElements(Node root, String tagName) {
        return stream(root.getChildNodes())
            .filter(node -> tagName.equals(node.getNodeName()));
    }

    private Stream<Node> stream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
            .mapToObj(nodeList::item);
    }

}
