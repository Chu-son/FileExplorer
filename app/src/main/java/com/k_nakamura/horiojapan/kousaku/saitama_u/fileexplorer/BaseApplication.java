package com.k_nakamura.horiojapan.kousaku.saitama_u.fileexplorer;

/**
 * Created by user on 2016/01/18.
 * from OneDriveSample
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.LruCache;
import android.widget.Toast;

import com.onedrive.sdk.authentication.ADALAuthenticator;
import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.OneDriveClient;
import com.onedrive.sdk.logger.LoggerLevel;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Base application
 */
public class BaseApplication extends Application {

    /**
     * The number of thumbnails to cache
     */
    private static final int MAX_IMAGE_CACHE_SIZE = 300;

    /**
     * Thumbnail cache
     */
    private LruCache<String, Bitmap> mImageCache;

    /**
     * The service instance
     */
    private final AtomicReference<IOneDriveClient> mClient = new AtomicReference<>();

    /**
     * The system connectivity manager
     */
    private ConnectivityManager mConnectivityManager;

    /**
     * What to do when the application starts
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Create the client configuration
     * @return the newly created configuration
     */
    private IClientConfig createConfig() {
        final MSAAuthenticator msaAuthenticator = new MSAAuthenticator() {
            //MyID
            @Override
            public String getClientId() {
                return "000000004C17C245";
            }

            @Override
            public String[] getScopes() {
                return new String[] {"onedrive.readwrite", "onedrive.appfolder", "wl.offline_access"};
            }
        };

        final ADALAuthenticator adalAuthenticator = new ADALAuthenticator() {
            @Override
            public String getClientId() {
                return "vrMSx9P7x0nJKnP17Ea6vYiK-IM4Unew";
            }

            @Override
            protected String getRedirectUrl() {
                return "https://localhost";
            }
        };

        final IClientConfig config = DefaultClientConfig.createWithAuthenticators(msaAuthenticator,adalAuthenticator);
        config.getLogger().setLoggingLevel(LoggerLevel.Debug);
        return config;
    }

    /**
     * Navigates the user to the wifi settings if there is a connection problem
     *
     * @return if the wifi activity was navigated to
     */
    synchronized boolean goToWifiSettingsIfDisconnected() {
        final NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            Toast.makeText(this, getString(R.string.wifi_unavailable_error_message), Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Clears out the auth token from the application store
     */
    void signOut() {
        if (mClient.get() == null) {
            return;
        }
        mClient.get().getAuthenticator().logout(new ICallback<Void>() {
            @Override
            public void success(final Void result) {
                mClient.set(null);
                final Intent intent = new Intent(getBaseContext(), BrowseOneDrive.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void failure(final ClientException ex) {
                Toast.makeText(getBaseContext(), "Logout error " + ex, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Get an instance of the service
     *
     * @return The Service
     */
    synchronized IOneDriveClient getOneDriveClient() {
        if (mClient.get() == null) {
            throw new UnsupportedOperationException("Unable to generate a new service object");
        }
        return mClient.get();
    }

    /**
     * Used to setup the Services
     * @param activity the current activity
     * @param serviceCreated the callback
     */
    synchronized void createOneDriveClient(final Activity activity, final ICallback<Void> serviceCreated) {
        final DefaultCallback<IOneDriveClient> callback = new DefaultCallback<IOneDriveClient>(activity) {
            @Override
            public void success(final IOneDriveClient result) {
                mClient.set(result);
                serviceCreated.success(null);
            }

            @Override
            public void failure(final ClientException error) {
                serviceCreated.failure(error);
            }
        };
        new OneDriveClient
                .Builder()
                .fromConfig(createConfig())
                .loginAndBuildClient(activity, callback);
    }

    /**
     * Gets the image cache for this application
     *
     * @return the image loader
     */
    public synchronized LruCache<String, Bitmap> getImageCache() {
        if (mImageCache == null) {
            mImageCache = new LruCache<>(BaseApplication.MAX_IMAGE_CACHE_SIZE);
        }
        return mImageCache;
    }
}