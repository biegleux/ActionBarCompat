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

import android.view.MenuItem;
import android.view.View;

/**
 * Implementation of menu compatibility for pre-Honeycomb platforms.
 */
public class MenuItemCompatBase {

	interface OnActionExpandListenerCompatBridge {
		public boolean onMenuItemActionExpand(MenuItem item);
		public boolean onMenuItemActionCollapse(MenuItem item);
	}

	public static Object newOnActionExpandListener(final
			OnActionExpandListenerCompatBridge listener) {
		return new SimpleMenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return listener.onMenuItemActionExpand(item);
			}
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				return listener.onMenuItemActionCollapse(item);
			}
		};
	}

	public static MenuItem setOnActionExpandListener(MenuItem item, Object listener) {
		return ((SimpleMenuItem) item).setOnActionExpandListener(
				(SimpleMenuItem.OnActionExpandListener) listener);
	}

	public static void setShowAsAction(MenuItem item, int actionEnum) {
		((SimpleMenuItem) item).setShowAsAction(actionEnum);
	}

	public static MenuItem setActionView(MenuItem item, View view) {
		return ((SimpleMenuItem) item).setActionView(view);
	}

	public static MenuItem setActionView(MenuItem item, int resId) {
		return ((SimpleMenuItem) item).setActionView(resId);
	}

	public static View getActionView(MenuItem item) {
		return ((SimpleMenuItem) item).getActionView();
	}

	public static MenuItem setShowAsActionFlags(MenuItem item, int actionEnum) {
		return ((SimpleMenuItem) item).setShowAsActionFlags(actionEnum);
	}

	public static boolean expandActionView(MenuItem item) {
		return ((SimpleMenuItem) item).expandActionView();
	}

	public static boolean collapseActionView(MenuItem item) {
		return ((SimpleMenuItem) item).collapseActionView();
	}

	public static boolean isActionViewExpanded(MenuItem item) {
		return ((SimpleMenuItem) item).isActionViewExpanded();
	}
}
