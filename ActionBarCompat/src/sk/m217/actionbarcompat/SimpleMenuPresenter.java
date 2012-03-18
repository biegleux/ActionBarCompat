/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Modified to use with SimpleMenu by Tibor Bombiak, 2012
 */

package sk.m217.actionbarcompat;

import android.content.Context;

/**
 * A SimpleMenuPresenter is responsible for building views for a SimpleMenu object.
 */
public interface SimpleMenuPresenter {

	/**
	 * Initialize this presenter for the given context and menu.
	 * This method is called by SimpleMenu when a presenter is
	 * added. See {@link SimpleMenu#addMenuPresenter(MenuPresenter)}
	 *
	 * @param context Context for this presenter; used for view creation and resource management
	 * @param menu Menu to host
	 */
	public void initForMenu(Context context, SimpleMenu menu);

    /**
     * Update the menu UI in response to a change. Called by
     * SimpleMenu when a menu item has been changed.
     *
     * @param menu Menu containing the updated item
     * @param item Updated item
     * @param int Indicates what has been changed
     */
    public void updateMenuView(SimpleMenu menu, SimpleMenuItem item, int change);

	/**
	 * Called when a menu item with a collapsable action view should expand its action view.
	 *
	 * @param menu Menu containing the item to be expanded
	 * @param item Item to be expanded
	 * @return true if this presenter expanded the action view, false otherwise.
	 */
    public boolean expandItemActionView(SimpleMenu menu, SimpleMenuItem item);

    /**
     * Called when a menu item with a collapsable action view should collapse its action view.
     *
     * @param menu Menu containing the item to be collapsed
     * @param item Item to be collapsed
     * @return true if this presenter collapsed the action view, false otherwise.
     */
    public boolean collapseItemActionView(SimpleMenu menu, SimpleMenuItem item);
}
