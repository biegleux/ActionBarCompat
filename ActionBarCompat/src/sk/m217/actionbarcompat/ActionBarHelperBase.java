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
 * Modified to support ActionViews by Benjamin Ferrari, Nov. 15 2011
 * Modified to support extended functionality of action bar by Tibor Bombiak, 2012
 */

package sk.m217.actionbarcompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A class that implements the action bar pattern for pre-Honeycomb devices.
 */
public class ActionBarHelperBase extends ActionBarHelper {
    private static final String MENU_RES_NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String MENU_ATTR_ID = "id";
    private static final String MENU_ATTR_SHOW_AS_ACTION = "showAsAction";
    private static final String MENU_ATTR_ACTION_LAYOUT = "actionLayout";

    protected Set<Integer> mActionItemIds = new HashSet<Integer>();
    protected Map<Integer, Integer> mActionItemIdsToActionLayoutLookup = new HashMap<Integer, Integer>();
    protected Map<Integer, Integer> mActionItemIdsToShowAsActionLookup = new HashMap<Integer, Integer>();
    private SimpleMenu mMenu;

    private HomeView mHomeLayout;
    private HomeView mExpandedHomeLayout;
    private TextView mTitleText;

    private boolean mInitialized = false;
    private boolean mUserTitle = false;
    private int mDisplayOptions = -1;
    private SimpleMenuItem mHomeItem;
    private Drawable mIcon;
    private CharSequence mTitle;

    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    private ActionMenuPresenter mActionMenuPresenter;
    private View mExpandedActionView;

    private static final int DISPLAY_DEFAULT = 0;
    private static final int DISPLAY_SHOW_HOME = 0x2;
    private static final int DISPLAY_HOME_AS_UP = 0x4;
    private static final int DISPLAY_SHOW_TITLE = 0x8;

    private static final String TAG = ActionBarHelperBase.class.getName();

    protected ActionBarHelperBase(Activity activity) {
        super(activity);
    }

    /**{@inheritDoc}*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    }

    /**{@inheritDoc}*/
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
    	initActionBar();
    }

    public void initActionBar() {
    	if (mInitialized) {
    		return;
    	}
        mActivity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        		R.layout.actionbar_compat);
        setupActionBar();
        mMenu = new SimpleMenu(mActivity);
        mMenu.setCallback(new SimpleMenu.Callback() {
			@Override
			public boolean onMenuItemSelected(SimpleMenu menu, MenuItem item) {
				return mActivity.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
			}
		});
        mActivity.onCreatePanelMenu(Window.FEATURE_OPTIONS_PANEL, mMenu);
        mActivity.onPrepareOptionsMenu(mMenu);
        for (int i = 0; i < mMenu.size(); i++) {
            MenuItem item = mMenu.getItem(i);
            if (mActionItemIds.contains(item.getItemId())) {
                addActionItemCompatFromMenuItem(item);
            }
        }
        // TODO mozno dat hned za mMenu = new SimpleMenu(mActivity);
        // ked sa implementuje setactionview, setshowasaction atd.
        mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
        mActionMenuPresenter = new ActionMenuPresenter();
        mMenu.addMenuPresenter(mExpandedMenuPresenter);
        mMenu.addMenuPresenter(mActionMenuPresenter);
        mInitialized = true;
    }

    private final OnClickListener mExpandedActionViewUpListener = new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		final SimpleMenuItem item = mExpandedMenuPresenter.mCurrentExpandedItem;
    		if (item != null) {
    			item.collapseActionView();
    		}
    	}
    };

    private final OnClickListener mUpClickListener = new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		mActivity.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, mHomeItem);
    	}
    };

    /**
     * Sets up the compatibility action bar with the given title.
     */
    private void setupActionBar() {
        final ViewGroup actionBarCompat = getActionBarCompat();
        if (actionBarCompat == null) {
            return;
        }

        // Add home layout
        mHomeItem = new SimpleMenuItem(new SimpleMenu(mActivity),
        		android.R.id.home, 0, mActivity.getTitle());

        final LayoutInflater inflater = LayoutInflater.from(mActivity);

        mHomeLayout = (HomeView) inflater.inflate(
        		R.layout.actionbar_compat_home, actionBarCompat, false);
        mHomeLayout.setOnClickListener(mUpClickListener);
        mHomeLayout.setClickable(true);
        mHomeLayout.setFocusable(true); 

        mExpandedHomeLayout = (HomeView) inflater.inflate(
        		R.layout.actionbar_compat_home, actionBarCompat, false);
        mExpandedHomeLayout.setUp(true);
        mExpandedHomeLayout.setOnClickListener(mExpandedActionViewUpListener);
        mExpandedHomeLayout.setContentDescription(mActivity.getResources().getText(
                R.string.action_bar_up_description));
        mExpandedHomeLayout.setFocusable(true);

        ApplicationInfo appInfo = mActivity.getApplicationInfo();
        PackageManager pm = mActivity.getPackageManager();
        //TODO mLogo = a.getDrawable(R.styleable.ActionBar_logo);
        // styleable nech nacita napr icon najprv zo stylu (pozri ActionBarView)
        try {
        	mIcon = pm.getActivityIcon(mActivity.getComponentName());
        } catch (NameNotFoundException e) {
        	Log.e(TAG, "Activity component name not found!", e);
        }
        if (mIcon == null) {
        	mIcon = appInfo.loadIcon(pm);
        }

        mHomeLayout.setIcon(mIcon);

        actionBarCompat.addView(mHomeLayout);

        // Add title text
        LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(
        		0, ViewGroup.LayoutParams.FILL_PARENT);
        springLayoutParams.weight = 1;

        mTitle = mActivity.getTitle();
        mTitleText = new TextView(mActivity, null, R.attr.actionbarCompatTitleStyle);
        mTitleText.setLayoutParams(springLayoutParams);
        mTitleText.setText(mTitle);
        actionBarCompat.addView(mTitleText);

        //setDisplayOptions(DISPLAY_DEFAULT); // TODO styles.xml
        setDisplayOptions(DISPLAY_SHOW_HOME | DISPLAY_SHOW_TITLE);
    }

    /**{@inheritDoc}*/
    @Override
    public void setRefreshActionItemState(boolean refreshing) {
        View refreshButton = mActivity.findViewById(R.id.actionbar_compat_item_refresh);
        View refreshIndicator = mActivity.findViewById(
        		R.id.actionbar_compat_item_refresh_progress);

        if (refreshButton != null) {
            refreshButton.setVisibility(refreshing ? View.GONE : View.VISIBLE);
        }
        if (refreshIndicator != null) {
            refreshIndicator.setVisibility(refreshing ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Action bar helper code to be run in {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
     * 
     * NOTE: This code will mark on-screen menu items as invisible.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hides on-screen action items from the options menu.
        for (Integer id : mActionItemIds) {
        	if (!(menu instanceof SimpleMenu)) {
        		menu.removeItem(id);
        	}
        	//menu.findItem(id).setVisible(false);
        }
        return true;
    }

    /**{@inheritDoc}*/
    @Override
    public void onTitleChanged(CharSequence title, int color) {
        TextView titleView = (TextView) mActivity.findViewById(R.id.actionbar_compat_title);
        if (titleView != null && !mUserTitle) {
        	mTitle = title;
        	if ((mDisplayOptions & DISPLAY_SHOW_TITLE) != 0) {
        		titleView.setText(mTitle);
        	}
        }
    }

    /**
     * Returns a {@link android.view.MenuInflater} that can read action bar metadata on
     * pre-Honeycomb devices.
     */
    public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
        return new WrappedMenuInflater(mActivity, superMenuInflater);
    }

    /**
     * Returns the {@link android.view.ViewGroup} for the action bar on phones (compatibility action
     * bar). Can return null, and will return null on Honeycomb.
     */
    private ViewGroup getActionBarCompat() {
        return (ViewGroup) mActivity.findViewById(R.id.actionbar_compat);
    }

    /**
     * Adds an action button to the compatibility action bar, using menu information from a {@link
     * android.view.MenuItem}. If the menu item ID is <code>menu_refresh</code>, the menu item's
     * state can be changed to show a loading spinner using
     * {@link sk.m217.actionbarcompat.ActionBarHelperBase#setRefreshActionItemState(boolean)}.
     */
    private View addActionItemCompatFromMenuItem(final MenuItem item) {
        final int itemId = item.getItemId();
        final SimpleMenuItem simpleItem = (SimpleMenuItem) item;

        final ViewGroup actionBar = getActionBarCompat();
        if (actionBar == null) {
            return null;
        }

        View view;
        if (simpleItem.getActionView() != null && !simpleItem.hasCollapsibleActionView()) {
        	view = simpleItem.getActionView();
        } else {
            // Create the button
            ImageButton actionButton = new ImageButton(mActivity, null,
            		R.attr.actionbarCompatItemStyle);
            actionButton.setLayoutParams(new ViewGroup.LayoutParams(
            		(int) mActivity.getResources().getDimension(
            				R.dimen.actionbar_compat_button_width),
                    ViewGroup.LayoutParams.FILL_PARENT));
            if (itemId == R.id.menu_refresh) {
                actionButton.setId(R.id.actionbar_compat_item_refresh);
            }
            actionButton.setImageDrawable(item.getIcon());
            actionButton.setScaleType(ImageView.ScaleType.CENTER);
            actionButton.setContentDescription(item.getTitle());
            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                	if (simpleItem.isEnabled()) {
                		if (simpleItem.hasCollapsibleActionView()) {
                			simpleItem.expandActionView();
                		}
                		simpleItem.invoke();
                	}
                }
            });
            view = actionButton;
        }
        simpleItem.setView(view);
        actionBar.addView(view);

        if (item.getItemId() == R.id.menu_refresh) {
            // Refresh buttons should be stateful, and allow for indeterminate progress indicators,
            // so add those.
            ProgressBar indicator = new ProgressBar(mActivity, null,
            		R.attr.actionbarCompatProgressIndicatorStyle);

            final int buttonWidth = mActivity.getResources().getDimensionPixelSize(
            		R.dimen.actionbar_compat_button_width);
            final int buttonHeight = mActivity.getResources().getDimensionPixelSize(
            		R.dimen.actionbar_compat_height);
            final int progressIndicatorWidth = buttonWidth / 2;

            LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(
            		progressIndicatorWidth, progressIndicatorWidth);
            indicatorLayoutParams.setMargins(
            		(buttonWidth - progressIndicatorWidth) / 2,
            		(buttonHeight - progressIndicatorWidth) / 2,
                    (buttonWidth - progressIndicatorWidth) / 2,
                    0);
            indicator.setLayoutParams(indicatorLayoutParams);
            indicator.setVisibility(View.GONE);
            indicator.setId(R.id.actionbar_compat_item_refresh_progress);
            actionBar.addView(indicator);
        }

        return view;
    }

    /**
     * A {@link android.view.MenuInflater} that reads action bar metadata.
     */
    private class WrappedMenuInflater extends MenuInflater {
        MenuInflater mInflater;

        public WrappedMenuInflater(Context context, MenuInflater inflater) {
            super(context);
            mInflater = inflater;
        }

        @Override
        public void inflate(int menuRes, Menu menu) {
        	if (!(menu instanceof SimpleMenu)) {
        		mInflater.inflate(menuRes, menu);
        		return;
        	}
            loadActionBarMetadata(menuRes);
            mInflater.inflate(menuRes, menu);
            for (int i = 0; i < menu.size(); i++) {
            	SimpleMenuItem item = (SimpleMenuItem) menu.getItem(i);
            	int itemId = item.getItemId();
            	if (mActionItemIdsToShowAsActionLookup.containsKey(itemId)) {
            		item.setShowAsAction(mActionItemIdsToShowAsActionLookup.get(itemId));
            	}
            	if (mActionItemIdsToActionLayoutLookup.containsKey(itemId)) {
            		item.setActionView(mActionItemIdsToActionLayoutLookup.get(itemId));
            	}
            }
        }

        /**
         * Loads action bar metadata from a menu resource, storing a list of menu item IDs that
         * should be shown on-screen (i.e. those with showAsAction set to always or ifRoom). 
         * @param menuResId
         */
        private void loadActionBarMetadata(int menuResId) {
            XmlResourceParser parser = null;
            try {
                parser = mActivity.getResources().getXml(menuResId);

                int eventType = parser.getEventType();
                int itemId;
                int showAsAction;

                boolean eof = false;
                while (!eof) {
                    switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (!parser.getName().equals("item")) {
                            break;
                        }

                        itemId = parser.getAttributeResourceValue(MENU_RES_NAMESPACE,
                        		MENU_ATTR_ID, 0);
                        if (itemId == 0) {
                            break;
                        }

                        showAsAction = parser.getAttributeIntValue(MENU_RES_NAMESPACE,
                        		MENU_ATTR_SHOW_AS_ACTION, -1);

                        mActionItemIdsToShowAsActionLookup.put(itemId, showAsAction);
                        if ((showAsAction & MenuItem.SHOW_AS_ACTION_ALWAYS) != 0 ||
                        		(showAsAction & MenuItem.SHOW_AS_ACTION_IF_ROOM) != 0) {
                        	int actionLayout = parser.getAttributeResourceValue(MENU_RES_NAMESPACE,
                            		MENU_ATTR_ACTION_LAYOUT, 0);
                            if (actionLayout != 0) {
                                mActionItemIdsToActionLayoutLookup.put(itemId, actionLayout);
                            }
                            mActionItemIds.add(itemId);
                        }

                        break;

                    case XmlPullParser.END_DOCUMENT:
                        eof = true;
                        break;
                    }

                    eventType = parser.next();
                }
            } catch (XmlPullParserException e) {
                throw new InflateException("Error inflating menu XML", e);
            } catch (IOException e) {
                throw new InflateException("Error inflating menu XML", e);
            } finally {
                if (parser != null) {
                    parser.close();
                }
            }
        }
    }

    private static class HomeView extends LinearLayout {
    	private View mUpView;
    	private ImageView mIconView;

    	public HomeView(Context context) {
    		super(context);
    	}

    	public HomeView(Context context, AttributeSet attrs) {
    		super(context, attrs);
    	}

    	public HomeView(Context context, AttributeSet attrs, int defStyle) {
    		super(context, attrs, defStyle);
    	}

    	public void setUp(boolean isUp) {
    		mUpView.setVisibility(isUp ? VISIBLE : INVISIBLE);
    	}

    	public void setIcon(Drawable icon) {
    		mIconView.setImageDrawable(icon);
    	}

    	@Override
    	protected void onFinishInflate() {
    		super.onFinishInflate();
    		mUpView = findViewById(R.id.actionbar_compat_up);
    		mIconView = (ImageView) findViewById(R.id.actionbar_compat_home);
    	}
    }

    private class ActionMenuPresenter implements SimpleMenuPresenter {
    	@Override
    	public void initForMenu(Context context, SimpleMenu menu) {
    	}
    	@Override
    	public void updateMenuView(SimpleMenu menu, SimpleMenuItem item, int change) {
    		switch (change) {
				case SimpleMenu.ITEM_CHANGED_NEW:
					// TODO
					break;
				case SimpleMenu.ITEM_CHANGED_TITLE:
					if (item != null && item.getView() instanceof ImageButton) {
						ImageButton actionButton = (ImageButton) item.getView();
						actionButton.setContentDescription(item.getTitle());
					}
					break;
				case SimpleMenu.ITEM_CHANGED_ICON:
					if (item != null && item.getView() instanceof ImageButton) {
						ImageButton actionButton = (ImageButton) item.getView();
						actionButton.setImageDrawable(item.getIcon());
					}
					break;
				case SimpleMenu.ITEM_CHANGED_ACTION:
					// TODO
					break;
				case SimpleMenu.ITEM_CHANGED_ACTION_VIEW:
					// TODO
					break;
				case SimpleMenu.ITEM_CHANGED_VISIBILITY:
					if (item != null && item.getView() != null) {
						item.getView().setVisibility(
								item.isVisible() ? View.VISIBLE : View.GONE);
					}
					break;
    		}
    	}
    	@Override
    	public boolean expandItemActionView(SimpleMenu menu, SimpleMenuItem item) {
    		return false;
    	}
    	@Override
    	public boolean collapseItemActionView(SimpleMenu menu, SimpleMenuItem item) {
    		return false;
    	}
    }

    private class ExpandedActionViewMenuPresenter implements SimpleMenuPresenter {
    	SimpleMenu mMenu;
    	SimpleMenuItem mCurrentExpandedItem;

    	@Override
    	public void initForMenu(Context context, SimpleMenu menu) {
    		mMenu = menu;
    	}
    	@Override
    	public void updateMenuView(SimpleMenu menu, SimpleMenuItem item, int change) {
    	}
    	@Override
    	public boolean expandItemActionView(SimpleMenu menu, SimpleMenuItem item) {
            final ViewGroup actionBarCompat = getActionBarCompat();
            if (actionBarCompat == null) {
                return false;
            }

    		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        			ViewGroup.LayoutParams.FILL_PARENT,
        			ViewGroup.LayoutParams.WRAP_CONTENT);
    		params.weight = 1;

    		mExpandedActionView = item.getActionView();
    		mExpandedHomeLayout.setIcon(mIcon.getConstantState().newDrawable(
    				mActivity.getResources()));
    		mCurrentExpandedItem = item;
    		if (mExpandedHomeLayout.getParent() != actionBarCompat) {
    			actionBarCompat.addView(mExpandedHomeLayout, 1);
    		}
    		if (mExpandedActionView.getParent() != actionBarCompat) {
    			actionBarCompat.addView(mExpandedActionView, 2, params);
    		}
    		mHomeLayout.setVisibility(View.GONE);

    		LinearLayout.LayoutParams springLayoutParams =
    				(LinearLayout.LayoutParams) mTitleText.getLayoutParams();
    		springLayoutParams.weight = 0;
    		mTitleText.setLayoutParams(springLayoutParams);
    		mTitleText.setVisibility(View.INVISIBLE);
    		if (item.isVisible() && item.getView() != null) {
    			item.getView().setVisibility(View.GONE);
    		}
    		item.setActionViewExpanded(true);
    		actionBarCompat.requestLayout();
    		item.setActionViewExpanded(true);
    		//if (mExpandedActionView instanceof CollapsibleActionView) {
    		//	((CollapsibleActionView) mExpandedActionView).onActionViewExpanded();
    		//}

    		return true;
    	}
    	@Override
    	public boolean collapseItemActionView(SimpleMenu menu, SimpleMenuItem item) {
            final ViewGroup actionBar = getActionBarCompat();
            if (actionBar == null) {
                return false;
            }
    		//if (mExpandedActionView instanceof CollapsibleActionView) {
    		//	((CollapsibleActionView) mExpandedActionView).onActionViewCollapsed();
    		//}
            actionBar.removeView(mExpandedActionView);
            actionBar.removeView(mExpandedHomeLayout);
            mExpandedActionView = null;
            if ((mDisplayOptions & DISPLAY_SHOW_HOME) != 0) {
            	mHomeLayout.setVisibility(View.VISIBLE);
            }

    		LinearLayout.LayoutParams springLayoutParams =
    				(LinearLayout.LayoutParams) mTitleText.getLayoutParams();
    		springLayoutParams.weight = 1;
    		mTitleText.setLayoutParams(springLayoutParams);
    		if ((mDisplayOptions & DISPLAY_SHOW_TITLE) != 0) {
    			mTitleText.setVisibility(View.VISIBLE);
    		}
    		if (item.isVisible() && item.getView() != null) {
    			item.getView().setVisibility(View.VISIBLE);
    		}
    		item.setActionViewExpanded(false);
            mExpandedHomeLayout.setIcon(null);
            mCurrentExpandedItem = null;
            actionBar.requestLayout();

            return true;
    	}
    }

    private void setDisplayOptions(int options) {
    	final int flagsChanged = mDisplayOptions == -1 ? -1 : options ^ mDisplayOptions;
    	mDisplayOptions = options;

    	final boolean showHome = (options & DISPLAY_SHOW_HOME) != 0;
    	final int vis = showHome && mExpandedActionView == null ? View.VISIBLE : View.GONE;

    	mHomeLayout.setVisibility(vis);

    	if ((flagsChanged & DISPLAY_HOME_AS_UP) != 0) {
    		final boolean setUp = (options & DISPLAY_HOME_AS_UP) != 0;
    		mHomeLayout.setUp(setUp);
    		if (setUp) {
    			setHomeButtonEnabled(true);
    		}
    	}

    	if ((flagsChanged & DISPLAY_SHOW_TITLE) != 0) {
    		if ((options & DISPLAY_SHOW_TITLE) != 0) {
    			if (mTitle != null) {
    				mTitleText.setText(mTitle);
    			}
    			mTitleText.setVisibility(View.VISIBLE);
    		} else {
    			mTitleText.setText("");
    			mTitleText.setVisibility(View.INVISIBLE);
    		}
    	}
    	if (!mHomeLayout.isEnabled()) {
    		mHomeLayout.setContentDescription(null);
    	} else if ((options & DISPLAY_HOME_AS_UP) != 0) {
    		mHomeLayout.setContentDescription(mActivity.getResources().getText(
    				R.string.action_bar_up_description));
    	} else {
    		mHomeLayout.setContentDescription(mActivity.getResources().getText(
    				R.string.action_bar_home_description));
        }
    }

    private void setDisplayOptions(int options, int mask) {
    	setDisplayOptions((options & mask) | (mDisplayOptions & ~mask));
    }

    /**
     * See {@link android.app.ActionBar#setIcon(int)}.
     * Note: Logo is not supported.
     * @throws IllegalStateException If action bar is not initialized.
     */
    public void setIcon(int resId) {
    	setIcon(mActivity.getResources().getDrawable(resId));
    }

    /**
     * See {@link android.app.ActionBar#setIcon(Drawable)}.
     * Note: Logo is not supported.
     * @throws IllegalStateException If action bar is not initialized.
     */
    public void setIcon(Drawable icon) {
    	if (!mInitialized) {
    		throw new IllegalStateException("ActionBar has not been initialized.");
    	}
    	mIcon = icon;
    	if (icon != null) {
    		mHomeLayout.setIcon(icon);
    	}
    }

    /**
     * See {@link android.app.ActionBar#setTitle(CharSequence)}.
     * @throws IllegalStateException If action bar is not initialized.
     */
	public void setTitle(CharSequence title) {
    	if (!mInitialized) {
    		throw new IllegalStateException("ActionBar has not been initialized.");
    	}
    	mTitle = title;
    	mUserTitle = true;
    	if ((mDisplayOptions & DISPLAY_SHOW_TITLE) != 0) {
    		mTitleText.setText(mTitle);
    	}
	}

	/**
	 * See {@link android.app.ActionBar#setTitle(int)}.
	 * @throws IllegalStateException If action bar is not initialized.
	 */
	public void setTitle(int resId) {
		setTitle(mActivity.getString(resId));
	}

	/**
	 * See {@link android.app.ActionBar#setDisplayShowHomeEnabled(boolean)}.
	 * @throws IllegalStateException If action bar is not initialized.
	 */
    public void setDisplayShowHomeEnabled(boolean showHome) {
    	if (!mInitialized) {
    		throw new IllegalStateException("ActionBar has not been initialized.");
    	}
    	setDisplayOptions(showHome ? DISPLAY_SHOW_HOME : 0, DISPLAY_SHOW_HOME);
    }

    /**
     * See {@link android.app.ActionBar#setDisplayHomeAsUpEnabled(boolean)}.
     * @throws IllegalStateException If action bar is not initialized.
     */
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
    	if (!mInitialized) {
    		throw new IllegalStateException("ActionBar has not been initialized.");
    	}
    	setDisplayOptions(showHomeAsUp ? DISPLAY_HOME_AS_UP : 0, DISPLAY_HOME_AS_UP);
    }

    /**
     * See {@link android.app.ActionBar#setDisplayShowTitleEnabled(boolean)}.
     * @throws IllegalStateException If action bar is not initialized.
     */
    public void setDisplayShowTitleEnabled(boolean showTitle) {
    	if (!mInitialized) {
    		throw new IllegalStateException("ActionBar has not been initialized.");
    	}
    	setDisplayOptions(showTitle ? DISPLAY_SHOW_TITLE : 0, DISPLAY_SHOW_TITLE);
    }

    /**
     * See {@link android.app.ActionBar#setBackgroundDrawable(Drawable)}.
     * @throws IllegalStateException If action bar is not initialized.
     * 
     */
	public void setBackgroundDrawable(Drawable d) {
    	if (!mInitialized) {
    		throw new IllegalStateException("ActionBar has not been initialized.");
    	}
    	getActionBarCompat().setBackgroundDrawable(d);
	}

	/**
	 * See {@link android.app.ActionBar#getTitle()}.
	 * @throws IllegalStateException If action bar is not initialized.
	 */
    public CharSequence getTitle() {
    	if (!mInitialized) {
    		throw new IllegalStateException("ActionBar has not been initialized.");
    	}
    	return mTitle;
    }

    /**
     * See {@link android.app.ActionBar#setHomeButtonEnabled(boolean)}.
     * @throws IllegalStateException If action bar is not initialized.
     */
	public void setHomeButtonEnabled(boolean enabled) {
    	if (!mInitialized) {
    		throw new IllegalStateException("ActionBar has not been initialized.");
    	}
    	mHomeLayout.setEnabled(enabled);
    	mHomeLayout.setFocusable(enabled);
		if (!enabled) {
			mHomeLayout.setContentDescription(null);
		} else if ((mDisplayOptions & DISPLAY_HOME_AS_UP) != 0) {
			mHomeLayout.setContentDescription(mActivity.getResources().getText(
                    R.string.action_bar_up_description));
		} else {
			mHomeLayout.setContentDescription(mActivity.getResources().getText(
                    R.string.action_bar_home_description));
		}
	}
}
