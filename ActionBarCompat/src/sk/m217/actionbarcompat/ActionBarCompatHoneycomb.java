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
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Implementation of action bar compatibility for Honeycomb platform.
 */
public class ActionBarCompatHoneycomb {

	public static void setIcon(Activity activity, int resId) {
		setIcon(activity, activity.getResources().getDrawable(resId));
	}

	public static void setIcon(Activity activity, Drawable icon) {
		View v = activity.findViewById(android.R.id.home);
		if (v != null && v instanceof ImageView) {
			((ImageView) v).setImageDrawable(icon);
		}
	}

	public static void setHomeButtonEnabled(Activity activity, boolean enabled) {
		View v = activity.findViewById(android.R.id.home);
		if (v != null) {
			ViewParent vp = v.getParent();
			if (vp != null && vp instanceof FrameLayout) {
				((FrameLayout) vp).setEnabled(enabled);
				((FrameLayout) vp).setClickable(enabled);
			}
		}
	}
}
