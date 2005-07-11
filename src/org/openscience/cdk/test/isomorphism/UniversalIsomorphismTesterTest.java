/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CKD) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.test.isomorphism;

import java.io.*;
import java.util.Vector;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.SymbolQueryAtom;
import org.openscience.cdk.isomorphism.matchers.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.mcss.*;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;

/**
 * @cdk.module test
 * @cdk.require java1.4+
 */
public class UniversalIsomorphismTesterTest extends CDKTestCase
{
	
	boolean standAlone = false;
	
	public UniversalIsomorphismTesterTest(String name)
	{
		super(name);
	}

	
	public static Test suite() {
		return new TestSuite(UniversalIsomorphismTesterTest.class);
	}

	public void test1() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeAlphaPinene();
		AtomContainer frag1 = MoleculeFactory.makeCyclohexene(); //one double bond in ring
		HueckelAromaticityDetector.detectAromaticity(mol);
		HueckelAromaticityDetector.detectAromaticity(frag1);
		
		if(standAlone) {
			System.out.println("Cyclohexene is a subgraph of alpha-Pinen: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
        } else {
            assertTrue(UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }
	}

	public void test2() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeAlphaPinene();
		AtomContainer frag1 = MoleculeFactory.makeCyclohexane(); // no double bond in ring		
		HueckelAromaticityDetector.detectAromaticity(mol);
		HueckelAromaticityDetector.detectAromaticity(frag1);
        
		if(standAlone){
			System.out.println("Cyclohexane is a subgraph of alpha-Pinen: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
		} else {
            assertTrue(!UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }
	}

	public void test3() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeIndole();
		AtomContainer frag1 = MoleculeFactory.makePyrrole(); 
		HueckelAromaticityDetector.detectAromaticity(mol);
		HueckelAromaticityDetector.detectAromaticity(frag1);

		if(standAlone) {
			System.out.println("Pyrrole is a subgraph of Indole: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
        } else {
            assertTrue(UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }
	}
	
	public void testBasicQueryAtomContainer() throws Exception {
		SmilesParser sp = new SmilesParser();
        AtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        AtomContainer SMILESquery = sp.parseSmiles("CC"); // acetic acid anhydride
        QueryAtomContainer query = QueryAtomContainerCreator.createBasicQueryContainer(SMILESquery);
        
        assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }
	
	public void testGetSubgraphAtomsMaps() throws java.lang.Exception
	{
		int[] result1 = {6, 5, 7, 8, 0};
		int[] result2 = {3, 4, 2, 1, 0};
		
		AtomContainer mol = MoleculeFactory.makeIndole();
		AtomContainer frag1 = MoleculeFactory.makePyrrole(); 
		HueckelAromaticityDetector.detectAromaticity(mol);
		HueckelAromaticityDetector.detectAromaticity(frag1);
		List list = UniversalIsomorphismTester.getSubgraphAtomsMaps(mol, frag1);
		List first = (List)list.get(0);
		for (int i = 0; i < first.size(); i++) {
			RMap rmap = (RMap)first.get(i);
			assertEquals(rmap.getId1(), result1[i]);
			assertEquals(rmap.getId2(), result2[i]);
		}
	}
	
    public void testQueryAtomContainer() throws CDKException
    {
        String molfile = "data/mdl/decalin.mol";
        String queryfile = "data/mdl/decalin.mol";
        Molecule mol = new Molecule();
        Molecule temp = new Molecule();
        QueryAtomContainer query1 = null;
        QueryAtomContainer query2 = null;
        
        try {
            MDLReader reader = new MDLReader(new FileReader(molfile));
            reader.read(mol);
            reader = new MDLReader(new FileReader(queryfile));
            reader.read(temp);
            query1 = QueryAtomContainerCreator.createBasicQueryContainer(temp);
            
            SmilesParser sp = new SmilesParser();
            AtomContainer atomContainer = sp.parseSmiles("C1CCCCC1");
            query2 = QueryAtomContainerCreator.createBasicQueryContainer(atomContainer);
            
        } catch (Exception ex) {
            System.err.println("testQueryAtomContainer: " + ex.getMessage());
        }
        
        List list = UniversalIsomorphismTester.getSubgraphMap(mol, query1);
        assertEquals(11, list.size());
        
        list = UniversalIsomorphismTester.getSubgraphMap(mol, query2);
        assertEquals(6, list.size());
        
    }
    
    public void testSFBug1110537()  throws CDKException{
        String file1 = "data/mdl/5SD.mol";
        String file2 = "data/mdl/ADN.mol";
        Molecule mol1 = new Molecule();
        Molecule mol2 = new Molecule();
        
        try {
            new MDLReader(new FileReader(file1)).read(mol1);
            new MDLReader(new FileReader(file2)).read(mol2);
        } catch (Exception ex) {
            System.err.println("testQueryAtomContainer: " + ex.getMessage());
            fail(ex.getMessage());
        }
        
        List list = UniversalIsomorphismTester.getOverlaps(mol1, mol2);
        assertEquals(1, list.size());
        assertEquals(11, ((AtomContainer)list.get(0)).getAtomCount());
        
        list = UniversalIsomorphismTester.getOverlaps(mol2, mol1);
        assertEquals(1, list.size());
        assertEquals(11, ((AtomContainer)list.get(0)).getAtomCount());
    }
    
    public void testSFBug1208740() throws CDKException {
        String file1 = "data/mdl/bug1208740_1.mol";
        String file2 = "data/mdl/bug1208740_2.mol";
        Molecule mol1 = new Molecule();
        Molecule mol2 = new Molecule();
        
        try {
            new MDLReader(new FileReader(file1)).read(mol1);
            new MDLReader(new FileReader(file2)).read(mol2);
        } catch (Exception ex) {
            System.err.println("testSFBug1208740: " + ex.getMessage());
            fail(ex.getMessage());
        }
        
        List list = UniversalIsomorphismTester.getOverlaps(mol1, mol2);
        assertEquals(5, list.size());
        list = UniversalIsomorphismTester.getOverlaps(mol2, mol1);
        assertEquals(5, list.size());
        
        // now apply aromaticity detection, then 8 overlaps should be found
        // see cdk-user@list.sf.net on 2005-06-16
        try {
            HueckelAromaticityDetector.detectAromaticity(mol1, true);
            HueckelAromaticityDetector.detectAromaticity(mol2, true);
        } catch (Exception ex) {
            System.err.println("Problem detecting aromaticity in testSFBug1208740: " + ex.getMessage());
            fail(ex.getMessage());
        }
        list = UniversalIsomorphismTester.getOverlaps(mol1, mol2);
        assertEquals(8, list.size());
        list = UniversalIsomorphismTester.getOverlaps(mol2, mol1);
        assertEquals(8, list.size());
    }
    
    public void testSFBug999330() throws CDKException {
        String file1 = "data/mdl/5SD.mol";
        String file2 = "data/mdl/ADN.mol";
        Molecule mol1 = new Molecule();
        Molecule mol2 = new Molecule();
        
        try {
            new MDLReader(new FileReader(file1)).read(mol1);
            new MDLReader(new FileReader(file2)).read(mol2);
        } catch (Exception ex) {
            System.err.println("testQueryAtomContainer: " + ex.getMessage());
            fail(ex.getMessage());
        }
        AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(mol2);
        mol2 = new Molecule((AtomContainer)permutor.next());
        
        List list1 = UniversalIsomorphismTester.getOverlaps(mol1, mol2);
        List list2 = UniversalIsomorphismTester.getOverlaps(mol2, mol1);
        assertEquals(1, list1.size());
        assertEquals(1, list2.size());
        assertEquals(((AtomContainer)list1.get(0)).getAtomCount(),
                     ((AtomContainer)list2.get(0)).getAtomCount());
    }
    
	public static void main(String[] args)
	{
		try{
			UniversalIsomorphismTesterTest uitt = new UniversalIsomorphismTesterTest("UniversalIsomorphismTesterTest");
			uitt.standAlone = true;
			uitt.test1();
			uitt.test2();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
}

