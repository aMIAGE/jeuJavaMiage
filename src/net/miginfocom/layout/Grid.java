package net.miginfocom.layout;

import java.lang.ref.WeakReference;
import java.util.*;
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

/** Holds components in a grid. Does most of the logic behind the layout manager.
 */
public final class Grid
{
	private static final DimConstraint DOCK_DIM_CONSTRAINT = new DimConstraint();
	static {
		DOCK_DIM_CONSTRAINT.setGrowPriority(0);
	}

	/** This is the maximum grid position for "normal" components. Docking components use the space out to
	 * <code>MAX_DOCK_GRID</code> and below 0.
	 */
	private static final int MAX_GRID = 30000;

	/** Docking components will use the grid coordinates <code>-MAX_DOCK_GRID -> 0</code> and <code>MAX_GRID -> MAX_DOCK_GRID</code>.
	 */
	private static final int MAX_DOCK_GRID = 32767;

	/** A constraint used for gaps.
	 */
	private static final ResizeConstraint GAP_RC_CONST = new ResizeConstraint(100, ResizeConstraint.WEIGHT_100, 100, ResizeConstraint.WEIGHT_100);

	/** The constraints. Never <code>null</code>.
	 */
	private final LC lc;

	/** The parent that is layout out and this grid is done for. Never <code>null</code>.
	 */
	private final ContainerWrapper parent;

	/** An x, y array implemented as a sparse array to accomodate for any grid size without wasting memory (or rather 15 bit (0-MAX_GRID * 0-MAX_GRID).
	 */
	private final LinkedHashMap<Integer, Cell> grid = new LinkedHashMap<Integer, Cell>();   // [(y << 16) + x] -> Cell. null key for absolute positioned compwraps

	/** The size of the grid. Row count and column count.
	 */
	private final TreeSet<Integer> rowIndexes = new TreeSet<Integer>(), colIndexes = new TreeSet<Integer>();

	/** The row and column specifications.
	 */
	private final AC rowConstr, colConstr;

	/** The in the constructor calculated min/pref/max sizes of the rows and columns.
	 */
	private final FlowSizeSpec colFlowSpecs, rowFlowSpecs;

	/** Components that are connectione in one dimension (such as baseline alignment for instance) are grouped together and stored here.
	 * One for each row/column.
	 */
	private ArrayList<LinkedDimGroup>[] colGroupLists, rowGroupLists;   //[(start)row/col number]

	/** The in the constructor calculated min/pref/max size of the whole grid.
	 */
	private final int[] width, height;

	/** If debug is on contains the bounds for things to paint when calling {@link ContainerWrapper#paintDebugCell(int, int, int, int)}
	  */
	private ArrayList<int[]> debugRects = null; // [x, y, width, height]

	/** If any of the absolute coordinates for component bounds has links the name of the target is in this Set.
	 * Since it requires some memory and computations this is checked at the creation so that
	 * the link information is only created if needed later.
	 * <p>
	 * The boolean is true for groups id:s and null for normal id:s.
	 */
	private HashMap<String, Boolean> linkTargetIDs = null;

	int dockOffY = 0, dockOffX = 0;

	/** Constructor.
	 * @param parent The parent that will be laid out.
	 * @param lc The form flow constraints.
	 * @param rowConstr The rows specifications. If more cell rows are required, the last element will be used for when there is no corresponding element in this array.
	 * @param colConstr The columns specifications. If more cell rows are required, the last element will be used for when there is no corresponding element in this array.
	 * @param fcMap The map containing the parsed constraints for each child component of <code>parent</code>.
	 */
	public Grid(ContainerWrapper parent, LC lc, AC rowConstr, AC colConstr, Map<ComponentWrapper, CC> fcMap)
	{
		this.lc = lc;
		this.rowConstr = rowConstr;
		this.colConstr = colConstr;
		this.parent = parent;

		int wrap = lc.getWrapAfter() != 0 ? lc.getWrapAfter() : (lc.isFlowX() ? colConstr : rowConstr).getConstaints().length;

		final ComponentWrapper[] comps = parent.getComponents();

		boolean hasTagged = false;  // So we do not have to sort if it will not do any good
		final int[] p = new int[2];
		final ArrayList<int[]> spannedRects = new ArrayList<int[]>(2);

		final DimConstraint[] specs = (lc.isFlowX() ? rowConstr : colConstr).getConstaints();

		int sizeGroupsX = 0, sizeGroupsY = 0;
		int[] dockInsets = null;    // top, left, bottom, right insets for docks.

		LinkHandler.clearTemporaryBounds(parent.getLayout());

		int maxC = -1, maxR = -1;
		for (int i = 0; i < comps.length;) {
			ComponentWrapper comp = comps[i];
			CC rootCc = fcMap.get(comp);

			int hideMode = comp.isVisible() ? -1 : rootCc.getHideMode() != -1 ? rootCc.getHideMode() : lc.getHideMode();

			if (rootCc == null || rootCc.isExternal() || hideMode == 3) { // To work with situations where there are components that does not have a layout manager, or not this one.
				if (rootCc != null)
					setLinkedBounds(comp, rootCc, comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight(), true);
				i++;
				continue;   // The "external" component should not be handled further.
			}

			if (rootCc.getHorizontal().getSizeGroup() != null)
				sizeGroupsX++;
			if (rootCc.getVertical().getSizeGroup() != null)
				sizeGroupsY++;

			// Special treatment of absolute positioned components.
			if (rootCc.getPos() != null) {

				// Check if link information should be saved later.
				for (int l = 0; l < rootCc.getPos().length; l++) {
					UnitValue u = rootCc.getPos()[l];
					if (u != null && u.isLinkedDeep())
						addLinkTargetIDs(u);
				}

				CompWrap cw = new CompWrap(comp, rootCc, hideMode);
				Cell cell = grid.get(null);
				if (cell == null) {
					grid.put(null, new Cell(cw));
				} else {
					cell.compWraps.add(cw);
				}

				if (rootCc.isBoundsIsGrid() == false) {
					i++;
					continue;
				}
			}

			if (rootCc.getDockSide() != -1) {
				if (dockInsets == null)
					dockInsets = new int[] {-MAX_DOCK_GRID, -MAX_DOCK_GRID, MAX_DOCK_GRID, MAX_DOCK_GRID};

				addDockingCell(dockInsets, rootCc.getDockSide(), new CompWrap(comp, rootCc, hideMode));
				i++;
				continue;
			}

			Boolean cellFlowX = rootCc.getFlowX();
			Cell cell = null;

			if (rootCc.isNewline())
				wrap(p);

			increase(p, rootCc.getSkip());    // Probably 0 advance...

			boolean rowNoGrid = lc.isNoGrid() || ((DimConstraint) LayoutUtil.getIndexSafe(specs, lc.isFlowX() ? p[1] : p[0])).isNoGrid();

			// Move to a free y, x  if no absolute grid specified
			int cx = rootCc.getCellX();
			int cy = rootCc.getCellY();
			if ((cx < 0 || cy < 0) && rowNoGrid == false) {
				while (isCellFree(p[1], p[0], spannedRects) == false) {
					if (Math.abs(increase(p, 1)) >= wrap)
						wrap(p);
				}
			} else {
				if (cx >= 0 && cy >= 0) {
					if (cy >= 0) {
						p[0] = cx;
						p[1] = cy;
					} else {    // Only one coordinate is specified. Use the current row (flowx) or column (flowy) to fill in.
						if (lc.isFlowX()) {
							p[0] = cx;
						} else {
							p[1] = cx;
						}
					}
				}
				cell = getCell(p[1], p[0]);   // Might be null
			}

			int spanx = rowNoGrid && lc.isFlowX() ? LayoutUtil.INF : rootCc.getSpanX();
			int spany = rowNoGrid && !lc.isFlowX() ? LayoutUtil.INF : rootCc.getSpanY();
			if (cell == null)
				cell = new Cell(spanx, spany, cellFlowX != null ? cellFlowX.booleanValue() : lc.isFlowX());

			// Add the one, or all, components that split the grid position to the same Cell.
			boolean forceWrap = false;
			int splitLeft = rowNoGrid ? LayoutUtil.INF : rootCc.getSplit() - 1;
			boolean splitExit = false;
			for (; splitLeft >= 0 && forceWrap == false && i < comps.length; splitLeft--) {
				ComponentWrapper compAdd = comps[i];
				CC cc = fcMap.get(compAdd); // Can not be null.

				hideMode = compAdd.isVisible() ? -1 : cc.getHideMode() != -1 ? cc.getHideMode() : lc.getHideMode();

				if (cc == null || cc.isExternal() || hideMode == 3) {
					i++;
					continue;       // To work with situations where there are components that does not have a layout manager, or not this one.
				}

				if (cc != rootCc) {
					if (cc.isNewline())
						break;

					if (splitLeft > 0 && cc.getSkip() > 0) {
						splitExit = true;
						break;
					}
				}

				CompWrap cw = new CompWrap(compAdd, cc, hideMode);
				cell.compWraps.add(cw);
				cell.hasTagged |= cc.getTag() != null;
				hasTagged |= cell.hasTagged;

				if (cc != rootCc) {
					if (cc.getHorizontal().getSizeGroup() != null)
						sizeGroupsX++;
					if (cc.getVertical().getSizeGroup() != null)
						sizeGroupsY++;
				}

				int flowSpan = lc.isFlowX() ? rootCc.getSpanX() : rootCc.getSpanY();
				forceWrap = (cc.isWrap() || (flowSpan == LayoutUtil.INF && splitLeft == 0));
				i++;
			}

			// Point to the cell from all spanned grid elements
			if (p[0] > maxC)
				maxC = p[0];
			if (p[1] > maxR)
				maxR = p[1];
			setCell(p[1], p[0], cell);

			// Add a rectangle so we can know that spanned cells occupy more space.
			if (spanx > 1 || spany > 1)
				spannedRects.add(new int[] {p[0], p[1], spanx, spany});

			if (forceWrap) {
				wrap(p);
			} else if (rowNoGrid == false) {
				int span = lc.isFlowX() ? cell.spanx : cell.spany;
				if (Math.abs((lc.isFlowX() ? p[0] : p[1])) + span >= wrap) {
					wrap(p);
				} else {
					int adv = lc.isFlowX() ? cell.spanx : cell.spany;
					if (splitExit)
						adv--;
					increase(p, adv);
				}
			}
		}

		// If there were size groups, calculate the largest values in the groups (for min/pref/max) and enforce them on the rest in the group.
		if (sizeGroupsX > 0 || sizeGroupsY > 0) {
			HashMap<String, int[]> sizeGroupMapX = sizeGroupsX > 0 ? new HashMap<String, int[]>(sizeGroupsX) : null;
			HashMap<String, int[]> sizeGroupMapY = sizeGroupsY > 0 ? new HashMap<String, int[]>(sizeGroupsY) : null;
			ArrayList<CompWrap> sizeGroupCWs = new ArrayList<CompWrap>(Math.max(sizeGroupsX, sizeGroupsY));

			for (Iterator<Cell> it = grid.values().iterator(); it.hasNext();) {
				Cell cell = it.next();
				for (int i = 0; i < cell.compWraps.size(); i++) {
					CompWrap cw = cell.compWraps.get(i);

					if (cw.cc.getHorizontal().getSizeGroup() != null || cw.cc.getVertical().getSizeGroup() != null) {
						addToSizeGroup(sizeGroupMapX, cw.cc.getHorizontal().getSizeGroup(), cw.horSizes);
						addToSizeGroup(sizeGroupMapY, cw.cc.getVertical().getSizeGroup(), cw.verSizes);
						sizeGroupCWs.add(cw);
					}
				}
			}

			// Set/equalize the sizeGroups to same the values.
			if (sizeGroupCWs != null) {
				for (int i = 0; i < sizeGroupCWs.size(); i++) {
					CompWrap cw = sizeGroupCWs.get(i);
					if (sizeGroupMapX != null)
						cw.setSizes(sizeGroupMapX.get(cw.cc.getHorizontal().getSizeGroup()), true);  // Target method handles null sizes
					if (sizeGroupMapY != null)
						cw.setSizes(sizeGroupMapY.get(cw.cc.getVertical().getSizeGroup()), false); // Target method handles null sizes
				}
			}
		}

		if (hasTagged)
			sortCellsByPlatform(grid.values(), parent);

		// Calculate gaps now that the cells are filled and we know all adjacent components.
		boolean ltr = LayoutUtil.isLeftToRight(lc, parent);
		for (Iterator<Cell> it = grid.values().iterator(); it.hasNext();) {
			Cell cell = it.next();
			ArrayList<CompWrap> cws = cell.compWraps;
			for (int i = 0, lastI = cws.size() - 1; i <= lastI; i++) {
				CompWrap cw = cws.get(i);
				ComponentWrapper cwBef = i > 0 ? cws.get(i - 1).comp : null;
				ComponentWrapper cwAft = i < lastI ? cws.get(i + 1).comp : null;

				String tag = fcMap.get(cw.comp).getTag();
				CC ccBef = cwBef != null ? fcMap.get(cwBef) : null;
				CC ccAft = cwAft != null ? fcMap.get(cwAft) : null;

				cw.calcGaps(cwBef, ccBef, cwAft, ccAft, tag, cell.flowx, ltr);
			}
		}

		// Add synthetic indexes for empty rows and columns so they can get a size
		dockOffX = getDockInsets(colIndexes);
		dockOffY = getDockInsets(rowIndexes);

		for (int i = 1, iSz = rowConstr.getCount() - getNormalIndexesCount(rowIndexes); i <= iSz; i++)
			rowIndexes.add(new Integer(maxR + i));
		for (int i = 1, iSz = colConstr.getCount() - getNormalIndexesCount(colIndexes); i <= iSz; i++)
			colIndexes.add(new Integer(maxC + i));

		colGroupLists = divideIntoLinkedGroups(false);
		rowGroupLists = divideIntoLinkedGroups(true);

		colFlowSpecs = calcRowsOrColsSizes(colGroupLists, parent.getWidth(), true);
		rowFlowSpecs = calcRowsOrColsSizes(rowGroupLists, parent.getHeight(), false);

		width = getMinPrefMaxSumSize(colFlowSpecs.sizes);
		height = getMinPrefMaxSumSize(rowFlowSpecs.sizes);

		resetLinkValues(false);

		adjustSizeForAbsolute(width, true);
		adjustSizeForAbsolute(height, false);
	}

	private final int getNormalIndexesCount(TreeSet<Integer> set)
	{
		int c = 0;
		for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
			int i = it.next().intValue();
			if (i > -MAX_GRID && i < MAX_GRID)
				c++;
		}
		return c;
	}

	private final int getDockInsets(TreeSet<Integer> set)
	{
		int c = 0;
		for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
			int i = it.next().intValue();
			if (i < -MAX_GRID)
				c++;
			else
				break;  // Since they are sorted we can break
		}
		return c;
	}

	/**
	 * @param cw Never <code>null</code>.
	 * @param fc Never <code>null</code>.
	 * @param external The bounds should be stored even if they are not in {@link #linkTargetIDs}.
	 * @return IF a change has been done.
	 */
	private boolean setLinkedBounds(ComponentWrapper cw, CC fc, int x, int y, int w, int h, boolean external)
	{
		String id = fc.getId() != null ? fc.getId() : cw.getLinkId();
		if (id == null)
			return false;

		String gid = null;
		int grIx = id.indexOf('.');
		if (grIx != -1 ) {
			gid = id.substring(0, grIx);
			id = id.substring(grIx + 1);
		}

		Object lay = parent.getLayout();
		boolean changed = false;
		if (external || linkTargetIDs.containsKey(id))
			changed = LinkHandler.setBounds(lay, id, x, y, w, h, !external, false);

		if (gid != null && (external || linkTargetIDs.containsKey(gid))) {
			linkTargetIDs.put(gid, Boolean.TRUE);
			changed |= LinkHandler.setBounds(lay, gid, x, y, w, h, !external, true);
		}

		return changed;
	}

	private void addLinkTargetIDs(UnitValue uv)
	{
		if (uv.isLinked()) {
			if (linkTargetIDs == null)
				linkTargetIDs = new HashMap<String, Boolean>(4);

			linkTargetIDs.put(uv.getLinkTargetId(), null);
		} else {
			for (int i = uv.getSubUnitCount() - 1; i >= 0; i--) {
				UnitValue subUv = uv.getSubUnitValue(i);
				if (subUv.isLinkedDeep())
					addLinkTargetIDs(subUv);
			}
		}
	}

	public void paintDebug()
	{
		if (debugRects != null) {
			parent.paintDebugOutline();

			ArrayList<int[]> painted = new ArrayList<int[]>();
			for (int i = 0, iSz = debugRects.size(); i < iSz; i++) {
				int[] r = debugRects.get(i);
				if (painted.contains(r) == false) {
					parent.paintDebugCell(r[0], r[1], r[2], r[3]);
					painted.add(r);
				}
			}

			for (Iterator<Cell> it = grid.values().iterator(); it.hasNext();) {
				ArrayList<CompWrap> compWraps = it.next().compWraps;
				for (int i = 0, iSz = compWraps.size(); i < iSz; i++)
					compWraps.get(i).comp.paintDebugOutline();
			}
		}
	}

	public final int[] getWidth()
	{
		return width;
	}

	public final int[] getHeight()
	{
		return height;
	}

	/** Go to next cell.
	 * @param p The point to increase
	 * @param cnt How many cells to advance.
	 * @return The new value in the "incresing" dimension.
	 */
	private final int increase(int[] p, int cnt)
	{
		return lc.isFlowX() ? (p[0] += cnt) : (p[1] += cnt);
	}

	/** Wraps to the next row or column depending on if horizontal flow or vertical flow is used.
	 * @param p The point to wrap and thus set either x or y to 0 and increase the other one.
	 */
	private final void wrap(int[] p)
	{
		boolean flowx = lc.isFlowX();
		p[0] = flowx ? 0 : p[0] + 1;
		p[1] = flowx ? p[1] + 1 : 0;
	}

	/** Sort components (normally buttons in a button bar) so they appear in the correct order.
	 * @param cells The cells to sort.
	 * @param parent The parent.
	 */
	private static void sortCellsByPlatform(Collection<Cell> cells, ContainerWrapper parent)
	{
		String order = PlatformDefaults.getButtonOrder();
		String orderLo = order.toLowerCase();

		int unrelSize = PlatformDefaults.convertToPixels(1, "u", true, 0, parent, null);
		int relSize = PlatformDefaults.convertToPixels(1, "r", true, 0, parent, null);

		if (unrelSize == UnitConverter.UNABLE || relSize == UnitConverter.UNABLE)
			throw new IllegalArgumentException((unrelSize == UnitConverter.UNABLE ? "'un" : "") + "related' not recognized by PlatformDefaults!");

		int[] pushGapUnrel = new int[] {unrelSize, unrelSize, LayoutUtil.NOT_SET};
		int[] pushGapUnrelFL = new int[] {0, 0, LayoutUtil.NOT_SET};    // If first or last
		int[] gapUnrel = new int[] {unrelSize, unrelSize, unrelSize};
		int[] gapRel = new int[] {relSize, relSize, relSize};

		for (Iterator<Cell> it = cells.iterator(); it.hasNext();) {
			Cell cell = it.next();
			if (cell.hasTagged == false)
				continue;

			CompWrap prevCW = null;
			int[] nextGap = null;
			ArrayList<CompWrap> sortedList = new ArrayList<CompWrap>(cell.compWraps.size());
			for (int i = 0, iSz = orderLo.length(); i < iSz; i++) {
				char c = orderLo.charAt(i);
				if (c == '+') {
					nextGap = prevCW != null ? pushGapUnrel : pushGapUnrelFL;   // FL means "First or Last"
				} else if (c == '_' && prevCW != null && nextGap != pushGapUnrelFL && nextGap != pushGapUnrel) {
					nextGap = gapUnrel;
				}

				if (c != '_' && c != '+') {
					String tag = PlatformDefaults.getTagForChar(c);
					for (int j = 0, jSz = cell.compWraps.size(); j < jSz; j++) {
						CompWrap cw = cell.compWraps.get(j);
						if (tag != null && tag.equals(cw.cc.getTag())) {

							if (Character.isUpperCase(order.charAt(i)))
								cw.horSizes[LayoutUtil.MIN] = PlatformDefaults.getMinimumButtonWidth().getPixels(0, parent, cw.comp);

							sortedList.add(cw);
							if (nextGap != null)
								(prevCW != null ? prevCW : cw).mergeGapSizes(nextGap, cell.flowx, prevCW == null);

							// "unknown" components will always get an Unrelated gap.
							nextGap = c == 'u' ? gapUnrel : gapRel;
							prevCW = cw;
						}
					}
				}
			}

			// If we have a gap that was supposed to push but no more components was found to but the "gap before" then compensate.
			if (nextGap == pushGapUnrel)
				sortedList.get(sortedList.size() - 1).mergeGapSizes(pushGapUnrelFL, cell.flowx, false);

			// Exchange the unsorted CompWraps for the sorted one.
			if (cell.compWraps.size() == sortedList.size()) {
				cell.compWraps.clear();
			} else {
				cell.compWraps.removeAll(sortedList);
			}
			cell.compWraps.addAll(sortedList);
		}
	}

	/** Does the actual layout. Uses many values caclulated in the constructor.
	 * @param bounds The bounds to layout against. Normally that of the parent. [x, y, width, height].
	 * @param alignX The alignment for the x-axis.
	 * @param alignY The alignment for the y-axis.
	 * @param debug If debug information should be saved in {@link #debugRects}.
	 */
	public void layout(int[] bounds, UnitValue alignX, UnitValue alignY, boolean debug)
	{
		if (debug)
			debugRects = new ArrayList<int[]>();

		resetLinkValues(true);

		layoutInOneDim(bounds[2], alignX, false, lc.isFillX());
		layoutInOneDim(bounds[3], alignY, true, lc.isFillY());

		HashMap<String, Integer> endGrpXMap = null, endGrpYMap = null;
		int compCount = parent.getComponentCount();

		// Transfer the calculated bound from the ComponentWrappers to the actual Components.
		if (compCount > 0) {
			for (int j = 0; j < (linkTargetIDs != null ? 2 : 1); j++) {   // First do the calculations (maybe more than once) then set the bounds when done
				boolean doAgain;
				int count = 0;
				do {
					doAgain = false;
					for (Iterator<Cell> it = grid.values().iterator(); it.hasNext();) {
						ArrayList<CompWrap> compWraps = it.next().compWraps;
						for (int i = 0, iSz = compWraps.size(); i < iSz; i++) {
							CompWrap cw = compWraps.get(i);

							if (j == 0) {
								doAgain |= doAbsoluteCorrections(cw, bounds);
								if (doAgain == false) { // If we are going to do this again, do not bother this time around
									if (cw.cc.getHorizontal().getEndGroup() != null)
										endGrpXMap = addToEndGroup(endGrpXMap, cw.cc.getHorizontal().getEndGroup(), cw.x + cw.w);
									if (cw.cc.getVertical().getEndGroup() != null)
										endGrpYMap = addToEndGroup(endGrpYMap, cw.cc.getVertical().getEndGroup(), cw.y + cw.h);
								}
							}

							if (linkTargetIDs == null || j == 1) {
								if (cw.cc.getHorizontal().getEndGroup() != null)
									cw.w = endGrpXMap.get(cw.cc.getHorizontal().getEndGroup()).intValue() - cw.x;
								if (cw.cc.getVertical().getEndGroup() != null)
									cw.h = endGrpYMap.get(cw.cc.getVertical().getEndGroup()).intValue() - cw.y;

								cw.x += bounds[0];
								cw.y += bounds[1];
								cw.transferBounds();
							}
						}
					}
					clearGroupLinkBounds();
					if (++count > (compCount << 2 + 10)) {
						System.err.println("Unstable Cyclic Dependency in absolute-linked values!");
						break;
					}

				} while (doAgain);
			}
		}

		// Add debug shapes for the "cells". Use the CompWraps as base for inding the cells.
		if (debug) {
			Collection<Cell> cwColl = grid.values();
			for (Iterator<Cell> it = cwColl.iterator(); it.hasNext();) {
				ArrayList<CompWrap> compWraps = it.next().compWraps;
				for (int i = 0, iSz = compWraps.size(); i < iSz; i++) {
					CompWrap cw = compWraps.get(i);
					LinkedDimGroup hGrp = getGroupContaining(colGroupLists, cw);
					LinkedDimGroup vGrp = getGroupContaining(rowGroupLists, cw);

					if (hGrp != null && vGrp != null)
						debugRects.add(new int[] {hGrp.lStart + bounds[0], vGrp.lStart + bounds[1], hGrp.lSize, vGrp.lSize});
				}
			}
		}
	}

	private void clearGroupLinkBounds()
	{
		if (linkTargetIDs == null)
			return;

		for (Iterator<Map.Entry<String,Boolean>> it = linkTargetIDs.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Boolean> o = it.next();
			if (o.getValue() == Boolean.TRUE)
				LinkHandler.clearBounds(parent.getLayout(), o.getKey());
		}
	}

	private void resetLinkValues(boolean parentSize)
	{
		Object lay = parent.getLayout();
		LinkHandler.clearTemporaryBounds(lay);

		int parW = parentSize ? parent.getWidth() : 0;
		int parH = parentSize ? parent.getHeight() : 0;
		int insX = LayoutUtil.getInsets(lc, 0).getPixels(0, parent, null);
		int insY = LayoutUtil.getInsets(lc, 1).getPixels(0, parent, null);
		int insW = parW - insX - LayoutUtil.getInsets(lc, 2).getPixels(0, parent, null);
		int insH = parH - insY - LayoutUtil.getInsets(lc, 3).getPixels(0, parent, null);

		LinkHandler.setBounds(lay, "visual", insX, insY, insW, insH, true, false);
		LinkHandler.setBounds(lay, "container", 0, 0, parW, parH, true, false);
	}

	/** Returns the {@link net.miginfocom.layout.Grid.LinkedDimGroup} that has the {@link net.miginfocom.layout.Grid.CompWrap}
	 * <code>cw</code>.
	 * @param groupLists The lists to search in.
	 * @param cw The component wrap to find.
	 * @return The linked group or <code>null</code> if none had the component wrap.
	 */
	private static LinkedDimGroup getGroupContaining(ArrayList<LinkedDimGroup>[] groupLists, CompWrap cw)
	{
		for (int i = 0; i < groupLists.length; i++) {
			ArrayList<LinkedDimGroup> groups = groupLists[i];
			for (int j = 0, jSz = groups.size(); j < jSz; j++) {
				ArrayList<CompWrap> cwList = groups.get(j)._compWraps;
				for (int k = 0, kSz = cwList.size(); k < kSz; k++) {
					if (cwList.get(k) == cw)
						return groups.get(j);
				}
			}
		}
		return null;
	}

	private boolean doAbsoluteCorrections(CompWrap cw, int[] bounds)
	{
		boolean changed = false;

		int[] stSz = getAbsoluteDimBounds(cw, bounds[2], true);
		if (stSz != null)
			cw.setDimBounds(stSz[0], stSz[1], true);

		stSz = getAbsoluteDimBounds(cw, bounds[3], false);
		if (stSz != null)
			cw.setDimBounds(stSz[0], stSz[1], false);

		// If there is a link id, store the new bounds.
		if (linkTargetIDs != null)
			changed = setLinkedBounds(cw.comp, cw.cc, cw.x, cw.y, cw.w, cw.h, false);

		return changed;
	}

	private void adjustSizeForAbsolute(int[] curSizes, boolean isHor)
	{
		Cell absCell = grid.get(null);
		if (absCell == null || absCell.compWraps.size() == 0)
			return;

		ArrayList<CompWrap> cws = absCell.compWraps;

		int maxEnd = 0;
		for (int j = 0, cwSz = absCell.compWraps.size(); j < cwSz + 3; j++) {  // "Do Again" max absCell.compWraps.size() + 3 times.
			boolean doAgain = false;
			for (int i = 0; i < cwSz; i++) {
				CompWrap cw = cws.get(i);
				int[] stSz = getAbsoluteDimBounds(cw, 0, isHor);
				int end = stSz[0] + stSz[1];
				if (maxEnd < end)
					maxEnd = end;

				// IF there is a link id, store the new bounds.
				if (linkTargetIDs != null)
					doAgain |= setLinkedBounds(cw.comp, cw.cc, stSz[0], stSz[0], stSz[1], stSz[1], false);
			}
			if (doAgain == false)
				break;

			// We need to check this again since the coords may be smaller this round.
			maxEnd = 0;
			clearGroupLinkBounds();
		}

		maxEnd += LayoutUtil.getInsets(lc, isHor ? 3 : 2).getPixels(0, parent, null);

		if (curSizes[LayoutUtil.MIN] < maxEnd)
			curSizes[LayoutUtil.MIN] = maxEnd;
		if (curSizes[LayoutUtil.PREF] < maxEnd)
			curSizes[LayoutUtil.PREF] = maxEnd;
	}

	private int[] getAbsoluteDimBounds(CompWrap cw, int refSize, boolean isHor)
	{
		UnitValue[] absPad = cw.cc.getPos();
		int[] plafPad = lc.isVisualPadding() ? cw.comp.getVisualPadding() : null;

		// If no changes do not create a lot of objects
		if (absPad == null && plafPad == null && cw.cc.getPadding() == null)
			return null;

		// Set start
		int st = isHor ? cw.x : cw.y;
		int sz = isHor ? cw.w : cw.h;

		// If absolute, use those coordinates instead.
		if (absPad != null) {
			UnitValue stUV = absPad != null ? absPad[isHor ? 0 : 1] : null;
			UnitValue endUV = absPad != null ? absPad[isHor ? 2 : 3] : null;

			int minSz = cw.getSize(LayoutUtil.MIN, isHor);
			int maxSz = cw.getSize(LayoutUtil.MAX, isHor);
			sz = Math.min(Math.max(cw.getSize(LayoutUtil.PREF, isHor), minSz), maxSz);

			if (stUV != null) {
				st = stUV.getPixels(stUV.getUnit() == UnitValue.ALIGN ? sz : refSize, parent, cw.comp);

				if (endUV != null)  // if (endUV == null && cw.cc.isBoundsIsGrid() == true)
					sz = Math.min(Math.max((isHor ? (cw.x + cw.w) : (cw.y + cw.h)) - st, minSz), maxSz);
			}

			if (endUV != null) {
				if (stUV != null) {   // if (stUV != null || cw.cc.isBoundsIsGrid()) {
					sz = Math.min(Math.max(endUV.getPixels(refSize, parent, cw.comp) - st, minSz), maxSz);
				} else {
					st = endUV.getPixels(refSize, parent, cw.comp) - sz;
				}
			}
		}

		// If constraint has padding -> correct the start/size
		UnitValue[] pad = cw.cc.getPadding();
		if (pad != null) {
			UnitValue uv = pad[isHor ? 1 : 0];
			int p = uv != null ? uv.getPixels(refSize, parent, cw.comp) : 0;
			st += p;
			uv = pad[isHor ? 3 : 2];
			sz += -p + (uv != null ? uv.getPixels(refSize, parent, cw.comp) : 0);
		}

		// If the plaf converter has padding -> correct the start/size
		if (plafPad != null) {
			int p = plafPad[isHor ? 1 : 0];
			st += p;
			sz += -p + (plafPad[isHor ? 3 : 2]);
		}

		return new int[] {st, sz};
	}

	private void layoutInOneDim(int refSize, UnitValue align, boolean isRows, boolean fill)
	{
		boolean fromEnd = !(isRows ? lc.isTopToBottom() : LayoutUtil.isLeftToRight(lc, parent));
		DimConstraint[] primDCs = (isRows ? rowConstr : colConstr).getConstaints();
		FlowSizeSpec fss = isRows ? rowFlowSpecs : colFlowSpecs;
		ArrayList<LinkedDimGroup>[] rowCols = isRows ? rowGroupLists : colGroupLists;

		int[] rowColSizes = LayoutUtil.calculateSerial(fss.sizes, fss.resConstsInclGaps, fill, LayoutUtil.PREF, refSize);
		if (LayoutUtil.isDesignTime()) {
			TreeSet<Integer> indexes = isRows ? rowIndexes : colIndexes;
			int[] ixArr = new int[indexes.size()];
			int ix = 0;
			for (Integer i : indexes)
				ixArr[ix++] = i.intValue();

			putSizesAndIndexes(parent.getComponent(), rowColSizes, ixArr, isRows);
		}

		int curPos = align != null ? Math.round(align.getPixels(refSize - LayoutUtil.sum(rowColSizes), parent, null)) : 0;

		if (fromEnd)
			curPos = refSize - curPos;

		for (int i = 0 ; i < rowCols.length; i++) {
			ArrayList<LinkedDimGroup> linkedGroups = rowCols[i];
			int scIx = i - (isRows ? dockOffY : dockOffX);

			int bIx = i << 1;
			int bIx2 = bIx + 1;

			curPos += (fromEnd ? -rowColSizes[bIx] : rowColSizes[bIx]);

			DimConstraint primDC = scIx >= 0 ? primDCs[scIx >= primDCs.length ? primDCs.length - 1 : scIx] : DOCK_DIM_CONSTRAINT;

			int rowSize = rowColSizes[bIx2];

			for (int j = 0; j < linkedGroups.size(); j++) {
				LinkedDimGroup group = linkedGroups.get(j);
				int groupSize = rowSize;
				if (group.span > 1)
					groupSize = LayoutUtil.sum(rowColSizes, bIx2, Math.min((group.span << 1) - 1, rowColSizes.length - bIx2 - 1));

				group.layout(primDC, curPos, groupSize, group.span);
			}

			curPos += (fromEnd ? -rowSize : rowSize);
		}
	}

	private void addToSizeGroup(HashMap<String, int[]> sizeGroups, String sizeGroup, int[] size)
	{
		if (sizeGroups == null)
			return;

		int[] sgSize = sizeGroups.get(sizeGroup);
		if (sgSize == null) {
			sizeGroups.put(sizeGroup, new int[] {size[LayoutUtil.MIN], size[LayoutUtil.PREF], size[LayoutUtil.MAX]});
		} else {
			sgSize[LayoutUtil.MIN] = Math.max(size[LayoutUtil.MIN], sgSize[LayoutUtil.MIN]);
			sgSize[LayoutUtil.PREF] = Math.max(size[LayoutUtil.PREF], sgSize[LayoutUtil.PREF]);
			sgSize[LayoutUtil.MAX] = Math.min(size[LayoutUtil.MAX], sgSize[LayoutUtil.MAX]);
		}
	}

	private HashMap<String, Integer> addToEndGroup(HashMap<String, Integer> endGroups, String endGroup, int end)
	{
		if (endGroup != null) {
			if (endGroups == null)
				endGroups = new HashMap<String, Integer>(2);

			Integer oldEnd = endGroups.get(endGroup);
			if (oldEnd == null || end > oldEnd.intValue())
				endGroups.put(endGroup, new Integer(end));
		}
		return endGroups;
	}

	/** Caclulates Min and Preferred size for the columns or rows.
	 * @return The sizes in a {@link net.miginfocom.layout.Grid.FlowSizeSpec}.
	 */
	private FlowSizeSpec calcRowsOrColsSizes(ArrayList<LinkedDimGroup>[] groupsLists, int refSize, boolean isHor)
	{
		DimConstraint[] primDCs = (isHor? colConstr : rowConstr).getConstaints();
		TreeSet<Integer> primIdexes = isHor ? colIndexes : rowIndexes;

		int[][] rowColBoundSizes = new int[primIdexes.size()][];
		HashMap<String, int[]> sizeGroupMap = new HashMap<String, int[]>(2);
		DimConstraint[] allDCs = new DimConstraint[primIdexes.size()];

		Iterator<Integer> primIt = primIdexes.iterator();
		for (int r = 0; r < rowColBoundSizes.length; r++) {
			int cellIx = primIt.next().intValue();
			int[] rowColSizes = new int[3];

			if (cellIx >= -MAX_GRID && cellIx <= MAX_GRID) {  // If not dock cell
				allDCs[r] = primDCs[cellIx >= primDCs.length ? primDCs.length - 1 : cellIx];
			} else {
				allDCs[r] = DOCK_DIM_CONSTRAINT;
			}

			ArrayList<LinkedDimGroup> groups = groupsLists[r];

			int[] groupSizes = new int[] {LayoutUtil.NOT_SET, LayoutUtil.NOT_SET, LayoutUtil.NOT_SET};
			for (int sType = LayoutUtil.MIN; sType <= LayoutUtil.MAX; sType++)
				groupSizes[sType] = getTotalGroupsSizeParallel(groups, sType, false);

			for (int sType = LayoutUtil.MIN; sType <= LayoutUtil.MAX; sType++) {

				int rowColSize = groupSizes[sType];

				UnitValue uv = allDCs[r].getSize() != null ? allDCs[r].getSize().getSize(sType) : null;
				if (uv != null) {
					// If the size of the column is a link to some other size, use that instead
					if (uv.getUnit() == UnitValue.PREF_SIZE) {
						rowColSize = groupSizes[LayoutUtil.PREF];
					} else if (uv.getUnit() == UnitValue.MIN_SIZE) {
						rowColSize = groupSizes[LayoutUtil.MIN];
					} else if (uv.getUnit() == UnitValue.MAX_SIZE) {
						rowColSize = groupSizes[LayoutUtil.MAX];
					} else {
						rowColSize = uv.getPixels(refSize, parent, null);
					}
				} else if (cellIx >= -MAX_GRID && cellIx <= MAX_GRID && rowColSize == 0) {
					rowColSize = LayoutUtil.isDesignTime() ? 15 : 0;    // Empty rows with no size set gets 15 pixels if design time
				}

				rowColSizes[sType] = rowColSize;
			}

			addToSizeGroup(sizeGroupMap, allDCs[r].getSizeGroup(), rowColSizes);

			rowColBoundSizes[r] = rowColSizes;
		}

		// Set/equalize the size groups to same the values.
		if (sizeGroupMap.size() > 0) {
			for (int r = 0; r < rowColBoundSizes.length; r++) {
				if (allDCs[r].getSizeGroup() != null)
					rowColBoundSizes[r] = sizeGroupMap.get(allDCs[r].getSizeGroup());
			}
		}

		// Add the gaps
		UnitValue tlIns = LayoutUtil.getInsets(lc, isHor ? 1 : 0);
		UnitValue brIns = LayoutUtil.getInsets(lc, isHor ? 3 : 2);
		ResizeConstraint[] resConstrs = getRowResizeConstraints(allDCs);
		int[][] gapSizes = getGaps(allDCs, refSize, isHor);
		FlowSizeSpec fss = mergeSizesGapsAndResConstrs(resConstrs, rowColBoundSizes, gapSizes, tlIns, brIns, refSize, parent);

		// Spanning components are not handled yet. Check and adjust the multi-row min/pref they enforce.
		adjustMinPrefForSpanningComps(allDCs, fss, groupsLists);

		return fss;
	}

	private static int[] getMinPrefMaxSumSize(int[][] sizes)
	{
		int[] retSizes = new int[3];
		for (int i = 0; i < sizes.length; i++) {
			for (int sType = LayoutUtil.MIN; sType <= LayoutUtil.MAX; sType++) {
				if (sizes[i] != null) {
					int s = sizes[i][sType];
					if (s != LayoutUtil.NOT_SET) {
						if (sType == LayoutUtil.PREF) {
							int min = sizes[i][LayoutUtil.MIN];
							if (min > s)    // Includes s == LayoutUtil.NOT_SET since < 0.
								s = min;
						}

						retSizes[sType] += s;   // MAX compensated below.
					}
				}
			}
			// So that MAX is always correct.
			if (sizes[i] != null && sizes[i][LayoutUtil.MAX] == LayoutUtil.NOT_SET)
				retSizes[LayoutUtil.MAX] = LayoutUtil.INF;
		}

		return retSizes;
	}

	private static ResizeConstraint[] getRowResizeConstraints(DimConstraint[] specs)
	{
		ResizeConstraint[] resConsts = new ResizeConstraint[specs.length];
		for (int i = 0; i < resConsts.length; i++)
			resConsts[i] = specs[i].resize;
		return resConsts;
	}

	private static ResizeConstraint[] getComponentResizeConstraints(ArrayList<CompWrap> compWraps, boolean isHor)
	{
		ResizeConstraint[] resConsts = new ResizeConstraint[compWraps.size()];
		for (int i = 0; i < resConsts.length; i++) {
			CC fc = compWraps.get(i).cc;
			resConsts[i] = (isHor ? fc.getHorizontal() : fc.getVertical()).resize;

			// Always grow docking components in the correct dimension.
			int dock = fc.getDockSide();
			if (isHor ? (dock == 0 || dock == 2) : (dock == 1 || dock == 3)) {
				ResizeConstraint dc = resConsts[i];
				resConsts[i] = new ResizeConstraint(dc.shrinkPrio, dc.shrink, dc.growPrio, new Float(100));
			}
		}
		return resConsts;
	}

	private int[][] getGaps(DimConstraint[] specs, int refSize, boolean isHor)
	{
		BoundSize defGap = isHor ? lc.getGridGapX() : lc.getGridGapY();
		if (defGap == null)
			defGap = isHor ? PlatformDefaults.getGridGapX() : PlatformDefaults.getGridGapY();

        int[][] retValues = new int[specs.length << 1][];
        for (int i = 0, bIx = 0; i < specs.length; i++) {
	        DimConstraint spec = specs[i];
	        bIx = i << 1;

	        // If the spec is the first/last (disregarding dock-rows)
	        boolean zAll = specs[i] == DOCK_DIM_CONSTRAINT;
	        boolean zBef = (zAll || i == 0 || specs[i - 1] == DOCK_DIM_CONSTRAINT);
	        boolean zAft = (zAll || i == specs.length - 1 || specs[i + 1] == DOCK_DIM_CONSTRAINT);

			retValues[bIx] = zBef ? new int[] {0, 0, 0} : spec.getRowGaps(parent, defGap,refSize, true);
			retValues[bIx + 1] = zAft ? new int[] {0, 0, 0} : spec.getRowGaps(parent, defGap, refSize, false);
        }
        return retValues;
    }

	private static int[][] getGaps(ArrayList<CompWrap> compWraps, boolean isHor)
	{
		int compCount = compWraps.size();
        int[][] retValues = new int[compCount << 1][];
        for (int i = 0, bIx = 0; i < compCount; i++) {
	        CompWrap cw = compWraps.get(i);
			retValues[bIx++] = cw.getGaps(isHor, true);
			retValues[bIx++] = cw.getGaps(isHor, false);
        }
        return retValues;
    }

	/**
	 * @param specs The specs for the columns or rows. Last index will be used of <code>count</code> is greater than this array's length.
	 * @param fss
	 * @param groupsLists
	 */
	private void adjustMinPrefForSpanningComps(DimConstraint[] specs, FlowSizeSpec fss, ArrayList<LinkedDimGroup>[] groupsLists)
	{
		for (int r = 0; r < groupsLists.length; r++) {
			ArrayList<LinkedDimGroup> groups = groupsLists[r];

			for (int i = 0; i < groups.size(); i++) {
				LinkedDimGroup group = groups.get(i);
				if (group.span == 1)
					continue;

				for (int sType = LayoutUtil.MIN; sType <= LayoutUtil.PREF; sType++) {
					int cSize = group.getMinPrefMax()[sType];
					if (cSize == LayoutUtil.NOT_SET)
						continue;

					int rowSize = 0;
					int sIx = (r << 1) + 1;
					int len = Math.min((group.span << 1), fss.sizes.length - sIx) - 1;
					for (int j = sIx; j < sIx + len; j++) {
						int sz = fss.sizes[j][sType];
						if (sz != LayoutUtil.NOT_SET)
							rowSize += sz;
					}

					if (rowSize < cSize) {
						for (int eag = 0, newRowSize = 0; eag < 4 && newRowSize < cSize; eag++)
							newRowSize = fss.expandSizes(specs, cSize, sIx, len, sType, eag);
					}
				}
			}
		}
	}

	/** For one dimension divide the component wraps into logical groups. One group for component wraps that share a common something,
	 * line the property to layout by base line.
	 * @param isRows If rows, and not columns, are to be divided.
	 * @return One <code>ArrayList<LinkedDimGroup></code> for every row/column.
	 */
	private ArrayList<LinkedDimGroup>[] divideIntoLinkedGroups(boolean isRows)
	{
		boolean fromEnd = !(isRows ? lc.isTopToBottom() : LayoutUtil.isLeftToRight(lc, parent));
		TreeSet<Integer> primIndexes = isRows ? rowIndexes : colIndexes;
		TreeSet<Integer> secIndexes = isRows ? colIndexes : rowIndexes;
		DimConstraint[] primDCs = (isRows ? rowConstr : colConstr).getConstaints();

		@SuppressWarnings("unchecked")
		ArrayList<LinkedDimGroup>[] groupLists = new ArrayList[primIndexes.size()];

		int gIx = 0;
		for (Iterator<Integer> primIt = primIndexes.iterator(); primIt.hasNext();) {
			int i = primIt.next().intValue();

			DimConstraint dc;
			if (i >= -MAX_GRID && i <= MAX_GRID) {  // If not dock cell
				dc = primDCs[i >= primDCs.length ? primDCs.length - 1 : i];
			} else {
				dc = DOCK_DIM_CONSTRAINT;
			}

			ArrayList<LinkedDimGroup> groupList = new ArrayList<LinkedDimGroup>(2);
			groupLists[gIx++] = groupList;

			for (Iterator<Integer> secIt = secIndexes.iterator(); secIt.hasNext();) {
				int j = secIt.next().intValue();
				Cell cell = isRows ? getCell(i, j) : getCell(j, i);
				if (cell == null || cell.compWraps.size() == 0)
					continue;

				int span = convertSpanToSparseGrid(i, (isRows ? cell.spany : cell.spanx), primIndexes);
				boolean isPar = cell.flowx == isRows;

				if ((isPar == false && cell.compWraps.size() > 1) || span > 1) {

					int linkType = isPar ? LinkedDimGroup.TYPE_PARALLEL : LinkedDimGroup.TYPE_SERIAL;
					LinkedDimGroup lg = new LinkedDimGroup("p," + j, span, linkType, !isRows, fromEnd);
					lg.setCompWraps(cell.compWraps);
					groupList.add(lg);
				} else {
					for (int cwIx = 0; cwIx < cell.compWraps.size(); cwIx++) {
						CompWrap cw = cell.compWraps.get(cwIx);
						boolean rowBaselineAlign = isRows && dc.getAlignOrDefault(!isRows) == UnitValue.BASELINE_IDENTITY;

						String linkCtx = cw.getLinkContext(rowBaselineAlign);

						// Find a group with same link context and put it in that group.
						boolean foundList = false;
						for (int glIx = 0, lastGl = groupList.size() - 1; glIx <= lastGl ; glIx++) {
							LinkedDimGroup group = groupList.get(glIx);
							if (group.linkCtx == linkCtx || linkCtx != null && linkCtx.equals(group.linkCtx)) {
								group.addCompWrap(cw);
								foundList = true;
								break;
							}
						}

						// If no found and at last add a new group.
						if (foundList == false) {

							int linkType = LinkedDimGroup.TYPE_PARALLEL;
							if (isRows && cw.isBaselineAlign(rowBaselineAlign))
								linkType = LinkedDimGroup.TYPE_BASELINE;

							LinkedDimGroup lg = new LinkedDimGroup(linkCtx, 1, linkType, !isRows, fromEnd);
							lg.addCompWrap(cw);
							groupList.add(lg);
						}
					}
				}
			}
		}
		return groupLists;
	}

	/** Spanning is specified in the uncompressed grid number. They can for instance be more than 60000 for the outer
	 * edge dock grid cells. When the grid is compressed and indexed after only the cells that area occupied the span
	 * is erratic. This method use the row/col indexes and corrects the span to be correct for the compressed grid.
	 * @param span The span un the uncompressed grid. <code>LayoutUtil.INF</code> will be interpreted to span the rest
	 * of the column/row excluding the surrounding docking components.
	 * @param indexes The indexes in the correct dimension.
	 * @return The converted span.
	 */
	private int convertSpanToSparseGrid(int curIx, int span, TreeSet<Integer> indexes)
	{
		int lastIx = curIx + span;
		int retSpan = 1;
		for (Iterator<Integer> it = indexes.iterator(); it.hasNext();) {
			int ix = it.next();
			if (ix <= curIx)
				continue;   // We have not arrive to the correct index yet

			if (ix >= lastIx)
				break;

			retSpan++;
		}
		return retSpan;
	}

	private final boolean isCellFree(int r, int c, ArrayList<int[]> occupiedRects)
	{
		if (getCell(r, c) != null)
			return false;

		for (int i = 0; i < occupiedRects.size(); i++) {
			int[] rect = occupiedRects.get(i);
			if (rect[0] <= c && rect[1] <= r  && rect[0] + rect[2] > c && rect[1] + rect[3] > r)
				return false;
		}
		return true;
	}

	private Cell getCell(int r, int c)
	{
		return grid.get(new Integer((r << 16) + c));
	}

	private void setCell(int r, int c, Cell cell)
	{
		if (c < 0 || c > MAX_GRID || r < 0 || r > MAX_GRID)
			throw new IllegalArgumentException("Cell position out of bounds. row: " + r + ", col: " + c);

		rowIndexes.add(new Integer(r));
		colIndexes.add(new Integer(c));

		grid.put(new Integer((r << 16) + c), cell);
	}

	/** Adds a docking cell. That cell is outside the normal cell indexes.
	 * @param dockInsets The current dock insets. Will be updated!
	 * @param side top == 0, left == 1, bottom = 2, right = 3.
	 * @param cw The compwrap to put in a cell and add.
	 */
	private void addDockingCell(int[] dockInsets, int side, CompWrap cw)
	{
		int r, c, spanx = 1, spany = 1;
		switch (side) {
			case 0:
			case 2:
				r = side == 0 ? dockInsets[0]++ : dockInsets[2]--;
				c = dockInsets[1];
				spanx = dockInsets[3] - dockInsets[1] + 1;  // The +1 is for cell 0.
				break;

			case 1:
			case 3:
				c = side == 1 ? dockInsets[1]++ : dockInsets[3]--;
				r = dockInsets[0];
				spany = dockInsets[2] - dockInsets[0] + 1;  // The +1 is for cell 0.
				break;

			default:
				throw new IllegalArgumentException("Internal error 123.");
		}

		rowIndexes.add(new Integer(r));
		colIndexes.add(new Integer(c));

		grid.put(new Integer((r << 16) + c), new Cell(cw, spanx, spany, spanx > 1));
	}

	/** A simple representation of a cell in the grid. Contains a number of component wraps and if they span more than one cell.
	 */
	private static class Cell
	{
		private final int spanx, spany;
		private final boolean flowx;
		private final ArrayList<CompWrap> compWraps = new ArrayList<CompWrap>(1);

		private boolean hasTagged = false;  // If one or more components have styles and need to be checked by the component sorter

		private Cell(CompWrap cw)
		{
			this(cw, 1, 1, true);
		}

		private Cell(int spanx, int spany, boolean flowx)
		{
			this(null, spanx, spany, flowx);
		}

		private Cell(CompWrap cw, int spanx, int spany, boolean flowx)
		{
			if (cw != null)
				compWraps.add(cw);
			this.spanx = spanx;
			this.spany = spany;
			this.flowx = flowx;
		}
	}

	/** A number of component wraps that share a layout "something" <b>in one dimension</b>
	 */
	private static class LinkedDimGroup
	{
		private static final int TYPE_SERIAL = 0;
		private static final int TYPE_PARALLEL = 1;
		private static final int TYPE_BASELINE = 2;

		private final String linkCtx;
		private final int span;
		private final int linkType;
		private final boolean isHor, fromEnd;

		private ArrayList<CompWrap> _compWraps = new ArrayList<CompWrap>(4);

		private int[] sizes = null;
		private int lStart = 0, lSize = 0;  // Currently mostly for debug painting

		private LinkedDimGroup(String linkCtx, int span, int linkType, boolean isHor, boolean fromEnd)
		{
			this.linkCtx = linkCtx;
			this.span = span;
			this.linkType = linkType;
			this.isHor = isHor;
			this.fromEnd = fromEnd;
		}

		private void addCompWrap(CompWrap cw)
		{
			_compWraps.add(cw);
			sizes = null;
		}

		private void setCompWraps(ArrayList<CompWrap> cws)
		{
			if (_compWraps != cws) {
				_compWraps = cws;
				sizes = null;
			}
		}

		private void layout(DimConstraint dc, int start, int size, int spanCount)
		{
			lStart = start;
			lSize = size;

			if (_compWraps.size() == 0)
				return;

			ContainerWrapper parent = _compWraps.get(0).comp.getParent();
			if (linkType == TYPE_PARALLEL) {
				layoutParallel(parent, _compWraps, dc, start, size, isHor, spanCount, fromEnd);
			} else if (linkType == TYPE_BASELINE) {
				layoutBaseline(parent, _compWraps, dc, start, size, LayoutUtil.PREF, spanCount);
			} else {
				layoutSerial(parent, _compWraps, dc, start, size, isHor, spanCount, fromEnd);
			}
		}

		/** Returns the min/pref/max sizes for this cell. Returned array <b>must not be altered</b>
		 * @return A shared min/pref/max array of sizes. Always of length 3 and never <code>null</code>. Will always be of type STATIC and PIXEL.
		 */
		private int[] getMinPrefMax()
		{
			if (sizes == null && _compWraps.size() > 0) {
				sizes = new int[3];
				for (int sType = LayoutUtil.MIN; sType <= LayoutUtil.PREF; sType++) {
					if (linkType == TYPE_PARALLEL) {
						sizes[sType] = getTotalSizeParallel(_compWraps, sType, isHor);
					} else if (linkType == TYPE_BASELINE) {
						int[] aboveBelow = getBaselineAboveBelow(_compWraps, sType, false);
						sizes[sType] = aboveBelow[0] + aboveBelow[1];
					} else {
						sizes[sType] = getTotalSizeSerial(_compWraps, sType, isHor);
					}
				}
				sizes[LayoutUtil.MAX] = LayoutUtil.INF;
			}
			return sizes;
		}
	}

	/** Wraps a {@link java.awt.Component} together with its constraint. Caches a lot of information about the component so
	 * for instance not the preferred size has to be caclulated more than once.
	 */
	private static class CompWrap
	{
		private final ComponentWrapper comp;
		private final CC cc;
		private int[][] gaps; // [top,left(actually before),bottom,right(actually after)][min,pref,max]

		private final int[] horSizes = new int[3];
		private final int[] verSizes = new int[3];

		private int x = LayoutUtil.NOT_SET, y = LayoutUtil.NOT_SET, w = LayoutUtil.NOT_SET, h = LayoutUtil.NOT_SET;

		private CompWrap(ComponentWrapper c, CC cc, int hideMode)
		{
			this.comp = c;
			this.cc = cc;

			for (int i = LayoutUtil.MIN; i <= LayoutUtil.MAX; i++) {
				horSizes[i] = hideMode <= 0 ? getSize(cc.getHorizontal().getSize(), i, true) : 0;
				verSizes[i] = hideMode <= 0 ? getSize(cc.getVertical().getSize(), i, false) : 0;
			}

			if (hideMode <= 0) {
				for (int i = 0; i < 2; i++) {   // Hor, ver
					int[] sz = i == 0 ? horSizes : verSizes;
					if (sz[LayoutUtil.PREF] > sz[LayoutUtil.MAX])
						sz[LayoutUtil.PREF] = sz[LayoutUtil.MAX];
					if (sz[LayoutUtil.PREF] < sz[LayoutUtil.MIN])
						sz[LayoutUtil.PREF] = sz[LayoutUtil.MIN];
				}
			}

			if (hideMode > 1) {
				gaps = new int[4][];
				for (int i = 0; i < gaps.length; i++)
					gaps[i] = new int[] {0, 0, 0};
			}
		}

		private void calcGaps(ComponentWrapper before, CC befCC, ComponentWrapper after, CC aftCC, String tag, boolean flowX, boolean isLTR)
		{
			ContainerWrapper par = comp.getParent();
			int parW = par.getWidth();
			int parH = par.getHeight();

			BoundSize befGap = before != null ? (flowX ? befCC.getHorizontal() : befCC.getVertical()).getGapAfter() : null;
			BoundSize aftGap = after != null ? (flowX ? aftCC.getHorizontal() : aftCC.getVertical()).getGapBefore() : null;

			mergeGapSizes(cc.getVertical().getComponentGaps(par, comp, befGap, (flowX ? null : before), tag, parH, 0, isLTR), false, true);
			mergeGapSizes(cc.getHorizontal().getComponentGaps(par, comp, befGap, (flowX ? before : null), tag, parW, 1, isLTR), true, true);
			mergeGapSizes(cc.getVertical().getComponentGaps(par, comp, aftGap, (flowX ? null : after), tag, parH, 2, isLTR), false, false);
			mergeGapSizes(cc.getHorizontal().getComponentGaps(par, comp, aftGap, (flowX ? after : null), tag, parW, 3, isLTR), true, false);
		}

		private void setDimBounds(int start, int size, boolean isHor)
		{
			if (isHor) {
				x = start;
				w = size;
			} else {
				y = start;
				h = size;
			}
		}

		private final void transferBounds()
		{
			comp.setBounds(x, y, w, h);
		}

		private void setSizes(int[] sizes, boolean isHor)
		{
			if (sizes == null)
				return;

			int[] s = isHor ? horSizes : verSizes;
			for (int i = 0; i < 3; i++)
				s[i] = sizes[i];
		}

		private void mergeGapSizes(int[] sizes, boolean isHor, boolean isTL)
		{
			if (gaps == null)
				gaps = new int[][] {null, null, null, null};

			if (sizes == null)
				return;

			int gapIX = getGapIx(isHor, isTL);
			int[] oldGaps = gaps[gapIX];
			if (oldGaps == null) {
				oldGaps = new int[] {0, 0, LayoutUtil.INF};
				gaps[gapIX] = oldGaps;
			}

			oldGaps[LayoutUtil.MIN] = Math.max(sizes[LayoutUtil.MIN], oldGaps[LayoutUtil.MIN]);
			oldGaps[LayoutUtil.PREF] = Math.max(sizes[LayoutUtil.PREF], oldGaps[LayoutUtil.PREF]);
			oldGaps[LayoutUtil.MAX] = Math.min(sizes[LayoutUtil.MAX], oldGaps[LayoutUtil.MAX]);
		}

		private int getGapIx(boolean isHor, boolean isTL)
		{
			return isHor ? (isTL ? 1 : 3) : (isTL ? 0 : 2);
		}

		private final String getLinkContext(boolean defBaseline)
		{
			return isBaselineAlign(defBaseline) ? "baseline" : null;
		}

		private final int getSizeInclGaps(int sizeType, boolean isHor)
		{
			return filter(sizeType, getGapBefore(sizeType, isHor) + getSize(sizeType, isHor) + getGapAfter(sizeType, isHor));
		}

		private final int getSize(int sizeType, boolean isHor)
		{
			return filter(sizeType, isHor ? horSizes[sizeType] : verSizes[sizeType]);
		}

		private final int getGapBefore(int sizeType, boolean isHor)
		{
			int[] gaps = getGaps(isHor, true);
			return gaps != null ? filter(sizeType, gaps[sizeType]) : 0;
		}

		private final int getGapAfter(int sizeType, boolean isHor)
		{
			int[] gaps = getGaps(isHor, false);
			return gaps != null ? filter(sizeType, gaps[sizeType]) : 0;
		}

		private final int[] getGaps(boolean isHor, boolean isTL)
		{
			return gaps[getGapIx(isHor, isTL)];
		}

		private final int filter(int sizeType, int size)
		{
			if (size == LayoutUtil.NOT_SET)
				return sizeType != LayoutUtil.MAX ? 0 : LayoutUtil.INF;
			return constrainSize(size);
		}

		private final int getSize(BoundSize uvs, int sizeType, boolean isHor)
		{
			if (uvs == null || uvs.getSize(sizeType) == null) {
				switch(sizeType) {
					case LayoutUtil.MIN:
						return isHor ? comp.getMinimumWidth() : comp.getMinimumHeight();
					case LayoutUtil.PREF:
						return isHor ? comp.getPreferredWidth() : comp.getPreferredHeight();
					default:
						return isHor ? comp.getMaximumWidth() : comp.getMaximumHeight();
				}
			}

			ContainerWrapper par = comp.getParent();
			return uvs.getSize(sizeType).getPixels(isHor ? par.getWidth() : par.getHeight(), par, comp);
		}

		public boolean isBaselineAlign(boolean defValue)
		{
			UnitValue al = cc.getVertical().getAlign();
			return comp.hasBaseline() && (al != null ? al == UnitValue.BASELINE_IDENTITY : defValue);
		}

		public int getBaseline(int sizeType)
		{
			return comp.getBaseline(getSize(sizeType, true), getSize(sizeType, false));
		}
	}

	//***************************************************************************************
	//* Helper Methods
	//***************************************************************************************

	private static void layoutBaseline(ContainerWrapper parent, ArrayList<CompWrap> compWraps, DimConstraint dc, int start, int size, int sizeType, int spanCount)
	{
		int[] aboveBelow = getBaselineAboveBelow(compWraps, sizeType, true);
		int blRowSize = aboveBelow[0] + aboveBelow[1];

		CC cc = compWraps.get(0).cc;

		// Align for the whole baseline component array
		UnitValue align = cc.getVertical().getAlign();
		if (spanCount == 1 && align == null)
			align = dc.getAlignOrDefault(false);
		if (align == UnitValue.BASELINE_IDENTITY)
			align = UnitValue.CENTER;

		int offset = start + aboveBelow[0] + (align != null ? Math.max(0, align.getPixels(size - blRowSize, parent, null)) : 0);
		for (int i = 0, iSz = compWraps.size(); i < iSz; i++) {
			CompWrap cw = compWraps.get(i);
			cw.y += offset;

			if (cw.y + cw.h > start + size)
				cw.h = start + size - cw.y;
		}
	}

	private static void layoutSerial(ContainerWrapper parent, ArrayList<CompWrap> compWraps, DimConstraint dc, int start, int size, boolean isHor, int spanCount, boolean fromEnd)
	{
		FlowSizeSpec fss = mergeSizesGapsAndResConstrs(
				getComponentResizeConstraints(compWraps, isHor),
				getComponentSizes(compWraps, isHor),
				getGaps(compWraps, isHor),
				null, null, 0, null
		);

		int[] sizes = LayoutUtil.calculateSerial(fss.sizes, fss.resConstsInclGaps, spanCount == 1 && dc.isFill(), LayoutUtil.PREF, size);
		setCompWrapBounds(parent, sizes, compWraps, dc.getAlignOrDefault(isHor),  start, size, isHor, fromEnd);
	}

	private static void setCompWrapBounds(ContainerWrapper parent, int[] allSizes, ArrayList<CompWrap> compWraps, UnitValue rowAlign,  int start, int size, boolean isHor, boolean fromEnd)
	{
		int totSize = LayoutUtil.sum(allSizes);
		CC cc = compWraps.get(0).cc;
		UnitValue align = correctAlign(cc, rowAlign, isHor, fromEnd);

		int cSt = start;
		int slack = size - totSize;
		if (slack > 0 && align != null) {
			int al = Math.min(slack, Math.max(0, align.getPixels(slack, parent, null)));
			cSt += (fromEnd ? -al : al);
		}

		for (int i = 0, bIx = 0, iSz = compWraps.size(); i < iSz; i++) {
			CompWrap cw = compWraps.get(i);
			if (fromEnd ) {
				cSt -= allSizes[bIx++];
				cw.setDimBounds(cSt - allSizes[bIx], allSizes[bIx], isHor);
				cSt -= allSizes[bIx++];
			} else {
				cSt += allSizes[bIx++];
				cw.setDimBounds(cSt, allSizes[bIx], isHor);
				cSt += allSizes[bIx++];
			}
		}
	}

	private static void layoutParallel(ContainerWrapper parent, ArrayList<CompWrap> compWraps, DimConstraint dc, int start, int size, boolean isHor, int spanCount, boolean fromEnd)
	{
		int[][] sizes = new int[compWraps.size()][];    // [compIx][gapBef,compSize,gapAft]

		for (int i = 0; i < sizes.length; i++) {
			CompWrap cw = compWraps.get(i);

			ResizeConstraint[] resConstr = new ResizeConstraint[] {
					GAP_RC_CONST, (isHor ? cw.cc.getHorizontal() : cw.cc.getVertical()).resize, GAP_RC_CONST
			};

			int[][] sz = new int[][] {
					cw.getGaps(isHor, true), (isHor ? cw.horSizes : cw.verSizes), cw.getGaps(isHor, false)
			};

			sizes[i] = LayoutUtil.calculateSerial(sz, resConstr, spanCount == 1 && dc.isFill(), LayoutUtil.PREF, size);
		}

		UnitValue rowAlign = spanCount == 1 ? dc.getAlignOrDefault(isHor) : null;
		setCompWrapBounds(parent, sizes, compWraps, rowAlign, start, size, isHor, fromEnd);
	}

	private static void setCompWrapBounds(ContainerWrapper parent, int[][] sizes, ArrayList<CompWrap> compWraps, UnitValue rowAlign,  int start, int size, boolean isHor, boolean fromEnd)
	{
		for (int i = 0; i < sizes.length; i++) {
			CompWrap cw = compWraps.get(i);

			UnitValue align = correctAlign(cw.cc, rowAlign, isHor, fromEnd);

			int[] cSizes = sizes[i];
			int gapBef = cSizes[0];
			int cSize = cSizes[1];  // No Math.min(size, cSizes[1]) here!
			int gapAft = cSizes[2];

			int cSt = fromEnd ? start - gapBef : start + gapBef;
			int slack = size - cSize - gapBef - gapAft;
			if (slack > 0 && align != null) {
				int al = Math.min(slack, Math.max(0, align.getPixels(slack, parent, null)));
				cSt += (fromEnd ? -al : al);
			}

			cw.setDimBounds(fromEnd ? cSt - cSize : cSt, cSize, isHor);
		}
	}

	private static UnitValue correctAlign(CC cc, UnitValue rowAlign, boolean isHor, boolean fromEnd)
	{
		UnitValue align = (isHor ? cc.getHorizontal() : cc.getVertical()).getAlign();
		if (align == null)
			align = rowAlign;
		if (align == UnitValue.BASELINE_IDENTITY)
			align = UnitValue.CENTER;

		if (fromEnd) {
			if (align == UnitValue.LEFT)
				align = UnitValue.RIGHT;
			else if (align == UnitValue.RIGHT)
				align = UnitValue.LEFT;
		}
		return align;
	}

	private static int[] getBaselineAboveBelow(ArrayList<CompWrap> compWraps, int sType, boolean centerBaseline)
	{
		int maxAbove = Short.MIN_VALUE;
		int maxBelow = Short.MIN_VALUE;
		for (int i = 0, iSz = compWraps.size(); i < iSz; i++) {
			CompWrap cw = compWraps.get(i);

			int height = cw.getSize(sType, false);
			if (height >= LayoutUtil.INF)
				return new int[] {LayoutUtil.INF / 2, LayoutUtil.INF / 2};

			int baseline = cw.getBaseline(sType);
			int above = baseline + cw.getGapBefore(sType, false);
			maxAbove = Math.max(above, maxAbove);
			maxBelow = Math.max(height - baseline + cw.getGapAfter(sType, false), maxBelow);

			if (centerBaseline)
				cw.setDimBounds(-baseline, height, false);
		}
		return new int[] {maxAbove, maxBelow};
	}

	private static int getTotalSizeParallel(ArrayList<CompWrap> compWraps, int sType, boolean isHor)
	{
		int size = sType == LayoutUtil.MAX ? LayoutUtil.INF : 0;

		for (int i = 0, iSz = compWraps.size(); i < iSz; i++) {
			CompWrap cw = compWraps.get(i);
			int cwSize = cw.getSizeInclGaps(sType, isHor);
			if (cwSize >= LayoutUtil.INF)
				return LayoutUtil.INF;

			if (sType == LayoutUtil.MAX ? cwSize < size : cwSize > size)
		        size = cwSize;
		}
		return constrainSize(size);
	}

	private static int getTotalSizeSerial(ArrayList<CompWrap> compWraps, int sType, boolean isHor)
	{
		int totSize = 0;
		for (int i = 0, iSz = compWraps.size(), lastGapAfter = 0; i < iSz; i++) {
			CompWrap wrap = compWraps.get(i);
			int gapBef = wrap.getGapBefore(sType, isHor);
			if (gapBef > lastGapAfter)
				totSize += gapBef - lastGapAfter;

			totSize += wrap.getSize(sType, isHor);
			totSize += (lastGapAfter = wrap.getGapAfter(sType, isHor));

			if (totSize >= LayoutUtil.INF)
				return LayoutUtil.INF;
		}
		return constrainSize(totSize);
	}

	private static int getTotalGroupsSizeParallel(ArrayList<LinkedDimGroup> groups, int sType, boolean countSpanning)
	{
		int size = sType == LayoutUtil.MAX ? LayoutUtil.INF : 0;
		for (int i = 0, iSz = groups.size(); i < iSz; i++) {
			LinkedDimGroup group = groups.get(i);
			if (countSpanning || group.span == 1) {
				int grpSize = group.getMinPrefMax()[sType];
				if (grpSize >= LayoutUtil.INF)
					return LayoutUtil.INF;

				if (sType == LayoutUtil.MAX ? grpSize < size : grpSize > size)
			        size = grpSize;
			}
		}
		return constrainSize(size);
	}

	/**
	 * @param compWraps
	 * @param isHor
	 * @return Might contain LayoutUtil.NOT_SET
	 */
	private static int[][] getComponentSizes(ArrayList<CompWrap> compWraps, boolean isHor)
	{
		int[][] compSizes = new int[compWraps.size()][];
		for (int i = 0; i < compSizes.length; i++) {
			CompWrap cw = compWraps.get(i);
			compSizes[i] = isHor ? cw.horSizes : cw.verSizes;
		}
		return compSizes;
	}

	/** Merges sizes and gaps together with Resize Constraints. For gaps {@link #GAP_RC_CONST} is used.
	 * @param resConstr One resize constriant for every row/component. Can be lesser in length and the last element should be used for missing elements.
	 * @param minPrefMaxSizes The sizes (min/pref/max) for every row/component.
	 * @param gapSizes The gaps before and after each row/component packed in one double sized array.
	 * @param firstGap If the first gap should be special, set it here. If <code>null</code> the gap in <code>gapSizes</code> will be used as normal.
	 * @param lastGap If the last gap should be special, set it here. If <code>null</code> the gap in <code>gapSizes</code> will be used as normal.
	 * @param refSize The reference size to get the gap in pixels.
	 * @param parent The parent used to get the gap in pixels.
	 * @return A holder for the merged values.
	 */
	private static FlowSizeSpec mergeSizesGapsAndResConstrs(ResizeConstraint[] resConstr, int[][] minPrefMaxSizes, int[][] gapSizes, UnitValue firstGap, UnitValue lastGap, int refSize, ContainerWrapper parent)
	{
		int[][] sizes = new int[(minPrefMaxSizes.length << 1) + 1][];  // Make room for gaps around.
		ResizeConstraint[] resConstsInclGaps = new ResizeConstraint[sizes.length];
		BoundSize firstGapBS = firstGap != null ? new BoundSize(firstGap, null) : null;
		BoundSize lastGapBS = lastGap != null ? new BoundSize(lastGap, null) : null;

		for (int i = 0; i < minPrefMaxSizes.length; i++) {

			// Component bounds and constraints
			int compIx = (i << 1) + 1;
			resConstsInclGaps[compIx] = resConstr[i < resConstr.length ? i : resConstr.length - 1];
			sizes[compIx] = minPrefMaxSizes[i];

			// Gap bounds and constraints
			int gapIx = compIx >= gapSizes.length ? gapSizes.length - 1 : compIx;

			int[] gapBefore = firstGap == null || i != 0 ? gapSizes[gapIx - 1] : firstGapBS.getPixelSizes(refSize, parent, null);
			int[] gapAfter = lastGap == null || i < minPrefMaxSizes.length - 1 ? gapSizes[gapIx] : lastGapBS.getPixelSizes(refSize, parent, null);

			sizes[compIx - 1] = mergeUnitValues(sizes[compIx - 1], gapBefore);
			sizes[compIx + 1] = gapAfter;

			resConstsInclGaps[compIx - 1] = sizes[compIx - 1] != null ? GAP_RC_CONST : null;
			resConstsInclGaps[compIx + 1] = sizes[compIx + 1] != null ? GAP_RC_CONST : null;
		}
		return new FlowSizeSpec(sizes, resConstsInclGaps);
	}

	private static final int[] mergeUnitValues(int[] oldValues, int[] newValues)
	{
		if (oldValues == null)
			return newValues;

		if (newValues == null)
			return oldValues;

		int[] ret = new int[oldValues.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = mergeUnitValues(oldValues[i], newValues[i], true);

		return ret;
	}

	private static final int mergeUnitValues(int oldValue, int newValue, boolean toMax)
	{
		if (oldValue == LayoutUtil.NOT_SET || oldValue == newValue)
			return newValue;

		if (newValue == LayoutUtil.NOT_SET)
			return oldValue;

		return toMax != oldValue > newValue ? newValue : oldValue;
	}

	private static int constrainSize(int s)
	{
		return s > 0 ? (s < LayoutUtil.INF ? s : LayoutUtil.INF) : 0;
	}

	private static class FlowSizeSpec
	{
		private final int[][] sizes;
		private final ResizeConstraint[] resConstsInclGaps;

		private FlowSizeSpec(int[][] sizes, ResizeConstraint[] resConstsInclGaps)
		{
			this.sizes = sizes;
			this.resConstsInclGaps = resConstsInclGaps;
		}

		/**
		 * @param specs The specs for the columns or rows. Last index will be used of <code>fromIx + len</code> is greater than this array's length.
		 * @param targetSize
		 * @param fromIx
		 * @param len
		 * @param sizeType
		 * @param eagerness How eager the algorithm should be to try to expand the sizes.
		 * <ul>
		 * <li>0 - Grow only rows/columns which has the <code>sizeType</code> set to be the containing components AND which has a grow weight &gt; 0.
		 * <li>1 - Grow only rows/columns which has the <code>sizeType</code> set to be the containing components AND which has a grow weight &gt; 0 OR unspecified.
		 * <li>2 - Grow all rows/columns that has a grow weight &gt; 0.
		 * <li>3 - Grow all rows/columns that has a grow weight &gt; 0 OR unspecified.
		 * </ul>
		 * @return The new size.
		 */
		private int expandSizes(DimConstraint[] specs, int targetSize, int fromIx, int len, int sizeType, int eagerness)
		{
			ResizeConstraint[] resConstr = new ResizeConstraint[len];
			int[][] sizesToExpand = new int[len][];
			for (int i = 0; i < len; i++) {
				int size = sizes[i + fromIx][sizeType];
				sizesToExpand[i] = new int[] {size, size, sizes[i + fromIx][LayoutUtil.MAX]};

				if (eagerness <= 1 && i % 2 == 0) { // // (i % 2 == 0) means only odd indexes, which is only rows/col indexes and not gaps.
					int cIx = (i + fromIx - 1) >> 1;
					DimConstraint spec = (DimConstraint) LayoutUtil.getIndexSafe(specs, cIx);

					if (    (sizeType == LayoutUtil.MIN && spec.getSize() != null && spec.getSize().getMin() != null && spec.getSize().getMin().getUnit() != UnitValue.MIN_SIZE) ||
					        (sizeType == LayoutUtil.PREF && spec.getSize() != null && spec.getSize().getPreferred() != null && spec.getSize().getPreferred().getUnit() != UnitValue.PREF_SIZE)) {
						continue;
					}
				}

				resConstr[i] = (ResizeConstraint) LayoutUtil.getIndexSafe(resConstsInclGaps, i + fromIx);
			}

			int[] newSizes = LayoutUtil.calculateSerial(sizesToExpand, resConstr, (eagerness == 1 || eagerness == 3), sizeType, targetSize);
			int newSize = 0;

			for (int i = 0; i < len; i++) {
				int s = newSizes[i];
				sizes[i + fromIx][sizeType] = s;
				newSize += s;
			}
			return newSize;
		}
	}

	@SuppressWarnings("rawtypes")
	private static HashMap[] PARENT_ROWCOL_SIZES_MAP = null;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static synchronized void putSizesAndIndexes(Object parComp, int[] sizes, int[] ixArr, boolean isRows)
	{
		if (PARENT_ROWCOL_SIZES_MAP == null) {
			PARENT_ROWCOL_SIZES_MAP = new HashMap[] {
				new HashMap(4), new HashMap(4)
			};
		}

		HashMap<WeakReference, int[][]> map = PARENT_ROWCOL_SIZES_MAP[isRows ? 0 : 1];

		int[][] sizeMix = new int[][] {ixArr, sizes};

		for (Iterator<Map.Entry<WeakReference, int[][]>> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<WeakReference, int[][]> entry = it.next();
			Object pComp = entry.getKey().get();

			if (pComp == null) {
				it.remove();
			} else if (pComp == parComp) {
				entry.setValue(sizeMix);
				sizeMix = null;
			}
		}

		if (sizeMix != null)
			map.put(new WeakReference(parComp), sizeMix);
	}

	static synchronized int[][] getSizesAndIndexes(Object parComp, boolean isRows)
	{
		if (PARENT_ROWCOL_SIZES_MAP == null)
			return null;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		HashMap<WeakReference, int[][]> map = PARENT_ROWCOL_SIZES_MAP[isRows ? 0 : 1];
		int[][] ret = null;

		for (@SuppressWarnings("rawtypes")
		Iterator<Map.Entry<WeakReference, int[][]>> it = map.entrySet().iterator(); it.hasNext();) {
			@SuppressWarnings("rawtypes")
			Map.Entry<WeakReference, int[][]> entry = it.next();
			Object pComp = entry.getKey().get();

			if (pComp == null) {
				it.remove();
			} else if (pComp == parComp) {
				ret = entry.getValue();
			}
		}
		return ret;
	}
}
