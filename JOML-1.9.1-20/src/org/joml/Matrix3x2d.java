/*
 * (C) Copyright 2017 JOML

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
//#ifdef __HAS_NIO__
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
//#endif
import java.text.DecimalFormat;
import java.text.NumberFormat;

//#ifdef __GWT__
import com.google.gwt.typedarrays.shared.Float64Array;
//#endif

/**
 * Contains the definition of a 3x2 matrix of doubles, and associated functions to transform
 * it. The matrix is column-major to match OpenGL's interpretation, and it looks like this:
 * <p>
 *      m00  m10  m20<br>
 *      m01  m11  m21<br>
 * 
 * @author Kai Burjack
 */
public class Matrix3x2d implements Matrix3x2dc, Externalizable {

    private final class Proxy implements Matrix3x2dc {
        private final Matrix3x2dc delegate;

        Proxy(Matrix3x2dc delegate) {
            this.delegate = delegate;
        }

        public double m00() {
            return delegate.m00();
        }

        public double m01() {
            return delegate.m01();
        }

        public double m10() {
            return delegate.m10();
        }

        public double m11() {
            return delegate.m11();
        }

        public double m20() {
            return delegate.m20();
        }

        public double m21() {
            return delegate.m21();
        }

        public Matrix3x2d mul(Matrix3x2dc right, Matrix3x2d dest) {
            return delegate.mul(right, dest);
        }

        public double determinant() {
            return delegate.determinant();
        }

        public Matrix3x2d invert(Matrix3x2d dest) {
            return delegate.invert(dest);
        }

        public Matrix3x2d translate(double x, double y, Matrix3x2d dest) {
            return delegate.translate(x, y, dest);
        }

        public Matrix3x2d translate(Vector2d offset, Matrix3x2d dest) {
            return delegate.translate(offset, dest);
        }

        public Matrix3x2d get(Matrix3x2d dest) {
            return delegate.get(dest);
        }

//#ifdef __GWT__
        public Float64Array get(Float64Array buffer) {
            return delegate.get(buffer);
        }

        public Float64Array get(int index, Float64Array buffer) {
            return delegate.get(index, buffer);
        }
//#endif

//#ifdef __HAS_NIO__
        public DoubleBuffer get(DoubleBuffer buffer) {
            return delegate.get(buffer);
        }

        public DoubleBuffer get(int index, DoubleBuffer buffer) {
            return delegate.get(index, buffer);
        }

        public ByteBuffer get(ByteBuffer buffer) {
            return delegate.get(buffer);
        }

        public ByteBuffer get(int index, ByteBuffer buffer) {
            return delegate.get(index, buffer);
        }

        public DoubleBuffer get4x4(DoubleBuffer buffer) {
            return delegate.get4x4(buffer);
        }

        public DoubleBuffer get4x4(int index, DoubleBuffer buffer) {
            return delegate.get4x4(index, buffer);
        }

        public ByteBuffer get4x4(ByteBuffer buffer) {
            return delegate.get4x4(buffer);
        }

        public ByteBuffer get4x4(int index, ByteBuffer buffer) {
            return delegate.get4x4(index, buffer);
        }
//#endif

        public double[] get(double[] arr, int offset) {
            return delegate.get(arr, offset);
        }

        public double[] get(double[] arr) {
            return delegate.get(arr);
        }

        public double[] get4x4(double[] arr, int offset) {
            return delegate.get4x4(arr, offset);
        }

        public double[] get4x4(double[] arr) {
            return delegate.get4x4(arr);
        }

        public Matrix3x2d scale(double x, double y, Matrix3x2d dest) {
            return delegate.scale(x, y, dest);
        }

        public Matrix3x2d scaleAroundLocal(double sx, double sy, double ox, double oy, Matrix3x2d dest) {
            return delegate.scaleAroundLocal(sx, sy, ox, oy, dest);
        }

        public Matrix3x2d scaleAroundLocal(double factor, double ox, double oy, Matrix3x2d dest) {
            return delegate.scaleAroundLocal(factor, ox, oy, dest);
        }

        public Matrix3x2d scale(double xy, Matrix3x2d dest) {
            return delegate.scale(xy, dest);
        }

        public Matrix3x2d scaleAround(double sx, double sy, double ox, double oy, Matrix3x2d dest) {
            return delegate.scaleAround(sx, sy, ox, oy, dest);
        }

        public Matrix3x2d scaleAround(double factor, double ox, double oy, Matrix3x2d dest) {
            return delegate.scaleAround(factor, ox, oy, dest);
        }

        public Vector3d transform(Vector3d v) {
            return delegate.transform(v);
        }

        public Vector3d transform(Vector3d v, Vector3d dest) {
            return delegate.transform(v, dest);
        }

        public Vector2d transformPosition(Vector2d v) {
            return delegate.transformPosition(v);
        }

        public Vector2d transformPosition(Vector2d v, Vector2d dest) {
            return delegate.transformPosition(v, dest);
        }

        public Vector2d transformDirection(Vector2d v) {
            return delegate.transformDirection(v);
        }

        public Vector2d transformDirection(Vector2d v, Vector2d dest) {
            return delegate.transformDirection(v, dest);
        }

        public Matrix3x2d rotate(double ang, Matrix3x2d dest) {
            return delegate.rotate(ang, dest);
        }

        public Matrix3x2d rotateAbout(double ang, double x, double y, Matrix3x2d dest) {
            return delegate.rotateAbout(ang, x, y, dest);
        }

        public Matrix3x2d rotateTo(Vector2d fromDir, Vector2d toDir, Matrix3x2d dest) {
            return delegate.rotateTo(fromDir, toDir, dest);
        }

        public Matrix3x2d view(double left, double right, double bottom, double top, Matrix3x2d dest) {
            return delegate.view(left, right, bottom, top, dest);
        }

        public Vector2d origin(Vector2d origin) {
            return delegate.origin(origin);
        }

        public double[] viewArea(double[] area) {
            return delegate.viewArea(area);
        }

        public Vector2d positiveX(Vector2d dir) {
            return delegate.positiveX(dir);
        }

        public Vector2d normalizedPositiveX(Vector2d dir) {
            return delegate.normalizedPositiveX(dir);
        }

        public Vector2d positiveY(Vector2d dir) {
            return delegate.positiveY(dir);
        }

        public Vector2d normalizedPositiveY(Vector2d dir) {
            return delegate.normalizedPositiveY(dir);
        }

        public Vector2d unproject(double winX, double winY, int[] viewport, Vector2d dest) {
            return delegate.unproject(winX, winY, viewport, dest);
        }

        public Vector2d unprojectInv(double winX, double winY, int[] viewport, Vector2d dest) {
            return delegate.unprojectInv(winX, winY, viewport, dest);
        }
    }

    private static final long serialVersionUID = 1L;

    public double m00, m01;
    public double m10, m11;
    public double m20, m21;

    /**
     * Create a new {@link Matrix3x2d} and set it to {@link #identity() identity}.
     */
    public Matrix3x2d() {
        this.m00 = 1.0;
        this.m11 = 1.0;
    }

    /**
     * Create a new {@link Matrix3x2d} and make it a copy of the given matrix.
     * 
     * @param mat
     *          the {@link Matrix3x2dc} to copy the values from
     */
    public Matrix3x2d(Matrix3x2dc mat) {
        if (mat instanceof Matrix3x2d) {
            MemUtil.INSTANCE.copy((Matrix3x2d) mat, this);
        } else {
            setMatrix3x2dc(mat);
        }
    }

    /**
     * Create a new 3x2 matrix using the supplied double values. The order of the parameter is column-major, 
     * so the first two parameters specify the two elements of the first column.
     * 
     * @param m00
     *          the value of m00
     * @param m01
     *          the value of m01
     * @param m10
     *          the value of m10
     * @param m11
     *          the value of m11
     * @param m20
     *          the value of m20
     * @param m21
     *          the value of m21
     */
    public Matrix3x2d(double m00, double m01,
                      double m10, double m11,
                      double m20, double m21) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
        this.m20 = m20;
        this.m21 = m21;
    }

//#ifdef __HAS_NIO__
    /**
     * Create a new {@link Matrix3x2d} by reading its 6 double components from the given {@link DoubleBuffer}
     * at the buffer's current position.
     * <p>
     * That DoubleBuffer is expected to hold the values in column-major order.
     * <p>
     * The buffer's position will not be changed by this method.
     * 
     * @param buffer
     *          the {@link DoubleBuffer} to read the matrix values from
     */
    public Matrix3x2d(DoubleBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }
//#endif

    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#m00()
     */
    public double m00() {
        return m00;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#m01()
     */
    public double m01() {
        return m01;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#m10()
     */
    public double m10() {
        return m10;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#m11()
     */
    public double m11() {
        return m11;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#m20()
     */
    public double m20() {
        return m20;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#m21()
     */
    public double m21() {
        return m21;
    }

    /**
     * Set the elements of this matrix to the ones in <code>m</code>.
     * 
     * @param m
     *          the matrix to copy the elements from
     * @return this
     */
    public Matrix3x2d set(Matrix3x2dc m) {
        if (m instanceof Matrix3x2d) {
            MemUtil.INSTANCE.copy((Matrix3x2d) m, this);
        } else {
            setMatrix3x2dc(m);
        }
        return this;
    }
    private void setMatrix3x2dc(Matrix3x2dc mat) {
        m00 = mat.m00();
        m01 = mat.m01();
        m10 = mat.m10();
        m11 = mat.m11();
        m20 = mat.m20();
        m21 = mat.m21();
    }

    /**
     * Multiply this matrix by the supplied <code>right</code> matrix by assuming a third row in
     * both matrices of <tt>(0, 0, 1)</tt>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     * 
     * @param right
     *          the right operand of the matrix multiplication
     * @return this
     */
    public Matrix3x2d mul(Matrix3x2dc right) {
        return mul(right, this);
    }

    /**
     * Multiply this matrix by the supplied <code>right</code> matrix by assuming a third row in
     * both matrices of <tt>(0, 0, 1)</tt> and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     * 
     * @param right
     *          the right operand of the matrix multiplication
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix3x2d mul(Matrix3x2dc right, Matrix3x2d dest) {
        double nm00 = m00 * right.m00() + m10 * right.m01();
        double nm01 = m01 * right.m00() + m11 * right.m01();
        double nm10 = m00 * right.m10() + m10 * right.m11();
        double nm11 = m01 * right.m10() + m11 * right.m11();
        double nm20 = m00 * right.m20() + m10 * right.m21() + m20;
        double nm21 = m01 * right.m20() + m11 * right.m21() + m21;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m20 = nm20;
        dest.m21 = nm21;
        return dest;
    }

    /**
     * Set the values within this matrix to the supplied double values. The result looks like this:
     * <p>
     * m00, m10, m20<br>
     * m01, m11, m21<br>
     * 
     * @param m00
     *          the new value of m00
     * @param m01
     *          the new value of m01
     * @param m10
     *          the new value of m10
     * @param m11
     *          the new value of m11
     * @param m20
     *          the new value of m20
     * @param m21
     *          the new value of m21
     * @return this
     */
    public Matrix3x2d set(double m00, double m01, 
                          double m10, double m11, 
                          double m20, double m21) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
        this.m20 = m20;
        this.m21 = m21;
        return this;
    }

    /**
     * Set the values in this matrix based on the supplied double array. The result looks like this:
     * <p>
     * 0, 2, 4<br>
     * 1, 3, 5<br>
     * 
     * This method only uses the first 6 values, all others are ignored.
     * 
     * @param m
     *          the array to read the matrix values from
     * @return this
     */
    public Matrix3x2d set(double m[]) {
        MemUtil.INSTANCE.copy(m, 0, this);
        return this;
    }

    /**
     * Return the determinant of this matrix.
     * 
     * @return the determinant
     */
    public double determinant() {
        return m00 * m11 - m01 * m10;
    }

    /**
     * Invert this matrix by assuming a third row in this matrix of <tt>(0, 0, 1)</tt>.
     *
     * @return this
     */
    public Matrix3x2d invert() {
        return invert(this);
    }

    /**
     * Invert the <code>this</code> matrix by assuming a third row in this matrix of <tt>(0, 0, 1)</tt>
     * and store the result in <code>dest</code>.
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix3x2d invert(Matrix3x2d dest) {
        // client must make sure that matrix is invertible
        double s = 1.0 / (m00 * m11 - m01 * m10);
        double nm00 =  m11 * s;
        double nm01 = -m01 * s;
        double nm10 = -m10 * s;
        double nm11 =  m00 * s;
        double nm20 = (m10 * m21 - m20 * m11) * s;
        double nm21 = (m20 * m01 - m00 * m21) * s;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m20 = nm20;
        dest.m21 = nm21;
        return dest;
    }

    /**
     * Set this matrix to be a simple translation matrix in a two-dimensional coordinate system.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional translation.
     * <p>
     * In order to apply a translation via to an already existing transformation
     * matrix, use {@link #translate(double, double) translate()} instead.
     * 
     * @see #translate(double, double)
     * 
     * @param x
     *          the units to translate in x
     * @param y
     *          the units to translate in y
     * @return this
     */
    public Matrix3x2d translation(double x, double y) {
        m00 = 1.0;
        m01 = 0.0;
        m10 = 0.0;
        m11 = 1.0;
        m20 = x;
        m21 = y;
        return this;
    }

    /**
     * Set this matrix to be a simple translation matrix in a two-dimensional coordinate system.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional translation.
     * <p>
     * In order to apply a translation via to an already existing transformation
     * matrix, use {@link #translate(Vector2d) translate()} instead.
     * 
     * @see #translate(Vector2d)
     * 
     * @param offset
     *          the translation
     * @return this
     */
    public Matrix3x2d translation(Vector2d offset) {
        return translation(offset.x, offset.y);
    }

    /**
     * Set only the translation components of this matrix <tt>(m20, m21)</tt> to the given values <tt>(x, y)</tt>.
     * <p>
     * To build a translation matrix instead, use {@link #translation(double, double)}.
     * To apply a translation to another matrix, use {@link #translate(double, double)}.
     * 
     * @see #translation(double, double)
     * @see #translate(double, double)
     * 
     * @param x
     *          the offset to translate in x
     * @param y
     *          the offset to translate in y
     * @return this
     */
    public Matrix3x2d setTranslation(double x, double y) {
        m20 = x;
        m21 = y;
        return this;
    }

    /**
     * Set only the translation components of this matrix <tt>(m20, m21)</tt> to the given values <tt>(offset.x, offset.y)</tt>.
     * <p>
     * To build a translation matrix instead, use {@link #translation(Vector2d)}.
     * To apply a translation to another matrix, use {@link #translate(Vector2d)}.
     * 
     * @see #translation(Vector2d)
     * @see #translate(Vector2d)
     * 
     * @param offset
     *          the new translation to set
     * @return this
     */
    public Matrix3x2d setTranslation(Vector2d offset) {
        return setTranslation(offset.x, offset.y);
    }

    /**
     * Apply a translation to this matrix by translating by the given number of units in x and y and store the result
     * in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * <p>
     * In order to set the matrix to a translation transformation without post-multiplying
     * it, use {@link #translation(double, double)}.
     * 
     * @see #translation(double, double)
     * 
     * @param x
     *          the offset to translate in x
     * @param y
     *          the offset to translate in y
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix3x2d translate(double x, double y, Matrix3x2d dest) {
        double rm20 = x;
        double rm21 = y;
        dest.m20 = m00 * rm20 + m10 * rm21 + m20;
        dest.m21 = m01 * rm20 + m11 * rm21 + m21;
        dest.m00 = m00;
        dest.m01 = m01;
        dest.m10 = m10;
        dest.m11 = m11;
        return dest;
    }

    /**
     * Apply a translation to this matrix by translating by the given number of units in x and y.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * <p>
     * In order to set the matrix to a translation transformation without post-multiplying
     * it, use {@link #translation(double, double)}.
     * 
     * @see #translation(double, double)
     * 
     * @param x
     *          the offset to translate in x
     * @param y
     *          the offset to translate in y
     * @return this
     */
    public Matrix3x2d translate(double x, double y) {
        return translate(x, y, this);
    }

    /**
     * Apply a translation to this matrix by translating by the given number of units in x and y, and
     * store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * <p>
     * In order to set the matrix to a translation transformation without post-multiplying
     * it, use {@link #translation(double, double)}.
     * 
     * @see #translation(Vector2d)
     * 
     * @param offset
     *          the offset to translate
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix3x2d translate(Vector2d offset, Matrix3x2d dest) {
        return translate(offset.x, offset.y, dest);
    }

    /**
     * Apply a translation to this matrix by translating by the given number of units in x and y.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * <p>
     * In order to set the matrix to a translation transformation without post-multiplying
     * it, use {@link #translation(double, double)}.
     * 
     * @see #translation(Vector2d)
     * 
     * @param offset
     *          the offset to translate
     * @return this
     */
    public Matrix3x2d translate(Vector2d offset) {
        return translate(offset.x, offset.y, this);
    }

    /**
     * Return a string representation of this matrix.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<tt>0.000E0;-</tt>".
     * 
     * @return the string representation
     */
    public String toString() {
        DecimalFormat formatter = new DecimalFormat(" 0.000E0;-");
        String str = toString(formatter);
        StringBuffer res = new StringBuffer();
        int eIndex = Integer.MIN_VALUE;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == 'E') {
                eIndex = i;
            } else if (c == ' ' && eIndex == i - 1) {
                // workaround Java 1.4 DecimalFormat bug
                res.append('+');
                continue;
            } else if (Character.isDigit(c) && eIndex == i - 1) {
                res.append('+');
            }
            res.append(c);
        }
        return res.toString();
    }

    /**
     * Return a string representation of this matrix by formatting the matrix elements with the given {@link NumberFormat}.
     * 
     * @param formatter
     *          the {@link NumberFormat} used to format the matrix values with
     * @return the string representation
     */
    public String toString(NumberFormat formatter) {
        return formatter.format(m00) + " " + formatter.format(m10) + " " + formatter.format(m20) + "\n"
             + formatter.format(m01) + " " + formatter.format(m11) + " " + formatter.format(m21) + "\n";
    }

    /**
     * Get the current values of <code>this</code> matrix and store them into
     * <code>dest</code>.
     * <p>
     * This is the reverse method of {@link #set(Matrix3x2dc)} and allows to obtain
     * intermediate calculation results when chaining multiple transformations.
     * 
     * @see #set(Matrix3x2dc)
     * 
     * @param dest
     *          the destination matrix
     * @return dest
     */
    public Matrix3x2d get(Matrix3x2d dest) {
        return dest.set(this);
    }

//#ifdef __GWT__
      /* (non-Javadoc)
       * @see org.joml.Matrix3x2dc#get(com.google.gwt.typedarrays.shared.Float64Array)
       */
      public Float64Array get(Float64Array buffer) {
          buffer.set(0,  m00);
          buffer.set(1,  m01);
          buffer.set(2,  m10);
          buffer.set(3,  m11);
          buffer.set(4,  m20);
          buffer.set(5,  m21);
          return buffer;
      }
      /* (non-Javadoc)
       * @see org.joml.Matrix3x2dc#get(int, com.google.gwt.typedarrays.shared.Float64Array)
       */
      public Float64Array get(int index, Float64Array buffer) {
          buffer.set(index,    m00);
          buffer.set(index+1,  m01);
          buffer.set(index+2,  m10);
          buffer.set(index+3,  m11);
          buffer.set(index+4,  m20);
          buffer.set(index+5,  m21);
          return buffer;
      }
//#endif

//#ifdef __HAS_NIO__
    /**
     * Store this matrix in column-major order into the supplied {@link DoubleBuffer} at the current
     * buffer {@link DoubleBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given DoubleBuffer.
     * <p>
     * In order to specify the offset into the DoubleBuffer at which
     * the matrix is stored, use {@link #get(int, DoubleBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #get(int, DoubleBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    public DoubleBuffer get(DoubleBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    /**
     * Store this matrix in column-major order into the supplied {@link DoubleBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given DoubleBuffer.
     * 
     * @param index
     *            the absolute position into the DoubleBuffer
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    public DoubleBuffer get(int index, DoubleBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    /**
     * Store this matrix in column-major order into the supplied {@link ByteBuffer} at the current
     * buffer {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which
     * the matrix is stored, use {@link #get(int, ByteBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #get(int, ByteBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    public ByteBuffer get(ByteBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    /**
     * Store this matrix in column-major order into the supplied {@link ByteBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * 
     * @param index
     *            the absolute position into the ByteBuffer
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    /**
     * Store this matrix as an equivalent 4x4 matrix in column-major order into the supplied {@link DoubleBuffer} at the current
     * buffer {@link DoubleBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given DoubleBuffer.
     * <p>
     * In order to specify the offset into the DoubleBuffer at which
     * the matrix is stored, use {@link #get4x4(int, DoubleBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #get4x4(int, DoubleBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    public DoubleBuffer get4x4(DoubleBuffer buffer) {
        MemUtil.INSTANCE.put4x4(this, 0, buffer);
        return buffer;
    }

    /**
     * Store this matrix as an equivalent 4x4 matrix in column-major order into the supplied {@link DoubleBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given DoubleBuffer.
     * 
     * @param index
     *            the absolute position into the DoubleBuffer
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    public DoubleBuffer get4x4(int index, DoubleBuffer buffer) {
        MemUtil.INSTANCE.put4x4(this, index, buffer);
        return buffer;
    }

    /**
     * Store this matrix as an equivalent 4x4 matrix in column-major order into the supplied {@link ByteBuffer} at the current
     * buffer {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which
     * the matrix is stored, use {@link #get4x4(int, ByteBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #get4x4(int, ByteBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    public ByteBuffer get4x4(ByteBuffer buffer) {
        MemUtil.INSTANCE.put4x4(this, 0, buffer);
        return buffer;
    }

    /**
     * Store this matrix as an equivalent 4x4 matrix in column-major order into the supplied {@link ByteBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * 
     * @param index
     *            the absolute position into the ByteBuffer
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    public ByteBuffer get4x4(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put4x4(this, index, buffer);
        return buffer;
    }
//#endif

    /**
     * Store this matrix into the supplied double array in column-major order at the given offset.
     * 
     * @param arr
     *          the array to write the matrix values into
     * @param offset
     *          the offset into the array
     * @return the passed in array
     */
    public double[] get(double[] arr, int offset) {
        MemUtil.INSTANCE.copy(this, arr, offset);
        return arr;
    }

    /**
     * Store this matrix into the supplied double array in column-major order.
     * <p>
     * In order to specify an explicit offset into the array, use the method {@link #get(double[], int)}.
     * 
     * @see #get(double[], int)
     * 
     * @param arr
     *          the array to write the matrix values into
     * @return the passed in array
     */
    public double[] get(double[] arr) {
        return get(arr, 0);
    }

    /**
     * Store this matrix into the supplied double array in column-major order at the given offset.
     * 
     * @param arr
     *          the array to write the matrix values into
     * @param offset
     *          the offset into the array
     * @return the passed in array
     */
    public double[] get4x4(double[] arr, int offset) {
        MemUtil.INSTANCE.copy4x4(this, arr, offset);
        return arr;
    }

    /**
     * Store this matrix into the supplied double array in column-major order.
     * <p>
     * In order to specify an explicit offset into the array, use the method {@link #get4x4(double[], int)}.
     * 
     * @see #get4x4(double[], int)
     * 
     * @param arr
     *          the array to write the matrix values into
     * @return the passed in array
     */
    public double[] get4x4(double[] arr) {
        return get4x4(arr, 0);
    }

//#ifdef __HAS_NIO__
    /**
     * Set the values of this matrix by reading 6 double values from the given {@link DoubleBuffer} in column-major order,
     * starting at its current position.
     * <p>
     * The DoubleBuffer is expected to contain the values in column-major order.
     * <p>
     * The position of the DoubleBuffer will not be changed by this method.
     * 
     * @param buffer
     *              the DoubleBuffer to read the matrix values from in column-major order
     * @return this
     */
    public Matrix3x2d set(DoubleBuffer buffer) {
        int pos = buffer.position();
        MemUtil.INSTANCE.get(this, pos, buffer);
        return this;
    }

    /**
     * Set the values of this matrix by reading 6 double values from the given {@link ByteBuffer} in column-major order,
     * starting at its current position.
     * <p>
     * The ByteBuffer is expected to contain the values in column-major order.
     * <p>
     * The position of the ByteBuffer will not be changed by this method.
     * 
     * @param buffer
     *              the ByteBuffer to read the matrix values from in column-major order
     * @return this
     */
    public Matrix3x2d set(ByteBuffer buffer) {
        int pos = buffer.position();
        MemUtil.INSTANCE.get(this, pos, buffer);
        return this;
    }
//#endif

    /**
     * Set all values within this matrix to zero.
     * 
     * @return this
     */
    public Matrix3x2d zero() {
        MemUtil.INSTANCE.zero(this);
        return this;
    }

    /**
     * Set this matrix to the identity.
     * 
     * @return this
     */
    public Matrix3x2d identity() {
        MemUtil.INSTANCE.identity(this);
        return this;
    }

    /**
     * Apply scaling to this matrix by scaling the unit axes by the given x and y and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the scaling will be applied first!
     * 
     * @param x
     *            the factor of the x component
     * @param y
     *            the factor of the y component
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix3x2d scale(double x, double y, Matrix3x2d dest) {
        dest.m00 = m00 * x;
        dest.m01 = m01 * x;
        dest.m10 = m10 * y;
        dest.m11 = m11 * y;
        dest.m20 = m20;
        dest.m21 = m21;
        return dest;
    }

    /**
     * Apply scaling to this matrix by scaling the base axes by the given x and y factors.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the scaling will be applied first!
     * 
     * @param x
     *            the factor of the x component
     * @param y
     *            the factor of the y component
     * @return this
     */
    public Matrix3x2d scale(double x, double y) {
        return scale(x, y, this);
    }

    /**
     * Apply scaling to this matrix by uniformly scaling the two base axes by the given <code>xy</code> factor
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the scaling will be applied first!
     * 
     * @see #scale(double, double, Matrix3x2d)
     * 
     * @param xy
     *            the factor for the two components
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix3x2d scale(double xy, Matrix3x2d dest) {
        return scale(xy, xy, dest);
    }

    /**
     * Apply scaling to this matrix by uniformly scaling the two base axes by the given <code>xyz</code> factor.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the scaling will be applied first!
     * 
     * @see #scale(double, double)
     * 
     * @param xy
     *            the factor for the two components
     * @return this
     */
    public Matrix3x2d scale(double xy) {
        return scale(xy, xy);
    }

    /**
     * Apply scaling to <code>this</code> matrix by scaling the base axes by the given sx and
     * sy factors while using <tt>(ox, oy)</tt> as the scaling origin, and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>
     * , the scaling will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>translate(ox, oy, dest).scale(sx, sy).translate(-ox, -oy)</tt>
     * 
     * @param sx
     *            the scaling factor of the x component
     * @param sy
     *            the scaling factor of the y component
     * @param ox
     *            the x coordinate of the scaling origin
     * @param oy
     *            the y coordinate of the scaling origin
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix3x2d scaleAround(double sx, double sy, double ox, double oy, Matrix3x2d dest) {
        double nm20 = m00 * ox + m10 * oy + m20;
        double nm21 = m01 * ox + m11 * oy + m21;
        dest.m00 = m00 * sx;
        dest.m01 = m01 * sx;
        dest.m10 = m10 * sy;
        dest.m11 = m11 * sy;
        dest.m20 = -m00 * ox - m10 * oy + nm20;
        dest.m21 = -m01 * ox - m11 * oy + nm21;
        return dest;
    }

    /**
     * Apply scaling to this matrix by scaling the base axes by the given sx and
     * sy factors while using <tt>(ox, oy)</tt> as the scaling origin.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * scaling will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>translate(ox, oy).scale(sx, sy).translate(-ox, -oy)</tt>
     * 
     * @param sx
     *            the scaling factor of the x component
     * @param sy
     *            the scaling factor of the y component
     * @param ox
     *            the x coordinate of the scaling origin
     * @param oy
     *            the y coordinate of the scaling origin
     * @return this
     */
    public Matrix3x2d scaleAround(double sx, double sy, double ox, double oy) {
        return scaleAround(sx, sy, ox, oy, this);
    }

    /**
     * Apply scaling to this matrix by scaling the base axes by the given <code>factor</code>
     * while using <tt>(ox, oy)</tt> as the scaling origin,
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * scaling will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>translate(ox, oy, dest).scale(factor).translate(-ox, -oy)</tt>
     * 
     * @param factor
     *            the scaling factor for all three axes
     * @param ox
     *            the x coordinate of the scaling origin
     * @param oy
     *            the y coordinate of the scaling origin
     * @param dest
     *            will hold the result
     * @return this
     */
    public Matrix3x2d scaleAround(double factor, double ox, double oy, Matrix3x2d dest) {
        return scaleAround(factor, factor, ox, oy, this);
    }

    /**
     * Apply scaling to this matrix by scaling the base axes by the given <code>factor</code>
     * while using <tt>(ox, oy)</tt> as the scaling origin.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * scaling will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>translate(ox, oy).scale(factor).translate(-ox, -oy)</tt>
     * 
     * @param factor
     *            the scaling factor for all axes
     * @param ox
     *            the x coordinate of the scaling origin
     * @param oy
     *            the y coordinate of the scaling origin
     * @return this
     */
    public Matrix3x2d scaleAround(double factor, double ox, double oy) {
        return scaleAround(factor, factor, ox, oy, this);
    }

    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#scaleAroundLocal(double, double, double, double, double, double, org.joml.Matrix3x2d)
     */
    public Matrix3x2d scaleAroundLocal(double sx, double sy, double ox, double oy, Matrix3x2d dest) {
        dest.m00 = sx * m00;
        dest.m01 = sy * m01;
        dest.m10 = sx * m10;
        dest.m11 = sy * m11;
        dest.m20 = sx * m20 - sx * ox + ox;
        dest.m21 = sy * m21 - sy * oy + oy;
        return dest;
    }

    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#scaleAroundLocal(double, double, double, org.joml.Matrix3x2d)
     */
    public Matrix3x2d scaleAroundLocal(double factor, double ox, double oy, Matrix3x2d dest) {
        return scaleAroundLocal(factor, factor, ox, oy, dest);
    }

    /**
     * Pre-multiply scaling to this matrix by scaling the base axes by the given sx and
     * sy factors while using <tt>(ox, oy)</tt> as the scaling origin.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>S * M</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>S * M * v</code>, the
     * scaling will be applied last!
     * <p>
     * This method is equivalent to calling: <tt>new Matrix3x2d().translate(ox, oy).scale(sx, sy).translate(-ox, -oy).mul(this, this)</tt>
     * 
     * @param sx
     *            the scaling factor of the x component
     * @param sy
     *            the scaling factor of the y component
     * @param sz
     *            the scaling factor of the z component
     * @param ox
     *            the x coordinate of the scaling origin
     * @param oy
     *            the y coordinate of the scaling origin
     * @param oz
     *            the z coordinate of the scaling origin
     * @return this
     */
    public Matrix3x2d scaleAroundLocal(double sx, double sy, double sz, double ox, double oy, double oz) {
        return scaleAroundLocal(sx, sy, ox, oy, this);
    }

    /**
     * Pre-multiply scaling to this matrix by scaling the base axes by the given <code>factor</code>
     * while using <tt>(ox, oy)</tt> as the scaling origin.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>S * M</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>S * M * v</code>, the
     * scaling will be applied last!
     * <p>
     * This method is equivalent to calling: <tt>new Matrix3x2d().translate(ox, oy).scale(factor).translate(-ox, -oy).mul(this, this)</tt>
     * 
     * @param factor
     *            the scaling factor for all three axes
     * @param ox
     *            the x coordinate of the scaling origin
     * @param oy
     *            the y coordinate of the scaling origin
     * @return this
     */
    public Matrix3x2d scaleAroundLocal(double factor, double ox, double oy) {
        return scaleAroundLocal(factor, factor, ox, oy, this);
    }

    /**
     * Set this matrix to be a simple scale matrix, which scales the two base axes uniformly by the given factor.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional scaling.
     * <p>
     * In order to post-multiply a scaling transformation directly to a matrix, use {@link #scale(double) scale()} instead.
     * 
     * @see #scale(double)
     * 
     * @param factor
     *             the scale factor in x and y
     * @return this
     */
    public Matrix3x2d scaling(double factor) {
        return scaling(factor, factor);
    }

    /**
     * Set this matrix to be a simple scale matrix.
     * 
     * @param x
     *             the scale in x
     * @param y
     *             the scale in y
     * @return this
     */
    public Matrix3x2d scaling(double x, double y) {
        m00 = x;
        m01 = 0.0;
        m10 = 0.0;
        m11 = y;
        m20 = 0.0;
        m21 = 0.0;
        return this;
    }

    /**
     * Set this matrix to a rotation matrix which rotates the given radians.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional rotation.
     * <p>
     * In order to apply the rotation transformation to an existing transformation,
     * use {@link #rotate(double) rotate()} instead.
     * 
     * @see #rotate(double)
     * 
     * @param angle
     *          the angle in radians
     * @return this
     */
    public Matrix3x2d rotation(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        m00 = cos;
        m10 = -sin;
        m20 = 0.0;
        m01 = sin;
        m11 = cos;
        m21 = 0.0;
        return this;
    }

    /**
     * Transform/multiply the given vector by this matrix by assuming a third row in this matrix of <tt>(0, 0, 1)</tt>
     * and store the result in that vector.
     * 
     * @see Vector3d#mul(Matrix3x2dc)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector3d transform(Vector3d v) {
        return v.mul(this);
    }

    /**
     * Transform/multiply the given vector by this matrix by assuming a third row in this matrix of <tt>(0, 0, 1)</tt>
     * and store the result in <code>dest</code>.
     * 
     * @see Vector3d#mul(Matrix3x2dc, Vector3d)
     * 
     * @param v
     *          the vector to transform
     * @param dest
     *          will contain the result
     * @return dest
     */
    public Vector3d transform(Vector3d v, Vector3d dest) {
        return v.mul(this, dest);
    }

    /**
     * Transform/multiply the given 2D-vector, as if it was a 3D-vector with z=1, by
     * this matrix and store the result in that vector.
     * <p>
     * The given 2D-vector is treated as a 3D-vector with its z-component being 1.0, so it
     * will represent a position/location in 2D-space rather than a direction.
     * <p>
     * In order to store the result in another vector, use {@link #transformPosition(Vector2d, Vector2d)}.
     * 
     * @see #transformPosition(Vector2d, Vector2d)
     * @see #transform(Vector3d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector2d transformPosition(Vector2d v) {
        v.set(m00 * v.x + m10 * v.y + m20,
              m01 * v.x + m11 * v.y + m21);
        return v;
    }

    /**
     * Transform/multiply the given 2D-vector, as if it was a 3D-vector with z=1, by
     * this matrix and store the result in <code>dest</code>.
     * <p>
     * The given 2D-vector is treated as a 3D-vector with its z-component being 1.0, so it
     * will represent a position/location in 2D-space rather than a direction.
     * <p>
     * In order to store the result in the same vector, use {@link #transformPosition(Vector2d)}.
     * 
     * @see #transformPosition(Vector2d)
     * @see #transform(Vector3d, Vector3d)
     * 
     * @param v
     *          the vector to transform
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Vector2d transformPosition(Vector2d v, Vector2d dest) {
        dest.set(m00 * v.x + m10 * v.y + m20,
                 m01 * v.x + m11 * v.y + m21);
        return dest;
    }

    /**
     * Transform/multiply the given 2D-vector, as if it was a 3D-vector with z=0, by
     * this matrix and store the result in that vector.
     * <p>
     * The given 2D-vector is treated as a 3D-vector with its z-component being <tt>0.0</tt>, so it
     * will represent a direction in 2D-space rather than a position. This method will therefore
     * not take the translation part of the matrix into account.
     * <p>
     * In order to store the result in another vector, use {@link #transformDirection(Vector2d, Vector2d)}.
     * 
     * @see #transformDirection(Vector2d, Vector2d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector2d transformDirection(Vector2d v) {
        v.set(m00 * v.x + m10 * v.y,
              m01 * v.x + m11 * v.y);
        return v;
    }

    /**
     * Transform/multiply the given 2D-vector, as if it was a 3D-vector with z=0, by
     * this matrix and store the result in <code>dest</code>.
     * <p>
     * The given 2D-vector is treated as a 3D-vector with its z-component being <tt>0.0</tt>, so it
     * will represent a direction in 2D-space rather than a position. This method will therefore
     * not take the translation part of the matrix into account.
     * <p>
     * In order to store the result in the same vector, use {@link #transformDirection(Vector2d)}.
     * 
     * @see #transformDirection(Vector2d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Vector2d transformDirection(Vector2d v, Vector2d dest) {
        dest.set(m00 * v.x + m10 * v.y,
                 m01 * v.x + m11 * v.y);
        return dest;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(m00);
        out.writeDouble(m01);
        out.writeDouble(m10);
        out.writeDouble(m11);
        out.writeDouble(m20);
        out.writeDouble(m21);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        m00 = in.readDouble();
        m01 = in.readDouble();
        m10 = in.readDouble();
        m11 = in.readDouble();
        m20 = in.readDouble();
        m21 = in.readDouble();
    }

    /**
     * Apply a rotation transformation to this matrix by rotating the given amount of radians.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>
     * , the rotation will be applied first!
     * 
     * @param ang
     *            the angle in radians
     * @return this
     */
    public Matrix3x2d rotate(double ang) {
        return rotate(ang, this);
    }

    /**
     * Apply a rotation transformation to this matrix by rotating the given amount of radians and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the rotation will be applied first!
     * 
     * @param ang
     *            the angle in radians
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix3x2d rotate(double ang, Matrix3x2d dest) {
        double cos = Math.cos(ang);
        double sin = Math.sin(ang);
        double rm00 = cos;
        double rm01 = sin;
        double rm10 = -sin;
        double rm11 = cos;
        double nm00 = m00 * rm00 + m10 * rm01;
        double nm01 = m01 * rm00 + m11 * rm01;
        dest.m10 = m00 * rm10 + m10 * rm11;
        dest.m11 = m01 * rm10 + m11 * rm11;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m20 = m20;
        dest.m21 = m21;
        return dest;
    }

    /**
     * Apply a rotation transformation to this matrix by rotating the given amount of radians about
     * the specified rotation center <tt>(x, y)</tt>.
     * <p>
     * This method is equivalent to calling: <tt>translate(x, y).rotate(ang).translate(-x, -y)</tt>
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the rotation will be applied first!
     * 
     * @see #translate(double, double)
     * @see #rotate(double)
     * 
     * @param ang
     *            the angle in radians
     * @param x
     *            the x component of the rotation center
     * @param y
     *            the y component of the rotation center
     * @return dest
     */
    public Matrix3x2d rotateAbout(double ang, double x, double y) {
        return rotateAbout(ang, x, y, this);
    }

    /**
     * Apply a rotation transformation to this matrix by rotating the given amount of radians about
     * the specified rotation center <tt>(x, y)</tt> and store the result in <code>dest</code>.
     * <p>
     * This method is equivalent to calling: <tt>translate(x, y, dest).rotate(ang).translate(-x, -y)</tt>
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the rotation will be applied first!
     * 
     * @see #translate(double, double, Matrix3x2d)
     * @see #rotate(double, Matrix3x2d)
     * 
     * @param ang
     *            the angle in radians
     * @param x
     *            the x component of the rotation center
     * @param y
     *            the y component of the rotation center
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix3x2d rotateAbout(double ang, double x, double y, Matrix3x2d dest) {
        double tm20 = m00 * x + m10 * y + m20;
        double tm21 = m01 * x + m11 * y + m21;
        double cos = Math.cos(ang);
        double sin = Math.sin(ang);
        double nm00 = m00 * cos + m10 * sin;
        double nm01 = m01 * cos + m11 * sin;
        dest.m10 = m00 * -sin + m10 * cos;
        dest.m11 = m01 * -sin + m11 * cos;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m20 = dest.m00 * -x + dest.m10 * -y + tm20;
        dest.m21 = dest.m01 * -x + dest.m11 * -y + tm21;
        return dest;
    }

    /**
     * Apply a rotation transformation to this matrix that rotates the given normalized <code>fromDir</code> direction vector
     * to point along the normalized <code>toDir</code>, and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the rotation will be applied first!
     * 
     * @param fromDir
     *            the normalized direction which should be rotate to point along <code>toDir</code>
     * @param toDir
     *            the normalized destination direction
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix3x2d rotateTo(Vector2d fromDir, Vector2d toDir, Matrix3x2d dest) {
        double dot = fromDir.x * toDir.x + fromDir.y * toDir.y;
        double det = fromDir.x * toDir.y - fromDir.y * toDir.x;
        double rm00 = dot;
        double rm01 = det;
        double rm10 = -det;
        double rm11 = dot;
        double nm00 = m00 * rm00 + m10 * rm01;
        double nm01 = m01 * rm00 + m11 * rm01;
        dest.m10 = m00 * rm10 + m10 * rm11;
        dest.m11 = m01 * rm10 + m11 * rm11;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m20 = m20;
        dest.m21 = m21;
        return dest;
    }

    /**
     * Apply a rotation transformation to this matrix that rotates the given normalized <code>fromDir</code> direction vector
     * to point along the normalized <code>toDir</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the rotation will be applied first!
     * 
     * @param fromDir
     *            the normalized direction which should be rotate to point along <code>toDir</code>
     * @param toDir
     *            the normalized destination direction
     * @return this
     */
    public Matrix3x2d rotateTo(Vector2d fromDir, Vector2d toDir) {
        return rotateTo(fromDir, toDir, this);
    }

    /**
     * Apply a "view" transformation to this matrix that maps the given <tt>(left, bottom)</tt> and
     * <tt>(right, top)</tt> corners to <tt>(-1, -1)</tt> and <tt>(1, 1)</tt> respectively and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * 
     * @see #setView(double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left view edge
     * @param right
     *            the distance from the center to the right view edge
     * @param bottom
     *            the distance from the center to the bottom view edge
     * @param top
     *            the distance from the center to the top view edge
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix3x2d view(double left, double right, double bottom, double top, Matrix3x2d dest) {
        double rm00 = 2.0 / (right - left);
        double rm11 = 2.0 / (top - bottom);
        double rm20 = (left + right) / (left - right);
        double rm21 = (bottom + top) / (bottom - top);
        dest.m20 = m00 * rm20 + m10 * rm21 + m20;
        dest.m21 = m01 * rm20 + m11 * rm21 + m21;
        dest.m00 = m00 * rm00;
        dest.m01 = m01 * rm00;
        dest.m10 = m10 * rm11;
        dest.m11 = m11 * rm11;
        return dest;
    }

    /**
     * Apply a "view" transformation to this matrix that maps the given <tt>(left, bottom)</tt> and
     * <tt>(right, top)</tt> corners to <tt>(-1, -1)</tt> and <tt>(1, 1)</tt> respectively.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * 
     * @see #setView(double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left view edge
     * @param right
     *            the distance from the center to the right view edge
     * @param bottom
     *            the distance from the center to the bottom view edge
     * @param top
     *            the distance from the center to the top view edge
     * @return this
     */
    public Matrix3x2d view(double left, double right, double bottom, double top) {
        return view(left, right, bottom, top, this);
    }

    /**
     * Set this matrix to define a "view" transformation that maps the given <tt>(left, bottom)</tt> and
     * <tt>(right, top)</tt> corners to <tt>(-1, -1)</tt> and <tt>(1, 1)</tt> respectively.
     * 
     * @see #view(double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left view edge
     * @param right
     *            the distance from the center to the right view edge
     * @param bottom
     *            the distance from the center to the bottom view edge
     * @param top
     *            the distance from the center to the top view edge
     * @return this
     */
    public Matrix3x2d setView(double left, double right, double bottom, double top) {
        m00 = 2.0 / (right - left);
        m01 = 0.0;
        m10 = 0.0;
        m11 = 2.0 / (top - bottom);
        m20 = (left + right) / (left - right);
        m21 = (bottom + top) / (bottom - top);
        return this;
    }

    /**
     * Obtain the position that gets transformed to the origin by <code>this</code> matrix.
     * This can be used to get the position of the "camera" from a given <i>view</i> transformation matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix3x2d inv = new Matrix3x2d(this).invert();
     * inv.transform(origin.set(0, 0));
     * </pre>
     * 
     * @param origin
     *          will hold the position transformed to the origin
     * @return origin
     */
    public Vector2d origin(Vector2d origin) {
        double s = 1.0 / (m00 * m11 - m01 * m10);
        origin.x = (m10 * m21 - m20 * m11) * s;
        origin.y = (m20 * m01 - m00 * m21) * s;
        return origin;
    }

    /**
     * Obtain the extents of the view transformation of <code>this</code> matrix and store it in <code>area</code>.
     * This can be used to determine which region of the screen (i.e. the NDC space) is covered by the view.
     * 
     * @param area
     *          will hold the view area as <tt>[minX, minY, maxX, maxY]</tt>
     * @return area
     */
    public double[] viewArea(double[] area) {
        double s = 1.0 / (m00 * m11 - m01 * m10);
        double rm00 =  m11 * s;
        double rm01 = -m01 * s;
        double rm10 = -m10 * s;
        double rm11 =  m00 * s;
        double rm20 = (m10 * m21 - m20 * m11) * s;
        double rm21 = (m20 * m01 - m00 * m21) * s;
        double nxnyX = -rm00 - rm10;
        double nxnyY = -rm01 - rm11;
        double pxnyX =  rm00 - rm10;
        double pxnyY =  rm01 - rm11;
        double nxpyX = -rm00 + rm10;
        double nxpyY = -rm01 + rm11;
        double pxpyX =  rm00 + rm10;
        double pxpyY =  rm01 + rm11;
        double minX = nxnyX;
        minX = minX < nxpyX ? minX : nxpyX;
        minX = minX < pxnyX ? minX : pxnyX;
        minX = minX < pxpyX ? minX : pxpyX;
        double minY = nxnyY;
        minY = minY < nxpyY ? minY : nxpyY;
        minY = minY < pxnyY ? minY : pxnyY;
        minY = minY < pxpyY ? minY : pxpyY;
        double maxX = nxnyX;
        maxX = maxX > nxpyX ? maxX : nxpyX;
        maxX = maxX > pxnyX ? maxX : pxnyX;
        maxX = maxX > pxpyX ? maxX : pxpyX;
        double maxY = nxnyY;
        maxY = maxY > nxpyY ? maxY : nxpyY;
        maxY = maxY > pxnyY ? maxY : pxnyY;
        maxY = maxY > pxpyY ? maxY : pxpyY;
        area[0] = minX + rm20;
        area[1] = minY + rm21;
        area[2] = maxX + rm20;
        area[3] = maxY + rm21;
        return area;
    }

    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#positiveX(org.joml.Vector2d)
     */
    public Vector2d positiveX(Vector2d dir) {
        double s = m00 * m11 - m01 * m10;
        s = 1.0 / s;
        dir.x =  m11 * s;
        dir.y = -m01 * s;
        dir.normalize();
        return dir;
    }

    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#normalizedPositiveX(org.joml.Vector2d)
     */
    public Vector2d normalizedPositiveX(Vector2d dir) {
        dir.x =  m11;
        dir.y = -m01;
        return dir;
    }

    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#positiveY(org.joml.Vector2d)
     */
    public Vector2d positiveY(Vector2d dir) {
        double s = m00 * m11 - m01 * m10;
        s = 1.0 / s;
        dir.x = -m10 * s;
        dir.y =  m00 * s;
        dir.normalize();
        return dir;
    }

    /* (non-Javadoc)
     * @see org.joml.Matrix3x2dc#normalizedPositiveY(org.joml.Vector2d)
     */
    public Vector2d normalizedPositiveY(Vector2d dir) {
        dir.x = -m10;
        dir.y =  m00;
        return dir;
    }

    /**
     * Unproject the given window coordinates <tt>(winX, winY)</tt> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by the inverse of <code>this</code> matrix.  
     * <p>
     * As a necessary computation step for unprojecting, this method computes the inverse of <code>this</code> matrix.
     * In order to avoid computing the matrix inverse with every invocation, the inverse of <code>this</code> matrix can be built
     * once outside using {@link #invert(Matrix3x2d)} and then the method {@link #unprojectInv(double, double, int[], Vector2d) unprojectInv()} can be invoked on it.
     * 
     * @see #unprojectInv(double, double, int[], Vector2d)
     * @see #invert(Matrix3x2d)
     * 
     * @param winX
     *          the x-coordinate in window coordinates (pixels)
     * @param winY
     *          the y-coordinate in window coordinates (pixels)
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector2d unproject(double winX, double winY, int[] viewport, Vector2d dest) {
        double s = 1.0 / (m00 * m11 - m01 * m10);
        double im00 =  m11 * s;
        double im01 = -m01 * s;
        double im10 = -m10 * s;
        double im11 =  m00 * s;
        double im20 = (m10 * m21 - m20 * m11) * s;
        double im21 = (m20 * m01 - m00 * m21) * s;
        double ndcX = (winX-viewport[0])/viewport[2]*2.0-1.0;
        double ndcY = (winY-viewport[1])/viewport[3]*2.0-1.0;
        dest.x = im00 * ndcX + im10 * ndcY + im20;
        dest.y = im01 * ndcX + im11 * ndcY + im21;
        return dest;
    }

    /**
     * Unproject the given window coordinates <tt>(winX, winY)</tt> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method differs from {@link #unproject(double, double, int[], Vector2d) unproject()} 
     * in that it assumes that <code>this</code> is already the inverse matrix of the original projection matrix.
     * It exists to avoid recomputing the matrix inverse with every invocation.
     * 
     * @see #unproject(double, double, int[], Vector2d)
     * 
     * @param winX
     *          the x-coordinate in window coordinates (pixels)
     * @param winY
     *          the y-coordinate in window coordinates (pixels)
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector2d unprojectInv(double winX, double winY, int[] viewport, Vector2d dest) {
        double ndcX = (winX-viewport[0])/viewport[2]*2.0-1.0;
        double ndcY = (winY-viewport[1])/viewport[3]*2.0-1.0;
        dest.x = m00 * ndcX + m10 * ndcY + m20;
        dest.y = m01 * ndcX + m11 * ndcY + m21;
        return dest;
    }

    /**
     * Create a new immutable view of this {@link Matrix3x2d}.
     * <p>
     * The observable state of the returned object is the same as that of <code>this</code>, but casting
     * the returned object to Matrix3x2d will not be possible.
     * <p>
     * This method allocates a new instance of a class implementing Matrix3x2dc on every call.
     * 
     * @return the immutable instance
     */
    public Matrix3x2dc toImmutable() {
        if (!Options.DEBUG)
            return this;
        return new Proxy(this);
    }

}
