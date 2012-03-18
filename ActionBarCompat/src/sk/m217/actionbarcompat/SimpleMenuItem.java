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
 * Modified to support collapsable action views, intents,
 * OnMenuItemClickListener and visibility by Tibor Bombiak, 2012.
 */

package sk.m217.actionbarcompat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * A <em>really</em> dumb implementation of the {@link android.view.MenuItem} interface, that's only
 * useful for our actionbar-compat purposes. See
 * <code>com.android.internal.view.menu.MenuItemImpl</code> in AOSP for a more complete
 * implementation.
 */
public class SimpleMenuItem implements MenuItemICS {

	private static final String TAG = "SimpleMenuItem";
	private static final int SHOW_AS_ACTION_MASK = SHOW_AS_ACTION_NEVER |
			SHOW_AS_ACTION_IF_ROOM |
			SHOW_AS_ACTION_ALWAYS;

    private SimpleMenu mMenu;

    private final int mId;
    private final int mOrder;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;
    private Intent mIntent;
    private Drawable mIconDrawable;
    private int mIconResId = NO_ICON;
    private boolean mEnabled = true;

    private int mShowAsAction = SHOW_AS_ACTION_NEVER;
    private View mActionView;
    private View mView;
    private OnActionExpandListener mOnActionExpandListener;
    private boolean mIsActionViewExpanded = false;
    private boolean mIsVisible = true;
    private OnMenuItemClickListener mClickListener;

    
    static final int NO_ICON = 0;

    public SimpleMenuItem(SimpleMenu menu, int id, int order, CharSequence title) {
        mMenu = menu;
        mId = id;
        mOrder = order;
        mTitle = title;
    }

    public int getItemId() {
        return mId;
    }

    public int getOrder() {
        return mOrder;
    }

    public MenuItem setTitle(CharSequence title) {
        mTitle = title;
        mMenu.onItemChanged(this, SimpleMenu.ITEM_CHANGED_TITLE);
        return this;
    }

    public MenuItem setTitle(int titleRes) {
        return setTitle(mMenu.getContext().getString(titleRes));
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public MenuItem setTitleCondensed(CharSequence title) {
        mTitleCondensed = title;
        return this;
    }

    public CharSequence getTitleCondensed() {
        return mTitleCondensed != null ? mTitleCondensed : mTitle;
    }

    public MenuItem setIcon(Drawable icon) {
        mIconResId = NO_ICON;
        mIconDrawable = icon;
        mMenu.onItemChanged(this, SimpleMenu.ITEM_CHANGED_ICON);
        return this;
    }

    public MenuItem setIcon(int iconResId) {
        mIconDrawable = null;
        mIconResId = iconResId;
        mMenu.onItemChanged(this, SimpleMenu.ITEM_CHANGED_ICON);
        return this;
    }

    public Drawable getIcon() {
        if (mIconDrawable != null) {
            return mIconDrawable;
        }

        if (mIconResId != NO_ICON) {
            return mMenu.getResources().getDrawable(mIconResId);
        }
        return null;
    }

    public MenuItem setEnabled(boolean enabled) {
        mEnabled = enabled;
        return this;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    // No-op operations. We use no-ops to allow inflation from menu XML.

    public int getGroupId() {
        // Noop
        return 0;
    }

    public View getActionView() {
    	return mActionView;
    }

    public MenuItem setActionProvider(ActionProvider actionProvider) {
        // Noop
        return this;
    }

    public ActionProvider getActionProvider() {
        // Noop
        return null;
    }

    public boolean expandActionView() {
    	if ((mShowAsAction & SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW) == 0 || mActionView == null) {
    		return false;
    	}

        if (mOnActionExpandListener == null ||
                mOnActionExpandListener.onMenuItemActionExpand(this)) {
        	return mMenu.expandItemActionView(this);
        }
        return false;
    }

    public boolean collapseActionView() {
        if ((mShowAsAction & SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW) == 0) {
            return false;
        }
        if (mActionView == null) {
            // We're already collapsed if we have no action view.
            return true;
        }

        if (mOnActionExpandListener == null ||
                mOnActionExpandListener.onMenuItemActionCollapse(this)) {
        	return mMenu.collapseItemActionView(this);
        }
        return false;
    }

    public boolean isActionViewExpanded() {
    	return mIsActionViewExpanded;
    }

    @Override
    public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        // Noop
        return this;
    }

    public MenuItem setOnActionExpandListener(OnActionExpandListener onActionExpandListener) {
    	mOnActionExpandListener = onActionExpandListener;
    	return this;
    }

    public MenuItem setIntent(Intent intent) {
    	mIntent = intent;
        return this;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public MenuItem setShortcut(char c, char c1) {
        // Noop
        return this;
    }

    public MenuItem setNumericShortcut(char c) {
        // Noop
        return this;
    }

    public char getNumericShortcut() {
        // Noop
        return 0;
    }

    public MenuItem setAlphabeticShortcut(char c) {
        // Noop
        return this;
    }

    public char getAlphabeticShortcut() {
        // Noop
        return 0;
    }

    public MenuItem setCheckable(boolean b) {
        // Noop
        return this;
    }

    public boolean isCheckable() {
        // Noop
        return false;
    }

    public MenuItem setChecked(boolean b) {
        // Noop
        return this;
    }

    public boolean isChecked() {
        // Noop
        return false;
    }

    public MenuItem setVisible(boolean visible) {
    	mIsVisible = visible;
    	mMenu.onItemChanged(this, SimpleMenu.ITEM_CHANGED_VISIBILITY);
        return this;
    }

    public boolean isVisible() {
    	return mIsVisible;
    }

    public boolean invoke() {
    	if (mClickListener != null && mClickListener.onMenuItemClick(this)) {
    		return true;
    	}

    	if (mMenu.dispatchMenuItemSelected(mMenu, this)) {
    		return true;
    	}

    	if (mIntent != null) {
    		try {
    			mMenu.getContext().startActivity(mIntent);
    			return true;
    		} catch (ActivityNotFoundException e) {
    			Log.e(TAG, "Can't find activity to handle intent; ignoring", e);
    		}
    	}

    	return false;
    }

    public boolean hasSubMenu() {
        // Noop
        return false;
    }

    public SubMenu getSubMenu() {
        // Noop
        return null;
    }

    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
    	mClickListener = onMenuItemClickListener;
        return this;
    }

    public ContextMenu.ContextMenuInfo getMenuInfo() {
        // Noop
        return null;
    }

    public void setShowAsAction(int actionEnum) {
    	switch (actionEnum & SHOW_AS_ACTION_MASK) {
    		case SHOW_AS_ACTION_ALWAYS:
    		case SHOW_AS_ACTION_IF_ROOM:
    		case SHOW_AS_ACTION_NEVER:
    			// Looks good!
    			break;

    		default:
    			// Mutually exclusive options selected!
    			throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM,"
    					+ " and SHOW_AS_ACTION_NEVER are mutually exclusive.");
    	}
    	mShowAsAction = actionEnum;
    	mMenu.onItemChanged(this, SimpleMenu.ITEM_CHANGED_ACTION);
    }

    public MenuItem setShowAsActionFlags(int actionEnum) {
    	setShowAsAction(actionEnum);
        return null;
    }

    public MenuItem setActionView(View view) {
    	mActionView = view;
    	if (view != null && view.getId() == View.NO_ID && mId > 0) {
    		view.setId(mId);
    	}
    	mMenu.onItemChanged(this, SimpleMenu.ITEM_CHANGED_ACTION_VIEW);
    	return this;
    }

    public MenuItem setActionView(int resId) {
    	final Context context = mMenu.getContext();
    	final LayoutInflater inflater = LayoutInflater.from(context);
       	setActionView(inflater.inflate(resId, null));
    	return this;
    }

    boolean hasCollapsibleActionView() {
    	return (mShowAsAction & SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW) != 0 && mActionView != null;
    }

    void setActionViewExpanded(boolean isExpanded) {
    	mIsActionViewExpanded = isExpanded;
    }

    View getView() {
    	return mView;
    }

    void setView(View view) {
    	mView = view;
    }
}
