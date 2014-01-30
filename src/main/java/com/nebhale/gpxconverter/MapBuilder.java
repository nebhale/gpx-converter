/*
 * Copyright 2014 the original author or authors.
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

import java.net.URI;
import java.util.List;

interface MapBuilder {

    /**
     * Creates a URI that renders a map
     *
     * @param points  the points for the map
     * @param maptype the type of map
     * @param width   the width of the map
     * @param height  the height of the map
     * @return the URI for the map
     */
    URI build(List<Point> points, String maptype, int width, int height);
}
