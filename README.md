# ActionBarCompat #
[ActionBar](http://developer.android.com/guide/topics/ui/actionbar.html) is a design pattern introduced in API 11. To support older versions of Android Google provides [Action Bar Compatibility](http://developer.android.com/resources/samples/ActionBarCompat/index.html) sample app.

This [library project](http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject) extends the functionality of the provided ActionBarCompat to support more APIs.

Based on:

* [ActionBarCompat](http://developer.android.com/resources/samples/ActionBarCompat/index.html)
* [ActionBarCompat-with-ActionViews](https://github.com/bookwormat/ActionBarCompat-with-ActionViews)

## Usage ##

### Setting action bar properties ###
If you want to set action bar properties in `onCreate()`, you need to initialize action bar first by calling `getActionBarHelper().initActionBar()`. This initializes action bar for pre-Honeycomb devices, does nothing for later API levels.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getActionBarHelper().initActionBar();
        ActionBarCompat.setDisplayShowHomeEnabled(this, true);
        ...

Another option is to set action bar properties in `onPostCreate()`, this way there is no need to call `getActionBarHelper().initActionBar()` as action bar will be already initialized in this stage.

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActionBarCompat.setDisplayShowTitleEnabled(this, true);
        ActionBarCompat.setTitle(this, R.string.actionbar_custom_title);
        ...
    }

### Accessing action views ###

You can access action view by calling `getActionView()`.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options, menu);
        if (mMenu != null) {
            return super.onCreateOptionsMenu(menu);
        }
        mMenu = menu;
        MenuItem item = mMenu.findItem(R.id.menu_search);
        EditText editText = (EditText) MenuItemCompat.getActionView(item);
        ...
*Note: `MenuItemCompat` has to be of type `sk.m217.actionbarcompat.MenuItemCompat` instead of type `android.support.v4.view.MenuItemCompat`.*

### Handling collapsible action views ###

You can handle expanding and collapsing of action view by defining an `MenuItemCompat.OnActionExpandListener` and registering it with `setOnActionExpandListener()`.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options, menu);
        if (mMenu != null) {
            return super.onCreateOptionsMenu(menu);
        }
        mMenu = menu;
        MenuItem item = mMenu.findItem(R.id.menu_search);
        if (item != null) {
            MenuItemCompat.setOnActionExpandListener(item,
                    new MenuItemCompat.OnActionExpandListenerCompat() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when collapsed
                    return true;
                }
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });
        }
        ...

See sample application for more details how to use ActionBarCompat library.

## Supported APIs ##

Collapsible action views are not supported for Honeycomb devices.

XML attributes support:

    android:showAsAction="never|always|collapseActionView"
    android:actionLayout

Supported [ActionBar](http://developer.android.com/reference/android/app/ActionBar.html) APIs:

* [void setIcon(int)](http://developer.android.com/reference/android/app/ActionBar.html#setIcon%28int%29)
* [void setIcon(Drawable)](http://developer.android.com/reference/android/app/ActionBar.html#setIcon%28android.graphics.drawable.Drawable%29)
* [void setTitle(CharSequence)](http://developer.android.com/reference/android/app/ActionBar.html#setTitle%28java.lang.CharSequence%29)
* [void setTitle(int)](http://developer.android.com/reference/android/app/ActionBar.html#setTitle%28int%29)
* [void setDisplayShowHomeEnabled(boolean)](http://developer.android.com/reference/android/app/ActionBar.html#setDisplayShowHomeEnabled%28boolean%29)
* [void setDisplayHomeAsUpEnabled(boolean)](http://developer.android.com/reference/android/app/ActionBar.html#setDisplayHomeAsUpEnabled%28boolean%29)
* [void setDisplayShowTitleEnabled(boolean)](http://developer.android.com/reference/android/app/ActionBar.html#setDisplayShowTitleEnabled%28boolean%29)
* [void setBackgroundDrawable(Drawable)](http://developer.android.com/reference/android/app/ActionBar.html#setBackgroundDrawable%28android.graphics.drawable.Drawable%29)
* [CharSequence getTitle()](http://developer.android.com/reference/android/app/ActionBar.html#getTitle%28%29)
* [void setHomeButtonEnabled(boolean)](http://developer.android.com/reference/android/app/ActionBar.html#setHomeButtonEnabled%28boolean%29)

Supported [MenuItem](http://developer.android.com/reference/android/view/MenuItem.html) APIs:

* [View getActionView()](http://developer.android.com/reference/android/view/MenuItem.html#getActionView%28%29)
* [boolean expandActionView()](http://developer.android.com/reference/android/view/MenuItem.html#expandActionView%28%29) (not supported for Honeycomb devices)
* [boolean collapseActionView()](http://developer.android.com/reference/android/view/MenuItem.html#collapseActionView%28%29) (not supported for Honeycomb devices)
* [boolean isActionViewExpanded()](http://developer.android.com/reference/android/view/MenuItem.html#isActionViewExpanded%28%29) (not supported for Honeycomb devices)
* [MenuItem setOnActionExpandListener(OnActionExpandListener)](http://developer.android.com/reference/android/view/MenuItem.html#setOnActionExpandListener%28android.view.MenuItem.OnActionExpandListener%29) (not supported for Honeycomb devices)
* [MenuItem setTitle(CharSequence)](http://developer.android.com/reference/android/view/MenuItem.html#setTitle%28java.lang.CharSequence%29)
* [MenuItem setTitle(int)](http://developer.android.com/reference/android/view/MenuItem.html#setTitle%28int%29)
* [CharSequence getTitle()](http://developer.android.com/reference/android/view/MenuItem.html#getTitle%28%29)
* [MenuItem setIcon(Drawable)](http://developer.android.com/reference/android/view/MenuItem.html#setIcon%28android.graphics.drawable.Drawable%29)
* [MenuItem setIcon(int)](http://developer.android.com/reference/android/view/MenuItem.html#setIcon%28int%29)
* [Drawable getIcon()](http://developer.android.com/reference/android/view/MenuItem.html#getIcon%28%29)
* [MenuItem setEnabled(boolean)](http://developer.android.com/reference/android/view/MenuItem.html#setEnabled%28boolean%29)
* [boolean isEnabled()](http://developer.android.com/reference/android/view/MenuItem.html#isEnabled%28%29)
* [MenuItem setIntent(Intent)](http://developer.android.com/reference/android/view/MenuItem.html#setIntent%28android.content.Intent%29)
* [Intent getIntent()](http://developer.android.com/reference/android/view/MenuItem.html#getIntent%28%29)
* [MenuItem setVisible(boolean)](http://developer.android.com/reference/android/view/MenuItem.html#setVisible%28boolean%29)
* [boolean isVisible()](http://developer.android.com/reference/android/view/MenuItem.html#isVisible%28%29)
* [MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener)](http://developer.android.com/reference/android/view/MenuItem.html#setOnMenuItemClickListener%28android.view.MenuItem.OnMenuItemClickListener%29)
