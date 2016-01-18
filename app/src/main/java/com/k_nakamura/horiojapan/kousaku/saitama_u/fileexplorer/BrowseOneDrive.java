package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.onedrive.sdk.concurrency.ICallback;

public class BrowseOneDrive extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        final BaseApplication app = (BaseApplication)getApplication();
        app.goToWifiSettingsIfDisconnected();

        final ICallback<Void> serviceCreated = new DefaultCallback<Void>(this) {
            @Override
            public void success(final Void result) {
                navigateToRoot();
            }
        };

        try {
            app.getOneDriveClient();
            navigateToRoot();
        } catch (final UnsupportedOperationException ignored) {
            app.createOneDriveClient(this, serviceCreated);
        }
    }

    /**
     * Navigate to the root object in the onedrive
     */
    private void navigateToRoot() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ItemFragment.newInstance("root"))
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onFragmentInteraction(final DisplayItem item) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ItemFragment.newInstance(item.getId()))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse_onedrive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
