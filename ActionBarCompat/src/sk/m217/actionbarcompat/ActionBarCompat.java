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

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Helper for accessing features in {@link android.app.ActionBar}
 * introduced after API level 4 in a backwards compatible fashion.
 */
public class ActionBarCompat {

	/**
	 * Interface for the full API.
	 */
	interface ActionBarCompatImpl {
		public void setIcon(Activity activity, int resId);
		public void setIcon(Activity activity, Drawable icon);
		public void setTitle(Activity activity, CharSequence title);
		public void setTitle(Activity activity, int resId);
		public void setDisplayShowHomeEnabled(Activity activity, boolean showHome);
		public void setDisplayHomeAsUpEnabled(Activity activity, boolean showHomeAsUp);
		public void setDisplayShowTitleEnabled(Activity activity, boolean showTitle);
		public void setBackgroundDrawable(Activity activity, Drawable d);
		public CharSequence getTitle(Activity activity);
		public void setHomeButtonEnabled(Activity activity, boolean enabled);
	}

	/**
	 * Interface implementation for devices with APIs below v11.
	 */
	static class ActionBarCompatBaseImpl implements ActionBarCompatImpl {
		@Override
		public void setIcon(Activity activity, int resId) {
			ActionBarCompatBase.setIcon(activity, resId);
		}
		@Override
		public void setIcon(Activity activity, Drawable icon) {
			ActionBarCompatBase.setIcon(activity, icon);
		}
		@Override
		public void setTitle(Activity activity, CharSequence title) {
			ActionBarCompatBase.setTitle(activity, title);
		}
		@Override
		public void setTitle(Activity activity, int resId) {
			ActionBarCompatBase.setTitle(activity, resId);
		}
		@Override
		public void setDisplayShowHomeEnabled(Activity activity, boolean showHome) {
			ActionBarCompatBase.setDisplayShowHomeEnabled(activity, showHome);
		}
		@Override
		public void setDisplayHomeAsUpEnabled(Activity activity, boolean showHomeAsUp) {
			ActionBarCompatBase.setDisplayHomeAsUpEnabled(activity, showHomeAsUp);
		}
		@Override
		public void setDisplayShowTitleEnabled(Activity activity, boolean showTitle) {
			ActionBarCompatBase.setDisplayShowTitleEnabled(activity, showTitle);
		}
		@Override
		public void setBackgroundDrawable(Activity activity, Drawable d) {
			ActionBarCompatBase.setBackgroundDrawable(activity, d);
		}
		@Override
		public CharSequence getTitle(Activity activity) {
			return ActionBarCompatBase.getTitle(activity);
		}
		@Override
		public void setHomeButtonEnabled(Activity activity, boolean enabled) {
			ActionBarCompatBase.setHomeButtonEnabled(activity, enabled);
		}
	}

	/**
	 * Interface implementation for devices with at least v11 APIs.
	 */
	static class ActionBarCompatHoneycombImpl extends ActionBarCompatICSImpl {
		@Override
		public void setIcon(Activity activity, int resId) {
			ActionBarCompatHoneycomb.setIcon(activity, resId);
		}
		@Override
		public void setIcon(Activity activity, Drawable icon) {
			ActionBarCompatHoneycomb.setIcon(activity, icon);
		}
		@Override
		public void setHomeButtonEnabled(Activity activity, boolean enabled) {
			ActionBarCompatHoneycomb.setHomeButtonEnabled(activity, enabled);
		}
	}

	/**
	 * Interface implementation for devices with at lease v14 APIs.
	 */
	static class ActionBarCompatICSImpl implements ActionBarCompatImpl {
		@Override
		public void setIcon(Activity activity, int resId) {
			ActionBarCompatICS.setIcon(activity, resId);
		}
		@Override
		public void setIcon(Activity activity, Drawable icon) {
			ActionBarCompatICS.setIcon(activity, icon);
		}
		@Override
		public void setTitle(Activity activity, CharSequence title) {
			ActionBarCompatICS.setTitle(activity, title);
		}
		@Override
		public void setTitle(Activity activity, int resId) {
			ActionBarCompatICS.setTitle(activity, resId);
		}
		@Override
		public void setDisplayShowHomeEnabled(Activity activity, boolean showHome) {
			ActionBarCompatICS.setDisplayShowHomeEnabled(activity, showHome);
		}
		@Override
		public void setDisplayHomeAsUpEnabled(Activity activity, boolean showHomeAsUp) {
			ActionBarCompatICS.setDisplayHomeAsUpEnabled(activity, showHomeAsUp);
		}
		@Override
		public void setDisplayShowTitleEnabled(Activity activity, boolean showTitle) {
			ActionBarCompatICS.setDisplayShowTitleEnabled(activity, showTitle);
		}
		@Override
		public void setBackgroundDrawable(Activity activity, Drawable d) {
			ActionBarCompatICS.setBackgroundDrawable(activity, d);
		}
		@Override
		public CharSequence getTitle(Activity activity) {
			return ActionBarCompatICS.getTitle(activity);
		}
		@Override
		public void setHomeButtonEnabled(Activity activity, boolean enabled) {
			ActionBarCompatICS.setHomeButtonEnabled(activity, enabled);
		}
	}

	/**
	 * Select the correct implementation to use for the current platform.
	 */
	static final ActionBarCompatImpl IMPL;
	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			IMPL = new ActionBarCompatICSImpl();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			IMPL = new ActionBarCompatHoneycombImpl();
		} else {
			IMPL = new ActionBarCompatBaseImpl();
		}
	}

	/*
	 * Hide constructor
	 */
	private ActionBarCompat(Context context) {

	}

	/**
	 * See {@link android.app.ActionBar#setIcon(int)}.
	 * Logo is not supported for pre-{@link android.os.Build.VERSION_CODES#HONEYCOMB}
	 * devices.
	 */
	public static void setIcon(Activity activity, int resId) {
		IMPL.setIcon(activity, resId);
	}

	/**
	 * See {@link android.app.ActionBar#setIcon(Drawable)}.
	 * Logo is not supported for pre-{@link android.os.Build.VERSION_CODES#HONEYCOMB}
	 * devices.
	 */
	public static void setIcon(Activity activity, Drawable icon) {
		IMPL.setIcon(activity, icon);
	}

	/**
	 * See {@link android.app.ActionBar#setTitle(CharSequence)}.
	 */
	public static void setTitle(Activity activity, CharSequence title) {
		IMPL.setTitle(activity, title);
	}

	/**
	 * See {@link android.app.ActionBar#setTitle(int)}.
	 */
	public static void setTitle(Activity activity, int resId) {
		IMPL.setTitle(activity, resId);
	}

	/**
	 * See {@link android.app.ActionBar#setDisplayShowHomeEnabled(boolean)}.
	 */
	public static void setDisplayShowHomeEnabled(Activity activity, boolean showHome) {
		IMPL.setDisplayShowHomeEnabled(activity, showHome);
	}

	/**
	 * See {@link android.app.ActionBar#setDisplayHomeAsUpEnabled(boolean)}.
	 */
	public static void setDisplayHomeAsUpEnabled(Activity activity, boolean showHomeAsUp) {
		IMPL.setDisplayHomeAsUpEnabled(activity, showHomeAsUp);
	}

	/**
	 * See {@link android.app.ActionBar#setDisplayShowTitleEnabled(boolean)}.
	 */
	public static void setDisplayShowTitleEnabled(Activity activity, boolean showTitle) {
		IMPL.setDisplayShowTitleEnabled(activity, showTitle);
	}

	/**
	 * See {@link android.app.ActionBar#setBackgroundDrawable(Drawable)}.
	 */
	public static void setBackgroundDrawable(Activity activity, Drawable d) {
		IMPL.setBackgroundDrawable(activity, d);
	}

	/**
	 * See {@link android.app.ActionBar#getTitle()}.
	 */
	public CharSequence getTitle(Activity activity) {
		return IMPL.getTitle(activity);
	}

	/**
	 * See {@link android.app.ActionBar#setHomeButtonEnabled(boolean)}.
	 */
	public static void setHomeButtonEnabled(Activity activity, boolean enabled) {
		IMPL.setHomeButtonEnabled(activity, enabled);
	}
}
