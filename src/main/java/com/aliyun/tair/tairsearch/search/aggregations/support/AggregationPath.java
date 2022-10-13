/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package com.aliyun.tair.tairsearch.search.aggregations.support;

import java.util.*;

/**
 * A path that can be used to sort/order buckets (in some multi-bucket aggregations, e.g. terms &amp; histogram) based on
 * sub-aggregations. The path may point to either a single-bucket aggregation or a metrics aggregation. If the path
 * points to a single-bucket aggregation, the sort will be applied based on the {@code doc_count} of the bucket. If this
 * path points to a metrics aggregation, if it's a single-value metrics (eg. avg, max, min, etc..) the sort will be
 * applied on that single value. If it points to a multi-value metrics, the path should point out what metric should be
 * the sort-by value.
 * <p>
 * The path has the following form:
 * {@code <aggregation_name>['>'<aggregation_name>*]['.'<metric_name>]}
 * <p>
 * Examples:
 *
 * <ul>
 *     <li>
 *         {@code agg1>agg2>agg3} - where agg1, agg2 and agg3 are all single-bucket aggs (eg filter, nested, missing, etc..). In
 *                                  this case, the order will be based on the number of documents under {@code agg3}.
 *     </li>
 *     <li>
 *         {@code agg1>agg2>agg3} - where agg1 and agg2 are both single-bucket aggs and agg3 is a single-value metrics agg (eg avg, max,
 *                                  min, etc..). In this case, the order will be based on the value of {@code agg3}.
 *     </li>
 *     <li>
 *         {@code agg1>agg2>agg3.avg} - where agg1 and agg2 are both single-bucket aggs and agg3 is a multi-value metrics agg (eg stats,
 *                                  extended_stats, etc...). In this case, the order will be based on the avg value of {@code agg3}.
 *     </li>
 *     <li>
 *         {@code agg1["foo"]>agg2>agg3.avg} - where agg1 is multi-bucket, and the path expects a bucket "foo",
 *                                              agg2 are both single-bucket aggs and agg3 is a multi-value metrics agg
 *                                              (eg stats, extended_stats, etc...).
 *                                              In this case, the order will be based on the avg value of {@code agg3}.
 *     </li>
 *     <li>
 *         {@code agg1["foo"]._count} - where agg1 is multi-bucket, and the path expects a bucket "foo".
 *                                      This would extract the doc_count for that specific bucket.
 *     </li>
 * </ul>
 *
 */
public class AggregationPath {
    private static final String AGG_DELIM = ">";

    public static AggregationPath parse(String path) {
        String[] elements = path.split(AGG_DELIM);
        elements = Arrays.stream(elements).filter(s -> !"".equals(s)).toArray(String[]::new);
        List<PathElement> tokens = new ArrayList<>(elements.length);
        String[] tuple = new String[2];
        for (int i = 0; i < elements.length; i++) {
            String element = elements[i];
            if (i == elements.length - 1) {
                int index = element.lastIndexOf('[');
                if (index >= 0) {
                    if (index == 0 || index > element.length() - 3) {
                        throw new IllegalArgumentException("Invalid path element [" + element + "] in path [" + path + "]");
                    }
                    if (element.charAt(element.length() - 1) != ']') {
                        throw new IllegalArgumentException("Invalid path element [" + element + "] in path [" + path + "]");
                    }
                    tokens.add(new PathElement(element, element.substring(0, index), element.substring(index + 1, element.length() - 1)));
                    continue;
                }
                index = element.lastIndexOf('.');
                if (index < 0) {
                    tokens.add(new PathElement(element, element, null));
                    continue;
                }
                if (index == 0 || index > element.length() - 2) {
                    throw new IllegalArgumentException("Invalid path element [" + element + "] in path [" + path + "]");
                }
                tuple = split(element, index, tuple);
                tokens.add(new PathElement(element, tuple[0], tuple[1]));

            } else {
                int index = element.lastIndexOf('[');
                if (index >= 0) {
                    if (index == 0 || index > element.length() - 3) {
                        throw new IllegalArgumentException("Invalid path element [" + element + "] in path [" + path + "]");
                    }
                    if (element.charAt(element.length() - 1) != ']') {
                        throw new IllegalArgumentException("Invalid path element [" + element + "] in path [" + path + "]");
                    }
                    tokens.add(new PathElement(element, element.substring(0, index), element.substring(index + 1, element.length() - 1)));
                    continue;
                }
                tokens.add(new PathElement(element, element, null));
            }
        }
        return new AggregationPath(tokens);
    }


    private final List<PathElement> pathElements;

    public AggregationPath(List<PathElement> tokens) {
        this.pathElements = tokens;
        if (tokens == null || tokens.size() == 0) {
            throw new IllegalArgumentException("Invalid path [" + this + "]");
        }
    }

    public PathElement lastPathElement() {
        return pathElements.get(pathElements.size() - 1);
    }

    public List<PathElement> getPathElements() {
        return this.pathElements;
    }

    public List<String> getPathElementsAsStringList() {
        List<String> stringPathElements = new ArrayList<>();
        for (PathElement pathElement : this.pathElements) {
            stringPathElements.add(pathElement.name);
            if (pathElement.key != null) {
                stringPathElements.add(pathElement.key);
            }
        }
        return stringPathElements;
    }

    private static String[] split(String toSplit, int index, String[] result) {
        result[0] = toSplit.substring(0, index);
        result[1] = toSplit.substring(index + 1);
        return result;
    }

    /**
     * Element in an agg path
     *
     */
    public static class PathElement {
        public String fullName;
        public String name;
        public String key;

        public PathElement(String fullName, String name, String key){
            this.fullName = fullName;
            this.name = name;
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PathElement token = (PathElement) o;

            if (key != null ? !key.equals(token.key) : token.key != null) {
                return false;
            }
            if (!name.equals(token.name)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (key != null ? key.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return fullName;
        }
    }
}
