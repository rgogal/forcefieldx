//******************************************************************************
//
// File:    SharedObjectArrayReductionBuf_1.java
// Package: edu.rit.mp.buf
// Unit:    Class edu.rit.mp.buf.SharedObjectArrayReductionBuf_1
//
// This Java source file is copyright (C) 2012 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Parallel Java Library ("PJ"). PJ is free
// software; you can redistribute it and/or modify it under the terms of the GNU
// General Public License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// PJ is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// Linking this library statically or dynamically with other modules is making a
// combined work based on this library. Thus, the terms and conditions of the GNU
// General Public License cover the whole combination.
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules, and
// to copy and distribute the resulting executable under terms of your choice,
// provided that you also meet, for each linked independent module, the terms
// and conditions of the license of that module. An independent module is a module
// which is not derived from or based on this library. If you modify this library,
// you may extend this exception to your version of the library, but you are not
// obligated to do so. If you do not wish to do so, delete this exception
// statement from your version.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************
package edu.rit.mp.buf;

import edu.rit.mp.Buf;
import edu.rit.pj.reduction.ObjectOp;
import edu.rit.pj.reduction.Op;
import edu.rit.pj.reduction.SharedObjectArray;
import edu.rit.util.Range;

/**
 * Class SharedObjectArrayReductionBuf_1 provides a reduction buffer for class
 * {@linkplain SharedObjectArrayBuf_1}.
 *
 * @param <T> Data type of the objects in the buffer.
 *
 * @author Alan Kaminsky
 * @version 01-Apr-2012
 */
class SharedObjectArrayReductionBuf_1<T>
        extends SharedObjectArrayBuf_1<T> {

// Hidden data members.
    ObjectOp<T> myOp;
    SharedObjectArrayBuf<T> myBuf;

// Exported constructors.
    /**
     * Construct a new shared object array reduction buffer.
     *
     * @param theArray Shared array.
     * @param theRange Range of array elements to include in the buffer. The
     * stride is assumed to be 1.
     * @param op Binary operation.
     * @param theBuf Underlying shared object array buffer.
     * @exception NullPointerException (unchecked exception) Thrown if
     * <code>op</code> is null.
     */
    public SharedObjectArrayReductionBuf_1(SharedObjectArray<T> theArray,
            Range theRange,
            ObjectOp<T> op,
            SharedObjectArrayBuf<T> theBuf) {
        super(theArray, theRange);
        if (op == null) {
            throw new NullPointerException("SharedObjectArrayReductionBuf_1(): op is null");
        }
        myOp = op;
        myBuf = theBuf;
    }

// Exported operations.
    /**
     * Store the given item in this buffer.
     * <P>
     * The <code>put()</code> method must not block the calling thread; if it does,
     * all message I/O in MP will be blocked.
     *
     * @param i Item index in the range 0 .. <code>length()</code>-1.
     * @param item Item to be stored at index <code>i</code>.
     */
    public void put(int i, T item) {
        myArray.reduce(myArrayOffset + i, item, myOp);
        reset();
        myBuf.reset();
    }

    /**
     * {@inheritDoc}
     *
     * Create a buffer for performing parallel reduction using the given binary
     * operation. The results of the reduction are placed into this buffer.
     * @exception ClassCastException (unchecked exception) Thrown if this
     * buffer's element data type and the given binary operation's argument data
     * type are not the same.
     */
    public Buf getReductionBuf(Op op) {
        throw new UnsupportedOperationException();
    }

}
