/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2000-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.InputStream;
import java.io.Reader;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.exception.CDKException;

/**
 * This class is the interface that all IO readers should implement.
 * Programs need only care about this interface for any kind of IO.
 * Currently, database IO and file IO is supported.
 *
 * <p>The easiest way to implement a new ChemObjectReader is to
 * subclass the DefaultChemObjectReader.
 *
 * <p>I don't know how this should be enforced, but a Reader should
 * also provide an empty constructor so that ClassLoader/getInstance()
 * can be used to instantiate a ChemObjectReader.
 *
 * @cdk.module io
 * @cdk.svnrev $Revision$
 *
 * @see DefaultChemObjectReader
 *
 * @author Egon Willighagen <egonw@users.sf.net>
 **/
public interface IChemObjectReader extends IChemObjectIO {

	public enum Mode {
		/** Only fail on serious format problems */
		RELAXED,
		/** Fail on any format problem */
		STRICT
	}
	
    /**
     * Reads an IChemObject of type "object" from input. The constructor
     * of the actual implementation may take a Reader as input to get
     * a very flexible reader that can read from string, files, etc.
     * 
     * @param  object    the type of object to return
     * @return returns an object of that contains the content (or 
     *         part) of the input content
     *
     * @exception CDKException it is thrown if
     *            the type of information is not available from 
     *            the input
     **/
    public IChemObject read(IChemObject object) throws CDKException;

    /**
     * Sets the Reader from which this ChemObjectReader should read
     * the contents.
     */
    public void setReader(Reader reader) throws CDKException;

    /**
     * Sets the InputStream from which this ChemObjectReader should read
     * the contents.
     */
    public void setReader(InputStream reader) throws CDKException;
    
    /**
     * Sets the reader mode. If Mode.STRICT, then the reader will fail on
     * any problem in the format of the read file, instead of trying to
     * recover from that.
     * 
     * @param mode
     */
    public void setReaderMode(Mode mode);
}
