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

public interface MenuItemHoneycomb extends MenuItem {

	public static final int SHOW_AS_ACTION_NEVER = 0;
	public static final int SHOW_AS_ACTION_IF_ROOM = 1;
	public static final int SHOW_AS_ACTION_ALWAYS = 2;
	public static final int SHOW_AS_ACTION_WITH_TEXT = 4;

	public void setShowAsAction(int actionEnum);
	public MenuItem setActionView(View view);
	public MenuItem setActionView(int resId);
	public View getActionView();
}
