/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.LatLonShape.QueryRelation;
import org.apache.lucene.geo.Line;
import org.apache.lucene.geo.Line2D;
import org.apache.lucene.geo.Polygon2D;

/** random bounding box and polygon query tests for random indexed arrays of {@link Line} types */
public class TestLatLonMultiLineShapeQueries extends BaseLatLonShapeTestCase {

  protected final MultiLineValidator VALIDATOR = new MultiLineValidator();
  protected final TestLatLonLineShapeQueries.LineValidator LINEVALIDATOR = new TestLatLonLineShapeQueries.LineValidator();

  @Override
  protected ShapeType getShapeType() {
    return ShapeType.LINE;
  }

  @Override
  protected Line[] nextShape() {
    int n = random().nextInt(4) + 1;
    Line[] lines = new Line[n];
    for (int i =0; i < n; i++) {
      lines[i] = nextLine();
    }
    return lines;
  }

  @Override
  protected Field[] createIndexableFields(String name, Object o) {
    Line[] lines = (Line[]) o;
    List<Field> allFields = new ArrayList<>();
    for (Line line : lines) {
      Field[] fields = LatLonShape.createIndexableFields(name, line);
      for (Field field : fields) {
        allFields.add(field);
      }
    }
    return allFields.toArray(new Field[allFields.size()]);
  }

  @Override
  protected Validator getValidator(QueryRelation relation) {
    VALIDATOR.setRelation(relation);
    LINEVALIDATOR.setRelation(relation);
    return VALIDATOR;
  }

  protected class MultiLineValidator extends Validator {
    @Override
    public boolean testBBoxQuery(double minLat, double maxLat, double minLon, double maxLon, Object shape) {
      Line[] lines = (Line[])shape;
      for (Line l : lines) {
        boolean b = LINEVALIDATOR.testBBoxQuery(minLat, maxLat, minLon, maxLon, l);
        if (b == true && queryRelation == QueryRelation.INTERSECTS) {
          return true;
        } else if (b == false && queryRelation == QueryRelation.DISJOINT) {
          return false;
        } else if (b == false && queryRelation == QueryRelation.WITHIN) {
          return false;
        }
      }
      return queryRelation != QueryRelation.INTERSECTS;
    }

    @Override
    public boolean testLineQuery(Line2D query, Object shape) {
      Line[] lines = (Line[])shape;
      for (Line l : lines) {
        boolean b = LINEVALIDATOR.testLineQuery(query, l);
        if (b == true && queryRelation == QueryRelation.INTERSECTS) {
          return true;
        } else if (b == false && queryRelation == QueryRelation.DISJOINT) {
          return false;
        } else if (b == false && queryRelation == QueryRelation.WITHIN) {
          return false;
        }
      }
      return queryRelation != QueryRelation.INTERSECTS;
    }

    @Override
    public boolean testPolygonQuery(Polygon2D query, Object shape) {
      Line[] lines = (Line[])shape;
      for (Line l : lines) {
        boolean b = LINEVALIDATOR.testPolygonQuery(query, l);
        if (b == true && queryRelation == QueryRelation.INTERSECTS) {
          return true;
        } else if (b == false && queryRelation == QueryRelation.DISJOINT) {
          return false;
        } else if (b == false && queryRelation == QueryRelation.WITHIN) {
          return false;
        }
      }
      return queryRelation != QueryRelation.INTERSECTS;
    }
  }

  @Slow
  @Nightly
  @Override
  public void testRandomBig() throws Exception {
    doTestRandom(10000);
  }
}