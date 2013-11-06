// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt
package openblocks.workspace;

/**
 * The PageChangedListener interface must be implemented by anything
 * that wants to be notified when a page's internal state changes
 * (i.e. due to resize, rename, etc.)
 */
public interface PageChangeListener {
	/**
	 * notifies this PageChangeListener that at least one
	 * of the pages have changed states.
	 */
	public void update();
}
