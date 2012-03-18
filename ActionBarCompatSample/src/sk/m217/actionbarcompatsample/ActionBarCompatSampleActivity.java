package sk.m217.actionbarcompatsample;

import sk.m217.actionbarcompat.ActionBarActivity;
import sk.m217.actionbarcompat.ActionBarCompat;
import sk.m217.actionbarcompat.MenuItemCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ActionBarCompatSampleActivity extends ActionBarActivity {

	private Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // If we want to set action bar properties here in onCreate(), we need to
        // initialize action bar first. Does nothing for Honeycomb and above devices.
        getActionBarHelper().initActionBar();

        ActionBarCompat.setDisplayShowHomeEnabled(this, true);
        ActionBarCompat.setDisplayHomeAsUpEnabled(this, false);
        ActionBarCompat.setHomeButtonEnabled(this, true);
        ActionBarCompat.setBackgroundDrawable(this,
        		getResources().getDrawable(R.drawable.actionbar_background));
        ActionBarCompat.setIcon(this, getResources().getDrawable(R.drawable.ic_launcher));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
    	super.onPostCreate(savedInstanceState);
    	// If we set action bar properties here in onPostCreate(),
    	// we do not need to call getActionBarHelper().initActionBar().
    	ActionBarCompat.setDisplayShowTitleEnabled(this, true);
    	ActionBarCompat.setTitle(this, R.string.actionbar_custom_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options, menu);

        if (mMenu != null) {
        	return super.onCreateOptionsMenu(menu);
        }

        mMenu = menu;

        // Handling collapsible action views
        MenuItem item = mMenu.findItem(R.id.menu_search);
        if (item != null) {
            // MenuItemCompat has to be of type sk.m217.actionbarcompat.MenuItemCompat,
            // instead of android.support.v4.view.MenuItemCompat.
        	MenuItemCompat.setOnActionExpandListener(item,
        			new MenuItemCompat.OnActionExpandListenerCompat() {
        		@Override
        		public boolean onMenuItemActionExpand(MenuItem item) {
        			Toast.makeText(ActionBarCompatSampleActivity.this,
        					"onMenuItemActionExpand()", Toast.LENGTH_SHORT).show();
        			return true;
        		}
        		@Override
        		public boolean onMenuItemActionCollapse(MenuItem item) {
        			Toast.makeText(ActionBarCompatSampleActivity.this,
        					"onMenuItemActionCollapse()", Toast.LENGTH_SHORT).show();
        			return true;
        		}
        	});
        }

        // Accessing action views
        EditText editText = (EditText) MenuItemCompat.getActionView(item);
        if (editText != null) {
        	editText.setText(R.string.menu_search);
        }

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(ActionBarCompatSampleActivity.this,
						"onMenuItemClick()", Toast.LENGTH_LONG).show();
				return false;
			}
		});

        // Modifying items
        item.setIcon(R.drawable.ic_action_search)
        	.setTitle(R.string.menu_search)
        	.setIntent(null)
        	.setVisible(true)
        	.setEnabled(true);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        	case android.R.id.home:
        		Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
        		break;

        	case R.id.menu_refresh:
        		Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
        		getActionBarHelper().setRefreshActionItemState(true);
        		getWindow().getDecorView().postDelayed(
        				new Runnable() {
        					@Override
        					public void run() {
        						getActionBarHelper().setRefreshActionItemState(false);
        					}
        				}, 1000);
        		break;

        	case R.id.menu_search:
        		Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
        		break;
        }
        return super.onOptionsItemSelected(item);    	
    }
}