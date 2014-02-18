/*******************************************************************************
 * Copyright 2012-present Pixate, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.pixate.freestyle.cg.math;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;

/**
 * Pixate Elliptical Arc.
 */
public class PXEllipticalArc {
    public static final float M_PI_2 = (float) (Math.PI / 2.0);
    public static final float TWO_PI = (float) (2.0 * Math.PI);
    private static final float THRESHOLD = 0.25f;
    // @formatter:off
    private static float coeffs3Low[][][] = {
      {
        {  3.85268f,   -21.229f,      -0.330434f,    0.0127842f  },
        { -1.61486f,     0.706564f,    0.225945f,    0.263682f   },
        { -0.910164f,    0.388383f,    0.00551445f,  0.00671814f },
        { -0.630184f,    0.192402f,    0.0098871f,   0.0102527f  }
      }, {
        { -0.162211f,    9.94329f,     0.13723f,     0.0124084f  },
        { -0.253135f,    0.00187735f,  0.0230286f,   0.01264f    },
        { -0.0695069f,  -0.0437594f,   0.0120636f,   0.0163087f  },
        { -0.0328856f,  -0.00926032f, -0.00173573f,  0.00527385f }
      }
    };
    private static float coeffs3High[][][] = {
      {
        {  0.0899116f, -19.2349f,     -4.11711f,     0.183362f   },
        {  0.138148f,   -1.45804f,     1.32044f,     1.38474f    },
        {  0.230903f,   -0.450262f,    0.219963f,    0.414038f   },
        {  0.0590565f,  -0.101062f,    0.0430592f,   0.0204699f  }
      }, {
        {  0.0164649f,   9.89394f,     0.0919496f,   0.00760802f },
        {  0.0191603f,  -0.0322058f,   0.0134667f,  -0.0825018f  },
        {  0.0156192f,  -0.017535f,    0.00326508f, -0.228157f   },
        { -0.0236752f,   0.0405821f,  -0.0173086f,   0.176187f   }
      }
    };
    // @formatter:on
    private float cx;
    private float cy;
    private float a;
    private float b;
    private float eta1;
    private float eta2;
    private float sinTheta;
    private float cosTheta;

    /**
     * Constructs a new PXEllipticalArc
     * 
     * @param cx
     * @param cy
     * @param radiusX
     * @param radiusY
     * @param startingAngle
     * @param endingAngle
     */
    public PXEllipticalArc(float cx, float cy, float radiusX, float radiusY, float startingAngle,
            float endingAngle) {
        this.cx = cx;
        this.cy = cy;
        this.a = radiusX;
        this.b = radiusY;
        eta1 = (float) Math.atan2(Math.sin(startingAngle) / b, Math.cos(startingAngle) / a);
        eta2 = (float) Math.atan2(Math.sin(endingAngle) / b, Math.cos(endingAngle) / a);

        if (eta1 > 0 && startingAngle < 0) {
            eta1 -= TWO_PI;
        } else if (eta1 < 0 && startingAngle > 0) {
            eta1 += TWO_PI;
        }
        if (eta2 > 0 && endingAngle < 0) {
            eta2 -= TWO_PI;
        } else if (eta2 < 0 && endingAngle > 0) {
            eta2 += TWO_PI;
        }

        // make sure eta1 <= eta2 <= eta1 + 2??
        // eta2 -= TWO_PI * floorf((eta2 - eta1) / TWO_PI);
        //
        // if ((endingAngle - startingAngle > M_PI) && (eta2 - eta1 < M_PI))
        // {
        // eta2 += TWO_PI;
        // }
        // assume axis-aligned
        cosTheta = 1.0f;
        sinTheta = 0.0f;
    }

    /**
     * Adds the elliptical path to the path.
     * 
     * @param path
     * @param transform
     * @return The last path point
     */
    public PointF addToPath(Path path, Matrix transform) {
        Matrix pTransform = transform.isIdentity() ? null : transform;

        boolean found = false;
        int n = 1;

        while (!found && (n < 1024)) {
            float dEta = (eta2 - eta1) / n;
            if (dEta <= M_PI_2) {
                float etaB = eta1;
                found = true;
                for (int i = 0; found && (i < n); ++i) {
                    float etaA = etaB;
                    etaB += dEta;
                    float error = estimateErrorForStartingAngle(etaA, etaB);
                    found = (error <= THRESHOLD);
                }
            }
            n = n << 1;
        }

        float dEta = (eta2 - eta1) / n;
        float etaB = eta1;

        float cosEtaB = (float) Math.cos(etaB);
        float sinEtaB = (float) Math.sin(etaB);
        float aCosEtaB = a * cosEtaB;
        float bSinEtaB = b * sinEtaB;
        float aSinEtaB = a * sinEtaB;
        float bCosEtaB = b * cosEtaB;
        float xB = cx + aCosEtaB * cosTheta - bSinEtaB * sinTheta;
        float yB = cy + aCosEtaB * sinTheta + bSinEtaB * cosTheta;
        float xBDot = -aSinEtaB * cosTheta - bCosEtaB * sinTheta;
        float yBDot = -aSinEtaB * sinTheta + bCosEtaB * cosTheta;

        float t = (float) Math.tan(0.5f * dEta);
        float alpha = (float) (Math.sin(dEta) * (Math.sqrt(4.0f + 3.0f * t * t) - 1.0f) / 3.0f);

        for (int i = 0; i < n; ++i) {
            // float etaA = etaB;
            float xA = xB;
            float yA = yB;
            float xADot = xBDot;
            float yADot = yBDot;

            etaB += dEta;

            cosEtaB = (float) Math.cos(etaB);
            sinEtaB = (float) Math.sin(etaB);
            aCosEtaB = a * cosEtaB;
            bSinEtaB = b * sinEtaB;
            aSinEtaB = a * sinEtaB;
            bCosEtaB = b * cosEtaB;
            xB = cx + aCosEtaB * cosTheta - bSinEtaB * sinTheta;
            yB = cy + aCosEtaB * sinTheta + bSinEtaB * cosTheta;
            xBDot = -aSinEtaB * cosTheta - bCosEtaB * sinTheta;
            yBDot = -aSinEtaB * sinTheta + bCosEtaB * cosTheta;

            float c1x = (xA + alpha * xADot);
            float c1y = (yA + alpha * yADot);
            float c2x = (xB - alpha * xBDot);
            float c2y = (yB - alpha * yBDot);

            if (pTransform != null) {
                float[] points = new float[] { c1x, c1y, c2x, c2y, xB, yB };
                transform.mapPoints(points);
            }
            // CGPathAddCurveToPoint(path, pTransform, c1x, c1y, c2x, c2y, xB,
            // yB);
            path.cubicTo(c1x, c1y, c2x, c2y, xB, yB);
        }
        return new PointF(xB, yB);
    }

    private float estimateErrorForStartingAngle(float etaA, float etaB) {
        float eta = (etaA + etaB) * 0.5f;
        float x = b / a;
        float dEta = etaB - etaA;
        float cos2 = (float) Math.cos(2.0f * eta);
        float cos4 = (float) Math.cos(4.0f * eta);
        float cos6 = (float) Math.cos(6.0f * eta);

        float safety[] = { 0.001f, 4.98f, 0.207f, 0.0067f };
        float c0, c1;

        if (x < 0.25f) {
            c0 = rationalFunction(x, coeffs3Low[0][0]) + cos2
                    * rationalFunction(x, coeffs3Low[0][1]) + cos4
                    * rationalFunction(x, coeffs3Low[0][2]) + cos6
                    * rationalFunction(x, coeffs3Low[0][3]);

            c1 = rationalFunction(x, coeffs3Low[1][0]) + cos2
                    * rationalFunction(x, coeffs3Low[1][1]) + cos4
                    * rationalFunction(x, coeffs3Low[1][2]) + cos6
                    * rationalFunction(x, coeffs3Low[1][3]);
        } else {
            c0 = rationalFunction(x, coeffs3High[0][0]) + cos2
                    * rationalFunction(x, coeffs3High[0][1]) + cos4
                    * rationalFunction(x, coeffs3High[0][2]) + cos6
                    * rationalFunction(x, coeffs3High[0][3]);

            c1 = rationalFunction(x, coeffs3High[1][0]) + cos2
                    * rationalFunction(x, coeffs3High[1][1]) + cos4
                    * rationalFunction(x, coeffs3High[1][2]) + cos6
                    * rationalFunction(x, coeffs3High[1][3]);
        }

        return (float) (rationalFunction(x, safety) * a * Math.exp(c0 + c1 * dEta));
    }

    private static float rationalFunction(float x, float[] c) {
        return ((x * (x * c[0] + c[1]) + c[2]) / (x + c[3]));
    }

    /**
     * Adds an elliptical arc to the given path.
     * 
     * @param path
     * @param m
     * @param x
     * @param y
     * @param radiusX
     * @param radiusY
     * @param startAngle
     * @param endAngle
     * @return The last path point.
     */
    public static PointF pathAddEllipticalArc(Path path, Matrix m, float x, float y, float radiusX,
            float radiusY, float startAngle, float endAngle) {
        PXEllipticalArc arc = new PXEllipticalArc(x, y, radiusX, radiusY, startAngle, endAngle);
        if (m == null) {
            return arc.addToPath(path, new Matrix());
        } else {
            return arc.addToPath(path, m);
        }
    }
}
