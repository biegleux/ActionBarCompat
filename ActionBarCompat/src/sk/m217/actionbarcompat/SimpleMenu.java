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
 * Modified to support collapsable action views and menu presenters
 * by Tibor Bombiak, 2012.
 */

package sk.m217.actionbarcompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

/**
 * A <em>really</em> dumb implementation of the {@link android.view.Menu} interface, that's only
 * useful for our actionbar-compat purposes. See
 * <code>com.android.internal.view.menu.MenuBuilder</code> in AOSP for a more complete
 * implementation.
 */
public class SimpleMenu implements Menu {

    private Context mContext;
    private Resources mResources;
    private ArrayList<SimpleMenuItem> mItems;
    private CopyOnWriteArrayList<WeakReference<SimpleMenuPresenter>> mPresenters =
            new CopyOnWriteArrayList<WeakReference<SimpleMenuPresenter>>();
    private SimpleMenuItem mExpandedItem;
    private Callback mCallback;

    static final int ITEM_CHANGED_NEW = 0;
    static final int ITEM_CHANGED_TITLE = 1;
    static final int ITEM_CHANGED_ICON = 2;
    static final int ITEM_CHANGED_ACTION = 3;
    static final int ITEM_CHANGED_ACTION_VIEW = 4;
    static final int ITEM_CHANGED_VISIBILITY = 5;

    public interface Callback {
    	public boolean onMenuItemSelected(SimpleMenu menu, MenuItem item);
    }

    public SimpleMenu(Context context) {
        mContext = context;
        mResources = context.getResources();
        mItems = new ArrayList<SimpleMenuItem>();
    }

    public Context getContext() {
        return mContext;
    }

    public Resources getResources() {
        return mResources;
    }

    /**
     * Add a presenter to this menu. This will only hold a WeakReference;
     * you do not need to explicitly remove a presenter, but you can using
     * {@link #removeMenuPresenter(MenuPresenter)}.
     * @param presenter The presenter to add
     */
    public void addMenuPresenter(SimpleMenuPresenter presenter) {
    	mPresenters.add(new WeakReference<SimpleMenuPresenter>(presenter));
    	presenter.initForMenu(mContext, this);
    }

    /**
     * Remove a presenter from this menu. That presenter will no longer
     * receive notifications of updates to this menu's data.
     * @param presenter The presenter to remove
     */
    public void removeMenuPresenter(SimpleMenuPresenter presenter) {
    	for (WeakReference<SimpleMenuPresenter> ref : mPresenters) {
    		final SimpleMenuPresenter item = ref.get();
    		if (item == null || item == presenter) {
    			mPresenters.remove(ref);
    		}
    	}
    }

    private void dispatchPresenterUpdate(SimpleMenuItem item, int change) {
    	if (mPresenters.isEmpty()) {
    		return;
    	}
    	for (WeakReference<SimpleMenuPresenter> ref : mPresenters) {
    		final SimpleMenuPresenter presenter = ref.get();
    		if (presenter == null) {
    			mPresenters.remove(ref);
    		} else {
    			presenter.updateMenuView(this, item, change);
    		}
    	}
    }

    public void setCallback(Callback cb) {
    	mCallback = cb;
    }

    boolean dispatchMenuItemSelected(SimpleMenu menu, MenuItem item) {
    	return mCallback != null && mCallback.onMenuItemSelected(menu, item);
    }

    public MenuItem add(CharSequence title) {
        return addInternal(0, 0, title);
    }

    public MenuItem add(int titleRes) {
        return addInternal(0, 0, mResources.getString(titleRes));
    }

    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        return addInternal(itemId, order, title);
    }

    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return addInternal(itemId, order, mResources.getString(titleRes));
    }

    /**
     * Adds an item to the menu.  The other add methods funnel to this.
     */
    private MenuItem addInternal(int itemId, int order, CharSequence title) {
        final SimpleMenuItem item = new SimpleMenuItem(this, itemId, order, title);
        mItems.add(findInsertIndex(mItems, order), item);
        onItemChanged(item, ITEM_CHANGED_NEW);
        return item;
    }

    private static int findInsertIndex(ArrayList<? extends MenuItem> items, int order) {
        for (int i = items.size() - 1; i >= 0; i--) {
            MenuItem item = items.get(i);
            if (item.getOrder() <= order) {
                return i + 1;
            }
        }

        return 0;
    }

    public int findItemIndex(int id) {
        final int size = size();

        for (int i = 0; i < size; i++) {
            SimpleMenuItem item = mItems.get(i);
            if (item.getItemId() == id) {
                return i;
            }
        }

        return -1;
    }

    public void removeItem(int itemId) {
        removeItemAtInt(findItemIndex(itemId));
    }

    private void removeItemAtInt(int index) {
        if ((index < 0) || (index >= mItems.size())) {
            return;
        }
        mItems.remove(index);
    }

    public void clear() {
        mItems.clear();
    }

    public MenuItem findItem(int id) {
        final int size = size();
        for (int i = 0; i < size; i++) {
            SimpleMenuItem item = mItems.get(i);
            if (item.getItemId() == id) {
                return item;
            }
        }

        return null;
    }

    public int size() {
        return mItems.size();
    }

    public MenuItem getItem(int index) {
        return mItems.get(index);
    }

    /**
     * Called when a menu item with a collapsable action view should expand its
     * action view.
     * @param item Item to be expanded.
     * @return True if this presenter expanded the action view, false otherwise.
     */
    public boolean expandItemActionView(SimpleMenuItem item) {
    	if (mPresenters.isEmpty()) {
    		return false;
    	}

    	boolean expanded = false;
    	for (WeakReference<SimpleMenuPresenter> ref : mPresenters) {
    		final SimpleMenuPresenter presenter = ref.get();
    		if (presenter == null) {
    			mPresenters.remove(ref);
    		} else {
    			if ((expanded = presenter.expandItemActionView(this, item))) {
    				break;
    			}
    		}
    	}
    	if (expanded) {
    		mExpandedItem = item;
    	}
    	return expanded;
    }

    /**
     * Called when a menu item with a collapsable action view should collapse its
     * action view.
     * @param item Item to be collapsed.
     * @return True if this presenter collapsed the action view, false otherwise. 
     */
    public boolean collapseItemActionView(SimpleMenuItem item) {
    	if (mPresenters.isEmpty() || mExpandedItem != item) {
    		return false;
    	}

    	boolean collapsed = false;
    	for (WeakReference<SimpleMenuPresenter> ref : mPresenters) {
    		final SimpleMenuPresenter presenter = ref.get();
    		if (presenter == null) {
    			mPresenters.remove(ref);
    		} else {
    			if ((collapsed = presenter.collapseItemActionView(this, item))) {
    				break;
    			}
    		}
    	}
    	if (collapsed) {
    		mExpandedItem = null;
    	}
    	return collapsed;
    }

    public SimpleMenuItem getExpandedItem() {
    	return mExpandedItem;
    }

    void onItemChanged(SimpleMenuItem item, int change) {
    	dispatchPresenterUpdate(item, change);
    }

    // Unsupported operations.

    public SubMenu addSubMenu(CharSequence charSequence) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public SubMenu addSubMenu(int titleRes) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public int addIntentOptions(int i, int i1, int i2, ComponentName componentName,
            Intent[] intents, Intent intent, int i3, MenuItem[] menuItems) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void removeGroup(int i) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void setGroupCheckable(int i, boolean b, boolean b1) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void setGroupVisible(int i, boolean b) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void setGroupEnabled(int i, boolean b) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public boolean hasVisibleItems() {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void close() {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public boolean performShortcut(int i, KeyEvent keyEvent, int i1) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public boolean isShortcutKey(int i, KeyEvent keyEvent) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public boolean performIdentifierAction(int i, int i1) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void setQwertyMode(boolean b) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }
}
