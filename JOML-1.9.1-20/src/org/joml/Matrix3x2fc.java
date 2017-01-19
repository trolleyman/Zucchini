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

//#ifdef __HAS_NIO__
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
//#endif

//#ifdef __GWT__
import com.google.gwt.typedarrays.shared.Float32Array;
//#endif

/**
 * Contains the definition of a 3x2 matrix of floats, and associated functions to transform
 * it. The matrix is column-major to match OpenGL's interpretation, and it looks like this:
 * <p>
 *      m00  m10  m20<br>
 *      m01  m11  m21<br>
 * 
 * @author Kai Burjack
 */
public interface Matrix3x2fc {

    /**
     * Return the value of the matrix element at column 0 and row 0.
     * 
     * @return the value of the matrix element
     */
    float m00();

    /**
     * Return the value of the matrix element at column 0 and row 1.
     * 
     * @return the value of the matrix element
     */
    float m01();

    /**
     * Return the value of the matrix element at column 1 and row 0.
     * 
     * @return the value of the matrix element
     */
    float m10();

    /**
     * Return the value of the matrix element at column 1 and row 1.
     * 
     * @return the value of the matrix element
     */
    float m11();

    /**
     * Return the value of the matrix element at column 2 and row 0.
     * 
     * @return the value of the matrix element
     */
    float m20();

    /**
     * Return the value of the matrix element at column 2 and row 1.
     * 
     * @return the value of the matrix element
     */
    float m21();

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
    Matrix3x2f mul(Matrix3x2fc right, Matrix3x2f dest);

    /**
     * Return the determinant of this matrix.
     * 
     * @return the determinant
     */
    float determinant();

    /**
     * Invert the <code>this</code> matrix by assuming a third row in this matrix of <tt>(0, 0, 1)</tt>
     * and store the result in <code>dest</code>.
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    Matrix3x2f invert(Matrix3x2f dest);

    /**
     * Apply a translation to this matrix by translating by the given number of units in x and y and store the result
     * in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * 
     * @param x
     *          the offset to translate in x
     * @param y
     *          the offset to translate in y
     * @param dest
     *          will hold the result
     * @return dest
     */
    Matrix3x2f translate(float x, float y, Matrix3x2f dest);

    /**
     * Apply a translation to this matrix by translating by the given number of units in x and y, and
     * store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * 
     * @param offset
     *          the offset to translate
     * @param dest
     *          will hold the result
     * @return dest
     */
    Matrix3x2f translate(Vector2f offset, Matrix3x2f dest);

    /**
     * Get the current values of <code>this</code> matrix and store them into
     * <code>dest</code>.
     * 
     * @param dest
     *          the destination matrix
     * @return dest
     */
    Matrix3x2f get(Matrix3x2f dest);

//#ifdef __GWT__
    /**
     * Store this matrix in column-major order into the supplied {@link Float32Array}.
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    Float32Array get(Float32Array buffer);
    /**
     * Store this matrix in column-major order into the supplied {@link Float32Array}
     * at the given index.
     * 
     * @param index
     *            the index at which to store this matrix in the supplied Float32Array 
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    Float32Array get(int index, Float32Array buffer);
//#endif

//#ifdef __HAS_NIO__
    /**
     * Store this matrix in column-major order into the supplied {@link FloatBuffer} at the current
     * buffer {@link FloatBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given FloatBuffer.
     * <p>
     * In order to specify the offset into the FloatBuffer at which
     * the matrix is stored, use {@link #get(int, FloatBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #get(int, FloatBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    FloatBuffer get(FloatBuffer buffer);

    /**
     * Store this matrix in column-major order into the supplied {@link FloatBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given FloatBuffer.
     * 
     * @param index
     *            the absolute position into the FloatBuffer
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    FloatBuffer get(int index, FloatBuffer buffer);

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
    ByteBuffer get(ByteBuffer buffer);

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
    ByteBuffer get(int index, ByteBuffer buffer);

    /**
     * Store this matrix as an equivalent 4x4 matrix in column-major order into the supplied {@link FloatBuffer} at the current
     * buffer {@link FloatBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given FloatBuffer.
     * <p>
     * In order to specify the offset into the FloatBuffer at which
     * the matrix is stored, use {@link #get4x4(int, FloatBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #get4x4(int, FloatBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    FloatBuffer get4x4(FloatBuffer buffer);

    /**
     * Store this matrix as an equivalent 4x4 matrix in column-major order into the supplied {@link FloatBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given FloatBuffer.
     * 
     * @param index
     *            the absolute position into the FloatBuffer
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    FloatBuffer get4x4(int index, FloatBuffer buffer);

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
    ByteBuffer get4x4(ByteBuffer buffer);

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
    ByteBuffer get4x4(int index, ByteBuffer buffer);
//#endif

    /**
     * Store this matrix into the supplied float array in column-major order at the given offset.
     * 
     * @param arr
     *          the array to write the matrix values into
     * @param offset
     *          the offset into the array
     * @return the passed in array
     */
    float[] get(float[] arr, int offset);

    /**
     * Store this matrix into the supplied float array in column-major order.
     * <p>
     * In order to specify an explicit offset into the array, use the method {@link #get(float[], int)}.
     * 
     * @see #get(float[], int)
     * 
     * @param arr
     *          the array to write the matrix values into
     * @return the passed in array
     */
    float[] get(float[] arr);

    /**
     * Store this matrix as an equivalent 4x4 matrix into the supplied float array in column-major order at the given offset.
     * 
     * @param arr
     *          the array to write the matrix values into
     * @param offset
     *          the offset into the array
     * @return the passed in array
     */
    float[] get4x4(float[] arr, int offset);

    /**
     * Store this matrix as an equivalent 4x4 matrix into the supplied float array in column-major order.
     * <p>
     * In order to specify an explicit offset into the array, use the method {@link #get4x4(float[], int)}.
     * 
     * @see #get4x4(float[], int)
     * 
     * @param arr
     *          the array to write the matrix values into
     * @return the passed in array
     */
    float[] get4x4(float[] arr);

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
    Matrix3x2f scale(float x, float y, Matrix3x2f dest);

    /**
     * Pre-multiply scaling to <code>this</code> matrix by scaling the base axes by the given sx and
     * sy factors while using the given <tt>(ox, oy)</tt> as the scaling origin,
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>S * M</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>S * M * v</code>
     * , the scaling will be applied last!
     * <p>
     * This method is equivalent to calling: <tt>new Matrix3x2f().translate(ox, oy).scale(sx, sy).translate(-ox, -oy).mul(this, dest)</tt>
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
    Matrix3x2f scaleAroundLocal(float sx, float sy, float ox, float oy, Matrix3x2f dest);

    /**
     * Pre-multiply scaling to this matrix by scaling the base axes by the given <code>factor</code>
     * while using <tt>(ox, oy)</tt> as the scaling origin,
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>S * M</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>S * M * v</code>, the
     * scaling will be applied last!
     * <p>
     * This method is equivalent to calling: <tt>new Matrix3x2f().translate(ox, oy).scale(factor).translate(-ox, -oy).mul(this, dest)</tt>
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
    Matrix3x2f scaleAroundLocal(float factor, float ox, float oy, Matrix3x2f dest);

    /**
     * Apply scaling to this matrix by uniformly scaling the two base axes by the given <code>xy</code> factor
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the scaling will be applied first!
     * 
     * @see #scale(float, float, Matrix3x2f)
     * 
     * @param xy
     *            the factor for the two components
     * @param dest
     *            will hold the result
     * @return dest
     */
    Matrix3x2f scale(float xy, Matrix3x2f dest);

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
    Matrix3x2f scaleAround(float sx, float sy, float ox, float oy, Matrix3x2f dest);

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
    Matrix3x2f scaleAround(float factor, float ox, float oy, Matrix3x2f dest);

    /**
     * Transform/multiply the given vector by this matrix by assuming a third row in this matrix of <tt>(0, 0, 1)</tt>
     * and store the result in that vector.
     * 
     * @see Vector3f#mul(Matrix3x2fc)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    Vector3f transform(Vector3f v);

    /**
     * Transform/multiply the given vector by this matrix and store the result in <code>dest</code>.
     * 
     * @see Vector3f#mul(Matrix3x2fc, Vector3f)
     * 
     * @param v
     *          the vector to transform
     * @param dest
     *          will contain the result
     * @return dest
     */
    Vector3f transform(Vector3f v, Vector3f dest);

    /**
     * Transform/multiply the given 2D-vector, as if it was a 3D-vector with z=1, by
     * this matrix and store the result in that vector.
     * <p>
     * The given 2D-vector is treated as a 3D-vector with its z-component being 1.0, so it
     * will represent a position/location in 2D-space rather than a direction.
     * <p>
     * In order to store the result in another vector, use {@link #transformPosition(Vector2f, Vector2f)}.
     * 
     * @see #transformPosition(Vector2f, Vector2f)
     * @see #transform(Vector3f)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    Vector2f transformPosition(Vector2f v);

    /**
     * Transform/multiply the given 2D-vector, as if it was a 3D-vector with z=1, by
     * this matrix and store the result in <code>dest</code>.
     * <p>
     * The given 2D-vector is treated as a 3D-vector with its z-component being 1.0, so it
     * will represent a position/location in 2D-space rather than a direction.
     * <p>
     * In order to store the result in the same vector, use {@link #transformPosition(Vector2f)}.
     * 
     * @see #transformPosition(Vector2f)
     * @see #transform(Vector3f, Vector3f)
     * 
     * @param v
     *          the vector to transform
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2f transformPosition(Vector2f v, Vector2f dest);

    /**
     * Transform/multiply the given 2D-vector, as if it was a 3D-vector with z=0, by
     * this matrix and store the result in that vector.
     * <p>
     * The given 2D-vector is treated as a 3D-vector with its z-component being <tt>0.0</tt>, so it
     * will represent a direction in 2D-space rather than a position. This method will therefore
     * not take the translation part of the matrix into account.
     * <p>
     * In order to store the result in another vector, use {@link #transformDirection(Vector2f, Vector2f)}.
     * 
     * @see #transformDirection(Vector2f, Vector2f)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    Vector2f transformDirection(Vector2f v);

    /**
     * Transform/multiply the given 2D-vector, as if it was a 3D-vector with z=0, by
     * this matrix and store the result in <code>dest</code>.
     * <p>
     * The given 2D-vector is treated as a 3D-vector with its z-component being <tt>0.0</tt>, so it
     * will represent a direction in 2D-space rather than a position. This method will therefore
     * not take the translation part of the matrix into account.
     * <p>
     * In order to store the result in the same vector, use {@link #transformDirection(Vector2f)}.
     * 
     * @see #transformDirection(Vector2f)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2f transformDirection(Vector2f v, Vector2f dest);

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
    Matrix3x2f rotate(float ang, Matrix3x2f dest);

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
     * @see #translate(float, float, Matrix3x2f)
     * @see #rotate(float, Matrix3x2f)
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
    Matrix3x2f rotateAbout(float ang, float x, float y, Matrix3x2f dest);

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
    Matrix3x2f rotateTo(Vector2f fromDir, Vector2f toDir, Matrix3x2f dest);

    /**
     * Apply a "view" transformation to this matrix that maps the given <tt>(left, bottom)</tt> and
     * <tt>(right, top)</tt> corners to <tt>(-1, -1)</tt> and <tt>(1, 1)</tt> respectively and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
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
    Matrix3x2f view(float left, float right, float bottom, float top, Matrix3x2f dest);

    /**
     * Obtain the position that gets transformed to the origin by <code>this</code> matrix.
     * This can be used to get the position of the "camera" from a given <i>view</i> transformation matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix3x2f inv = new Matrix3x2f(this).invertAffine();
     * inv.transform(origin.set(0, 0));
     * </pre>
     * 
     * @param origin
     *          will hold the position transformed to the origin
     * @return origin
     */
    Vector2f origin(Vector2f origin);

    /**
     * Obtain the extents of the view transformation of <code>this</code> matrix and store it in <code>area</code>.
     * This can be used to determine which region of the screen (i.e. the NDC space) is covered by the view.
     * 
     * @param area
     *          will hold the view area as <tt>[minX, minY, maxX, maxY]</tt>
     * @return area
     */
    float[] viewArea(float[] area);

    /**
     * Obtain the direction of <tt>+X</tt> before the transformation represented by <code>this</code> matrix is applied.
     * <p>
     * This method uses the rotation component of the left 2x2 submatrix to obtain the direction 
     * that is transformed to <tt>+X</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix3x2f inv = new Matrix3x2f(this).invert();
     * inv.transformDirection(dir.set(1, 0)).normalize();
     * </pre>
     * If <code>this</code> is already an orthogonal matrix, then consider using {@link #normalizedPositiveX(Vector2f)} instead.
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+X</tt>
     * @return dir
     */
    Vector2f positiveX(Vector2f dir);

    /**
     * Obtain the direction of <tt>+X</tt> before the transformation represented by <code>this</code> <i>orthogonal</i> matrix is applied.
     * This method only produces correct results if <code>this</code> is an <i>orthogonal</i> matrix.
     * <p>
     * This method uses the rotation component of the left 2x2 submatrix to obtain the direction 
     * that is transformed to <tt>+X</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix3x2f inv = new Matrix3x2f(this).transpose();
     * inv.transformDirection(dir.set(1, 0));
     * </pre>
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+X</tt>
     * @return dir
     */
    Vector2f normalizedPositiveX(Vector2f dir);

    /**
     * Obtain the direction of <tt>+Y</tt> before the transformation represented by <code>this</code> matrix is applied.
     * <p>
     * This method uses the rotation component of the left 2x2 submatrix to obtain the direction 
     * that is transformed to <tt>+Y</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix3x2f inv = new Matrix3x2f(this).invert();
     * inv.transformDirection(dir.set(0, 1)).normalize();
     * </pre>
     * If <code>this</code> is already an orthogonal matrix, then consider using {@link #normalizedPositiveY(Vector2f)} instead.
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+Y</tt>
     * @return dir
     */
    Vector2f positiveY(Vector2f dir);

    /**
     * Obtain the direction of <tt>+Y</tt> before the transformation represented by <code>this</code> <i>orthogonal</i> matrix is applied.
     * This method only produces correct results if <code>this</code> is an <i>orthogonal</i> matrix.
     * <p>
     * This method uses the rotation component of the left 2x2 submatrix to obtain the direction 
     * that is transformed to <tt>+Y</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix3x2f inv = new Matrix3x2f(this).transpose();
     * inv.transformDirection(dir.set(0, 1));
     * </pre>
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+Y</tt>
     * @return dir
     */
    Vector2f normalizedPositiveY(Vector2f dir);

    /**
     * Unproject the given window coordinates <tt>(winX, winY)</tt> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by the inverse of <code>this</code> matrix.  
     * <p>
     * As a necessary computation step for unprojecting, this method computes the inverse of <code>this</code> matrix.
     * In order to avoid computing the matrix inverse with every invocation, the inverse of <code>this</code> matrix can be built
     * once outside using {@link #invert(Matrix3x2f)} and then the method {@link #unprojectInv(float, float, int[], Vector2f) unprojectInv()} can be invoked on it.
     * 
     * @see #unprojectInv(float, float, int[], Vector2f)
     * @see #invert(Matrix3x2f)
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
    Vector2f unproject(float winX, float winY, int[] viewport, Vector2f dest);

    /**
     * Unproject the given window coordinates <tt>(winX, winY)</tt> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method differs from {@link #unproject(float, float, int[], Vector2f) unproject()} 
     * in that it assumes that <code>this</code> is already the inverse matrix of the original projection matrix.
     * It exists to avoid recomputing the matrix inverse with every invocation.
     * 
     * @see #unproject(float, float, int[], Vector2f)
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
    Vector2f unprojectInv(float winX, float winY, int[] viewport, Vector2f dest);

}
