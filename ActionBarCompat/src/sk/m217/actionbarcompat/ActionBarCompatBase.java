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

public class ActionBarCompatBase {

	private static ActionBarHelperBase getActionBarHelperBase(Activity activity) {
		ActionBarActivity actionBarActivity = (ActionBarActivity) activity;
		return (ActionBarHelperBase) actionBarActivity.getActionBarHelper();		
	}

	public static void setIcon(Activity activity, int resId) {
		getActionBarHelperBase(activity).setIcon(resId);
	}

	public static void setIcon(Activity activity, Drawable icon) {
		getActionBarHelperBase(activity).setIcon(icon);
	}

	public static void setTitle(Activity activity, CharSequence title) {
		getActionBarHelperBase(activity).setTitle(title);
	}

	public static void setTitle(Activity activity, int resId) {
		getActionBarHelperBase(activity).setTitle(resId);
	}

	public static void setDisplayShowHomeEnabled(Activity activity, boolean showHome) {
		getActionBarHelperBase(activity).setDisplayShowHomeEnabled(showHome);
	}

	public static void setDisplayHomeAsUpEnabled(Activity activity, boolean showHomeAsUp) {
		getActionBarHelperBase(activity).setDisplayHomeAsUpEnabled(showHomeAsUp);
	}

	public static void setDisplayShowTitleEnabled(Activity activity, boolean showTitle) {
		getActionBarHelperBase(activity).setDisplayShowTitleEnabled(showTitle);
	}

	public static void setBackgroundDrawable(Activity activity, Drawable d) {
		getActionBarHelperBase(activity).setBackgroundDrawable(d);
	}

	public static CharSequence getTitle(Activity activity) {
		return getActionBarHelperBase(activity).getTitle();
	}

	public static void setHomeButtonEnabled(Activity activity, boolean enabled) {
		getActionBarHelperBase(activity).setHomeButtonEnabled(enabled);
	}
}
