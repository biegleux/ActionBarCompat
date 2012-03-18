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
import android.graphics.drawable.Drawable;

/**
 * Implementation of action bar compatibility that can call ICS APIs.
 */
public class ActionBarCompatICS {

	public static void setIcon(Activity activity, int resId) {
		activity.getActionBar().setIcon(resId);
	}

	public static void setIcon(Activity activity, Drawable icon) {
		activity.getActionBar().setIcon(icon);
	}

	public static void setTitle(Activity activity, CharSequence title) {
		activity.getActionBar().setTitle(title);
	}

	public static void setTitle(Activity activity, int resId) {
		activity.getActionBar().setTitle(resId);
	}

	public static void setDisplayShowHomeEnabled(Activity activity, boolean showHome) {
		activity.getActionBar().setDisplayShowHomeEnabled(showHome);
	}

	public static void setDisplayHomeAsUpEnabled(Activity activity, boolean showHomeAsUp) {
		activity.getActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
	}

	public static void setDisplayShowTitleEnabled(Activity activity, boolean showTitle) {
		activity.getActionBar().setDisplayShowTitleEnabled(showTitle);
	}

	public static void setBackgroundDrawable(Activity activity, Drawable d) {
		activity.getActionBar().setBackgroundDrawable(d);
	}
	
	public static CharSequence getTitle(Activity activity) {
		return activity.getActionBar().getTitle();
	}

	public static void setHomeButtonEnabled(Activity activity, boolean enabled) {
		activity.getActionBar().setHomeButtonEnabled(enabled);
	}
}
