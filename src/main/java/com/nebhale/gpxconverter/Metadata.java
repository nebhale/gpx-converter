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

final class Metadata {

    private final Author author;

    private final String link;

    private final String name;

    private Metadata(Author author, String link, String name) {
        this.author = author;
        this.link = link;
        this.name = name;
    }

    static Builder builder() {
        return new Builder();
    }

    Author getAuthor() {
        return this.author;
    }

    String getLink() {
        return this.link;
    }

    String getName() {
        return this.name;
    }

    static final class Builder {

        private Author author;

        private String link;

        private String name;

        private Builder() {
        }

        Builder author(Author author) {
            this.author = author;
            return this;
        }

        Metadata build() {
            return new Metadata(this.author, this.link, this.name);
        }

        Builder from(Metadata metadata) {
            this.author = Optional.ofNullable(metadata.getAuthor())
                .map(author -> Author.builder()
                    .from(author)
                    .build())
                .orElse(null);

            this.link = metadata.getLink();
            this.name = metadata.getName();

            return this;
        }

        Builder link(String link) {
            this.link = link;
            return this;
        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

    }
}
