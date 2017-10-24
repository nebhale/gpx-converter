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

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
final class TypeDiscriminatingRouteCodec implements RouteCodec {

    private final RouteCodec gpx = new GpxRouteCodec();

    private final RouteCodec tcx = new TcxRouteCodec();

    @Override
    public Route decodeDocument(Document document) {
        String tagName = document.getDocumentElement().getTagName();

        if ("gpx".equals(tagName)) {
            return this.gpx.decodeDocument(document);
        } else if ("TrainingCenterDatabase".equals(tagName)) {
            return this.tcx.decodeDocument(document);
        } else {
            throw new IllegalArgumentException(String.format("Unable to parse route with root tag '%s'", tagName));
        }
    }

    @Override
    public Document encodeDocument(Route route) {
        return this.gpx.encodeDocument(route);
    }

}
