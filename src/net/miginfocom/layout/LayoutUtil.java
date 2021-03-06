package net.miginfocom.layout;

import java.beans.*;
import java.io.*;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
/*
 * License (BSD):
 * ==============
 *
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (miglayout (at) miginfocom (dot) com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * Neither the name of the MiG InfoCom AB nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @version 1.0
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 2006-sep-08
 */

/** A utility class that has only static helper methods.
 */
public final class LayoutUtil
{
	@SuppressWarnings("rawtypes")
	private static HashMap<Class, PersistenceDelegate> delegates = null;

	/** A substitute value for aa really large value. Integer.MAX_VALUE is not used since that means a lot of defensive code
	 * for potential overflow must exist in many places. This value is large enough for being unresonable yet it is hard to
	 * overflow.
	 */
	static final int INF = (Integer.MAX_VALUE >> 8) - 100; // To reduce likelyhood of overflow errors when calculating.

	/** Tag int for a value that in considered "not set". Used as "null" element in int arays.
	 */
	static final int NOT_SET = Integer.MIN_VALUE + 12346;   // Magic value...

	// Index for the different sizes
	public static final int MIN = 0;
	public static final int PREF = 1;
	public static final int MAX = 2;

	private static WeakHashMap<Object, String> CR_MAP = null;
	private static Boolean designTime = null;

	private LayoutUtil()
	{
	}

	public static String getVersion()
	{
		return "2.1.2";
	}

	public static void setDesignTime(boolean b)
	{
		try {
			designTime = b ? Boolean.TRUE : Boolean.FALSE;
			Beans.setDesignTime(true);
		} catch(SecurityException e) {
		}
	}

	public static boolean isDesignTime()
	{
		return designTime != null ? designTime.booleanValue() : Beans.isDesignTime();
	}

	/** Associates <code>con</code> with the creation string <code>s</code>. The <code>con</code> object should
	 * probably have an equals method that compares identities or <code>con</code> objects that .equals() will only
	 * be able to have <b>one</b> creation string.
	 * <p>
	 * If {@link LayoutUtil#isDesignTime()} returns <code>false</code> the method does nothing.
	 * @param con The object. if <code>null</code> the method does nothing.
	 * @param s The creation string. if <code>null</code> the method does nothing.
	 */
	static void putCCString(Object con, String s)
	{
		if (CR_MAP == null && LayoutUtil.isDesignTime())
			CR_MAP = new WeakHashMap<Object, String>(64);

		if (CR_MAP != null && con != null && s != null)
			CR_MAP.put(con, s);
	}

	/** Sets/add the persistence delagates to be used for a class.
	 * @param c The class to set the registered deligate for.
	 * @param del The new deligate or <code>null</code> to erase to old one.
	 */
	@SuppressWarnings("rawtypes")
	static synchronized void setDelegate(Class c, PersistenceDelegate del)
	{
		if (LayoutUtil.isDesignTime() == false)
			return;

		if (delegates == null)
			delegates = new HashMap<Class, PersistenceDelegate>(4);

		delegates.put(c, del);

		try {
			Introspector.getBeanInfo(c).getBeanDescriptor().setValue("persistenceDelegate", del);
		} catch (IntrospectionException e1) {
//			e1.printStackTrace();  // No rights?
		}
	}

	/** Returns strings set with {@link #putCCString(Object, String)} or <code>null</code> if nothing is associated or
	 * {@link LayoutUtil#isDesignTime()} returns <code>false</code>.
	 * @param con The constraitn object.
	 * @return The creation string or <code>null</code> if nothing is registered with the <code>con</code> object.
	 */
	static String getCCString(Object con)
	{
		return CR_MAP != null ? CR_MAP.get(con) : null;
	}

	static void throwCC()
	{
		throw new IllegalStateException("setStoreConstraintData(true) must be set for strings to be saved.");
	}

	/** Takes a number on min/preferred/max sizes and resize constraints and returns the calculated sizes which sum should add up to <code>bounds</code>. Whether the sum
	 * will actually equal <code>bounds</code> is dependent om the pref/max sizes and resise constraints.
	 * @param sizes [ix],[MIN][PREF][MAX]. Grid.CompWrap.NOT_SET will be treated as N/A or 0. A "[MIN][PREF][MAX]" array with null elements will be interpreted as very flexible (no bounds)
	 * but if the array itself is null it will not get any size.
	 * @param resConstr Elements can be <code>null</code> and the whole array can be <code>null</code>. <code>null</code> means that the size will not be flexible at all.
	 * Can have length less than <code>sizes</code> in which case the last element should be used for the elements missing.
	 * @param defGrow If there is no grow weight for a resConstr the value of this argument decides if <code>ResizeConstraint.WEIGHT_100</code> should be used
	 * (<code>true</code>) or "no grow" should be used (<code>false</code>). These forced resConstr will be grown last though and only if needed to fill to the bounds.
	 * @param startSizeType The initial size to use. E.g. {@link net.miginfocom.layout.LayoutUtil#MIN}.
	 * @param bounds To use for relative sizes.
	 * @return The sizes. Array length will match <code>sizes</code>.
	 */
	static int[] calculateSerial(int[][] sizes, ResizeConstraint[] resConstr, boolean defGrow, int startSizeType, int bounds)
	{
		float[] lengths = new float[sizes.length];	// heights/widths that are set
		float usedLength = 0.0f;

		// Give all preferred size to start with
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] != null) {
				float len = sizes[i][startSizeType] != NOT_SET ? sizes[i][startSizeType] : 0;
				int newSizeBounded = getBrokenBoundary(len, sizes[i][MIN], sizes[i][MAX]);
				if (newSizeBounded != NOT_SET)
					len = newSizeBounded;

				usedLength += len;
				lengths[i] = len;
			}
		}

		int useLengthI = Math.round(usedLength);
		if (useLengthI != bounds && resConstr != null) {
			boolean isGrow = useLengthI < bounds;

			// Create a Set with the available priorities
			TreeSet<Integer> prioList = new TreeSet<Integer>();
			for (int i = 0; i < sizes.length; i++) {
				ResizeConstraint resC = (ResizeConstraint) getIndexSafe(resConstr, i);
				if (resC != null)
					prioList.add(new Integer(isGrow ? resC.growPrio : resC.shrinkPrio));
			}
			Integer[] prioIntegers = prioList.toArray(new Integer[prioList.size()]);

			for (int force = 0; force <= (defGrow && isGrow ? 1 : 0); force++) {    // Run twice if defGrow and the need for growing.
				for (int pr = prioIntegers.length - 1; pr >= 0; pr--) {
					int curPrio = prioIntegers[pr].intValue();

					float totWeight = 0f;
					Float[] resizeWeight = new Float[sizes.length];
					for (int i = 0; i < sizes.length; i++) {
						if (sizes[i] == null)   // if no min/pref/max size at all do not grow or shrink.
							continue;

						ResizeConstraint resC = (ResizeConstraint) getIndexSafe(resConstr, i);
						if (resC != null) {
							int prio = isGrow ? resC.growPrio : resC.shrinkPrio;

							if (curPrio == prio) {
								if (isGrow) {
									resizeWeight[i] = (force == 0 || resC.grow != null) ? resC.grow : ResizeConstraint.WEIGHT_100;
								} else {
									resizeWeight[i] = resC.shrink;
								}
								if (resizeWeight[i] != null)
									totWeight += resizeWeight[i].floatValue();
							}
						}
					}

					if (totWeight > 0f) {
						boolean hit;
						do {
							float toChange = bounds - usedLength;
							hit = false;
							float changedWeight = 0f;
							for (int i = 0; i < sizes.length && totWeight > 0.0001f; i++) {

								Float weight = resizeWeight[i];
								if (weight != null) {
									float sizeDelta = toChange * weight.floatValue() / totWeight;
									float newSize = lengths[i] + sizeDelta;

									if (sizes[i] != null) {
										int newSizeBounded = getBrokenBoundary(newSize, sizes[i][MIN], sizes[i][MAX]);
										if (newSizeBounded != NOT_SET) {
											resizeWeight[i] = null;
											hit = true;
											changedWeight += weight.floatValue();
											newSize = newSizeBounded;
											sizeDelta = newSize - lengths[i];
										}
									}

									lengths[i] = newSize;
									usedLength += sizeDelta;
								}
							}
							totWeight -= changedWeight;
						} while (hit);
					}
				}
			}
		}
		return roundSizes(lengths);
	}

	static Object getIndexSafe(Object[] arr, int ix)
	{
		return arr != null ? arr[ix < arr.length ? ix : arr.length - 1] : null;
	}

	/** Returns the broken boundary if <code>sz</code> is outside the boundaries <code>lower</code> or <code>upper</code>. If both boundaries
	 * are broken, the lower one is returned. If <code>sz</code> is &lt; 0 then <code>new Float(0f)</code> is returned so that no sizes can be
	 * negative.
	 * @param sz The size to check
	 * @param lower The lower boundary (or <code>null</code> fo no boundary).
	 * @param upper The upper boundary (or <code>null</code> fo no boundary).
	 * @return The broken boundary or <code>null</code> if no boundary was broken.
	 */
	private static int getBrokenBoundary(float sz, int lower, int upper)
	{
		if (lower != NOT_SET) {
			if (sz < lower)
				return new Integer(lower);
		} else if (sz < 0f) {
			return new Integer(0);
		}

		if (upper != NOT_SET && sz > upper)
			return new Integer(upper);

		return NOT_SET;
	}


	static int sum(int[] terms, int start, int len)
	{
		int s = 0;
		for (int i = start, iSz = start + len; i < iSz; i++)
			s += terms[i];
		return s;
	}

	static int sum(int[] terms)
	{
		return sum(terms, 0, terms.length);
	}

	public static int getSizeSafe(int[] sizes, int sizeType)
	{
		if (sizes == null || sizes[sizeType] == NOT_SET)
			return sizeType == MAX ? LayoutUtil.INF : 0;
		return sizes[sizeType];
	}

	static BoundSize derive(BoundSize bs, UnitValue min, UnitValue pref, UnitValue max)
	{
		if (bs == null)
			return new BoundSize(min, pref, max, null);

		return new BoundSize(
				min != null ? min : bs.getMin(),
				pref != null ? pref : bs.getPreferred(),
				max != null ? max : bs.getMax(), null);
	}

	/** Returns if left-to-right orientation is used. If not set explicitly in the layout constraints the Locale
	 * of the <code>parent</code> is used.
	 * @param lc The constraint if there is one. Can be <code>null</code>.
	 * @param parent The parent that may be used to get the left-to-right if ffc does not specify this.
	 * @return If left-to-right orientation is currently used.
	 */
	public final static boolean isLeftToRight(LC lc, ContainerWrapper parent)
	{
		if (lc != null && lc.getLeftToRight() != null)
			return lc.getLeftToRight().booleanValue();

		return parent == null || parent.isLeftToRight();
	}

	/** Round a number of float sizes into int sizes so that the total length match up
	 * @param sizes The sizes to round
	 * @return An array of equal length as <code>sizes</code>.
	 */
	static int[] roundSizes(float[] sizes)
	{
		int[] retInts = new int[sizes.length];
		float posD = 0;

		for (int i = 0; i < retInts.length; i++) {
			int posI = (int) (posD + 0.5f);

			posD += sizes[i];

			retInts[i] = (int) (posD + 0.5f) - posI;
		}

		return retInts;
	}

	/** Safe equals. null == null, but null never equals anything else.
	 * @param o1 The first object. May be <code>null</code>.
	 * @param o2 The second object. May be <code>null</code>.
	 * @return Returns <code>true</code> if <code>o1</code> and <code>o2</code> are equal (using .equals()) or both are <code>null</code>.
	 */
	static final boolean equals(Object o1, Object o2)
	{
		return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
	}

//	static int getBaselineCorrect(Component comp)
//	{
//		Dimension pSize = comp.getPreferredSize();
//		int baseline = comp.getBaseline(pSize.width, pSize.height);
//		int nextBaseline = comp.getBaseline(pSize.width, pSize.height + 1);
//
//		// Amount to add to height when calculating where baseline
//		// lands for a particular height:
//		int padding = 0;
//
//		// Where the baseline is relative to the mid point
//		int baselineOffset = baseline - pSize.height / 2;
//		if (pSize.height % 2 == 0 && baseline != nextBaseline) {
//			padding = 1;
//		} else if (pSize.height % 2 == 1 && baseline == nextBaseline) {
//			baselineOffset--;
//			padding = 1;
//		}
//
//		// The following calculates where the baseline lands for
//		// the height z:
//		return (pSize.height + padding) / 2 + baselineOffset;
//	}

	/** Converts a <code>float</code> to a string and is removing the ".0" if the float is an integer.
	 * @param f the float.
	 * @return <code>f</code> as a string. Never <code>null</code>.
	 */
	static final String floatToString(float f)
	{
		String valS = String.valueOf(f);
		return valS.endsWith(".0") ? valS.substring(0, valS.length() - 2) : valS;
	}

	/** Returns the inset for the side.
	 * @param side top == 0, left == 1, bottom = 2, right = 3.
	 * @return The inset for the side. May be <code>null</code>.
	 */
	static final UnitValue getInsets(LC lc, int side)
	{
		UnitValue[] i = lc.getInsets();
		return i != null && i[side] != null ? i[side] : PlatformDefaults.getPanelInsets(side);
	}

	/** Writes the objet and CLOSES the stream. Uses the persistence delegate registered in this class.
	 * @param os The stream to write to. Will be closed.
	 * @param o The object to be serialized.
	 * @param listener The listener to recieve the exeptions if there are any. If <code>null</code> not used.
	 */
	static void writeXMLObject(OutputStream os, Object o, ExceptionListener listener)
	{
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(LayoutUtil.class.getClassLoader());

		XMLEncoder encoder = new XMLEncoder(os);

		if (listener != null)
			encoder.setExceptionListener(listener);

		encoder.writeObject(o);
        encoder.close();    // Must be closed to write.

		Thread.currentThread().setContextClassLoader(oldClassLoader);
	}

	private static ByteArrayOutputStream writeOutputStream = null;
	/** Writes an object to XML.
	 * @param out The boject out to write to. Will not be closed.
	 * @param o The object to write.
	 */
	public static synchronized void writeAsXML(ObjectOutput out, Object o) throws IOException
	{
		if (writeOutputStream == null)
			writeOutputStream = new ByteArrayOutputStream(16384);

		writeOutputStream.reset();

		writeXMLObject(writeOutputStream, o, new ExceptionListener() {
			public void exceptionThrown(Exception e) {
				e.printStackTrace();
			}});

		byte[] buf = writeOutputStream.toByteArray();

		out.writeInt(buf.length);
		out.write(buf);
	}

	private static byte[] readBuf = null;
	/** Reads an object from <code>in</code> using the
	 * @param in The object input to read from.
	 * @return The object. Never <code>null</code>.
	 * @throws IOException If there was a problem saving as XML
	 */
	@SuppressWarnings("resource")
	public static synchronized Object readAsXML(ObjectInput in) throws IOException
	{
		if (readBuf == null)
			readBuf = new byte[16384];

		Thread cThread = Thread.currentThread();
		ClassLoader oldCL = null;

		try {
			oldCL = cThread.getContextClassLoader();
			cThread.setContextClassLoader(LayoutUtil.class.getClassLoader());
		} catch(SecurityException e) {
		}

		Object o = null;
		try {
			int length = in.readInt();
			if (length > readBuf.length)
				readBuf = new byte[length];

			in.readFully(readBuf, 0, length);

			o = new XMLDecoder(new ByteArrayInputStream(readBuf, 0, length)).readObject();

		} catch(EOFException e) {
		}

		if (oldCL != null)
			cThread.setContextClassLoader(oldCL);

		return o;
	}

	private static final IdentityHashMap<Object, Object> SER_MAP = new IdentityHashMap<Object, Object>(2);

	/** Sets the serialized object and associates it with <code>caller</code>.
	 * @param caller The objec created <code>o</code>
	 * @param o The just serialized object.
	 */
	public static void setSerializedObject(Object caller, Object o)
	{
		synchronized(SER_MAP) {
			SER_MAP.put(caller, o);
		}
	}

	/** Returns the serialized object that are associated with <code>caller</code>. It also removes it from the list.
	 * @param caller The original creator of the object.
	 * @return The object.
	 */
	public static Object getSerializedObject(Object caller)
	{
		synchronized(SER_MAP) {
			return SER_MAP.remove(caller);
		}
	}
}
