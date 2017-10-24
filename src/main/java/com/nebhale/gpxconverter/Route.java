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

import java.util.Optional;

final class Route {

    private final Metadata metadata;

    private final Track track;

    private Route(Metadata metadata, Track track) {
        this.metadata = metadata;
        this.track = track;
    }

    static Builder builder() {
        return new Builder();
    }

    Metadata getMetadata() {
        return this.metadata;
    }

    Track getTrack() {
        return this.track;
    }

    static final class Builder {

        private Metadata metadata;

        private Track track;

        private Builder() {
        }

        Route build() {
            return new Route(this.metadata, this.track);
        }

        Builder from(Route route) {
            this.metadata = Optional.ofNullable(route.getMetadata())
                .map(metadata -> Metadata.builder()
                    .from(metadata)
                    .build())
                .orElse(null);

            this.track = Optional.ofNullable(route.getTrack())
                .map(track -> Track.builder()
                    .from(track)
                    .build())
                .orElse(null);

            return this;
        }

        Builder metadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        Builder track(Track track) {
            this.track = track;
            return this;
        }

    }

}
