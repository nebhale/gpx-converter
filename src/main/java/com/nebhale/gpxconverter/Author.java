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

final class Author {

    private final String link;

    private final String name;

    private Author(String link, String name) {
        this.link = link;
        this.name = name;
    }

    static Builder builder() {
        return new Builder();
    }

    String getLink() {
        return this.link;
    }

    String getName() {
        return this.name;
    }

    static final class Builder {

        private String link;

        private String name;

        private Builder() {
        }

        Author build() {
            return new Author(this.link, this.name);
        }

        Builder from(Author author) {
            this.link = author.getLink();
            this.name = author.getName();
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
