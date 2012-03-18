/*
 * Copyright 2012 Tibor Bombiak <biegleux[at]gmail[dot]com>
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
 */

package sk.m217.actionbarcompat;

import android.content.Context;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;

/**
 * Helper for accessing features in {@link MenuItem}
 * introduced after API level 4 in a backwards compatible fashion.
 */
public class MenuItemCompat {

    /**
     * Never show this item as a button in an Action Bar.
     */
	public static final int SHOW_AS_ACTION_NEVER = 0;

    /**
     * Show this item as a button in an Action Bar if the system
     * decides there is room for it.
     */
	public static final int SHOW_AS_ACTION_IF_ROOM = 1;

    /**
     * Always show this item as a button in an Action Bar. Use sparingly!
     * If too many items are set to always show in the Action Bar it can
     * crowd the Action Bar and degrade the user experience on devices with
     * smaller screens. A good rule of thumb is to have no more than 2
     * items set to always show at a time.
     */
	public static final int SHOW_AS_ACTION_ALWAYS = 2;

    /**
     * When this item is in the action bar, always show it with a
     * text label even if it also has an icon specified.
     */
	public static final int SHOW_AS_ACTION_WITH_TEXT = 4;

    /**
     * This item's action view collapses to a normal menu item.
     * When expanded, the action view temporarily takes over
     * a larger segment of its container.
     */
	public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;

	/**
	 * Interface for the full API.
	 */
	interface MenuItemCompatImpl {
		// API 11
		public void setShowAsAction(MenuItem item, int actionEnum);
		public MenuItem setActionView(MenuItem item, View view);
		public MenuItem setActionView(MenuItem item, int resId);
		public View getActionView(MenuItem item);
		// API 14
		public MenuItem setShowAsActionFlags(MenuItem item, int actionEnum);
		public boolean expandActionView(MenuItem item);
		public boolean collapseActionView(MenuItem item);
		public boolean isActionViewExpanded(MenuItem item);
		public Object newOnActionExpandListener(OnActionExpandListenerCompat listener);
		public MenuItem setOnActionExpandListener(MenuItem item, Object listener);
	}

	/**
	 * Interface implementation for devices with APIs below v11.
	 */
	static class MenuItemCompatBaseImpl implements MenuItemCompatImpl {
		@Override
		public void setShowAsAction(MenuItem item, int actionEnum) {
			MenuItemCompatBase.setShowAsAction(item, actionEnum);
		}
		@Override
		public MenuItem setActionView(MenuItem item, View view) {
			return MenuItemCompatBase.setActionView(item, view);
		}
		@Override
		public MenuItem setActionView(MenuItem item, int resId) {
			return MenuItemCompatBase.setActionView(item, resId);
		}
		@Override
		public View getActionView(MenuItem item) {
			return MenuItemCompatBase.getActionView(item);
		}
		@Override
		public MenuItem setShowAsActionFlags(MenuItem item, int actionEnum) {
			return MenuItemCompatBase.setShowAsActionFlags(item, actionEnum);
		}
		@Override
		public boolean expandActionView(MenuItem item) {
			return MenuItemCompatBase.expandActionView(item);
		}
		@Override
		public boolean collapseActionView(MenuItem item) {
			return MenuItemCompatBase.collapseActionView(item);
		}
		@Override
		public boolean isActionViewExpanded(MenuItem item) {
			return MenuItemCompatBase.isActionViewExpanded(item);
		}
		@Override
		public Object newOnActionExpandListener(final OnActionExpandListenerCompat
				listener) {
			return MenuItemCompatBase.newOnActionExpandListener(
					new MenuItemCompatBase.OnActionExpandListenerCompatBridge() {
						@Override
						public boolean onMenuItemActionExpand(MenuItem item) {
							return listener.onMenuItemActionExpand(item);
						}
						public boolean onMenuItemActionCollapse(MenuItem item) {
							return listener.onMenuItemActionCollapse(item);
						}
					});
		}
		@Override
		public MenuItem setOnActionExpandListener(MenuItem item, Object listener) {
			return MenuItemCompatBase.setOnActionExpandListener(item, listener);
		}
	}

	/**
	 * Interface implementation for devices with at least v11 APIs.
	 */
	static class MenuItemCompatHoneycombImpl implements MenuItemCompatImpl {
		@Override
		public void setShowAsAction(MenuItem item, int actionEnum) {
			MenuItemCompatHoneycomb.setShowAsAction(item, actionEnum);
		}
		@Override
		public MenuItem setActionView(MenuItem item, View view) {
			return MenuItemCompatHoneycomb.setActionView(item, view);
		}
		@Override
		public MenuItem setActionView(MenuItem item, int resId) {
			return MenuItemCompatHoneycomb.setActionView(item, resId);
		}
		@Override
		public View getActionView(MenuItem item) {
			return MenuItemCompatHoneycomb.getActionView(item);
		}
		@Override
		public MenuItem setShowAsActionFlags(MenuItem item, int actionEnum) {
			return MenuItemCompatHoneycomb.setShowAsActionFlags(item, actionEnum);
		}
		@Override
		public boolean expandActionView(MenuItem item) {
			return false; // not supported
		}
		@Override
		public boolean collapseActionView(MenuItem item) {
			return false; // not supported
		}
		@Override
		public boolean isActionViewExpanded(MenuItem item) {
			return false; // not supported
		}
		@Override
		public Object newOnActionExpandListener(final OnActionExpandListenerCompat
				listener) {
			return null; // not supported
		}
		@Override
		public MenuItem setOnActionExpandListener(MenuItem item, Object listener) {
			return item; // not supported
		}
	}

	/**
	 * Interface implementation for devices with at least v14 APIs.
	 */
	static class MenuItemCompatICSImpl extends MenuItemCompatHoneycombImpl {
		@Override
		public MenuItem setShowAsActionFlags(MenuItem item, int actionEnum) {
			return MenuItemCompatICS.setShowAsActionFlags(item, actionEnum);
		}
		@Override
		public boolean expandActionView(MenuItem item) {
			return MenuItemCompatICS.expandActionView(item);
		}
		@Override
		public boolean collapseActionView(MenuItem item) {
			return MenuItemCompatICS.collapseActionView(item);
		}
		@Override
		public boolean isActionViewExpanded(MenuItem item) {
			return MenuItemCompatICS.isActionViewExpanded(item);
		}
		@Override
		public Object newOnActionExpandListener(final OnActionExpandListenerCompat
				listener) {
			return MenuItemCompatICS.newOnActionExpandListener(
					new MenuItemCompatICS.OnActionExpandListenerCompatBridge() {
						@Override
						public boolean onMenuItemActionExpand(MenuItem item) {
							return listener.onMenuItemActionExpand(item);
						}
						public boolean onMenuItemActionCollapse(MenuItem item) {
							return listener.onMenuItemActionCollapse(item);
						}
					});
		}
		@Override
		public MenuItem setOnActionExpandListener(MenuItem item, Object listener) {
			return MenuItemCompatICS.setOnActionExpandListener(item, listener);
		}
	}

	/**
	 * Select the correct implementation to use for the current platform.
	 */
	static final MenuItemCompatImpl IMPL;
	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			IMPL = new MenuItemCompatICSImpl();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			IMPL = new MenuItemCompatHoneycombImpl();
		} else {
			IMPL = new MenuItemCompatBaseImpl();
		}
	}

	/*
	 * Hide constructor
	 */
	private MenuItemCompat(Context context) {

	}

	/**
	 * See {@link MenuItem#setOnActionExpandListener(OnActionExpandListenerCompat)}.
	 * If running on {@link android.os.Build.VERSION_CODES#HONEYCOMB} devices,
	 * does nothing and returns item.
	 */
	public static MenuItem setOnActionExpandListener(MenuItem item,
			OnActionExpandListenerCompat listener) {
		return IMPL.setOnActionExpandListener(item, listener.mListener);
	}

	/**
	 * See {@link MenuItem#setShowAsAction(int)}.
	 */
	public static void setShowAsAction(MenuItem item, int actionEnum) {
		IMPL.setShowAsAction(item, actionEnum);
	}

	/**
	 * See {@link MenuItem#setShowAsAction(View)}.
	 */
	public static MenuItem setActionView(MenuItem item, View view) {
		return IMPL.setActionView(item, view);
	}

	/**
	 * See {@link MenuItem#setShowAsAction(int)}.
	 */
	public static MenuItem setActionView(MenuItem item, int resId) {
		return IMPL.setActionView(item, resId);
	}

	/**
	 * See {@link MenuItem#getActionView()}.
	 */
	public static View getActionView(MenuItem item) {
		return IMPL.getActionView(item);
	}

	/**
	 * See {@link MenuItem#setShowAsActionFlags(int)}.
	 */
	public static MenuItem setShowAsActionFlags(MenuItem item, int actionEnum) {
		return IMPL.setShowAsActionFlags(item, actionEnum);
	}

	/**
	 * See {@link MenuItem#expandActionView()}.
	 * If running on {@link android.os.Build.VERSION_CODES#HONEYCOMB} devices,
	 * does nothing and returns false.
	 */
	public static boolean expandActionView(MenuItem item) {
		return IMPL.expandActionView(item);
	}

	/**
	 * See {@link MenuItem#collapseActionView()}.
	 * If running on {@link android.os.Build.VERSION_CODES#HONEYCOMB} devices,
	 * does nothing and returns false.
	 */
	public static boolean collapseActionView(MenuItem item) {
		return IMPL.collapseActionView(item);
	}

	/**
	 * See {@link MenuItem#isActionViewExpanded()}.
	 * If running on {@link android.os.Build.VERSION_CODES#HONEYCOMB} devices,
	 * does nothing and returns false.
	 */
	public static boolean isActionViewExpanded(MenuItem item) {
		return IMPL.isActionViewExpanded(item);
	}

	/**
	 * Helper for accessing features in {@link MenuItem.OnActionExpandListener}
	 * introduced after API level 4 in a backwards compatible fashion.
	 */
	public static abstract class OnActionExpandListenerCompat {
		final Object mListener;

		public OnActionExpandListenerCompat() {
			mListener = IMPL.newOnActionExpandListener(this);
		}
		public boolean onMenuItemActionExpand(MenuItem item) {
			return false;
		}
		public boolean onMenuItemActionCollapse(MenuItem item) {
			return false;
		}
	}
}
