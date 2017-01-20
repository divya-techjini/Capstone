/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package udacity.com.capstone.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import udacity.com.capstone.R;
import udacity.com.capstone.data.Record;
import udacity.com.capstone.data.provider.RecordContract;

/**
 * An abstract activity that handles authorization and connection to the Drive
 * services.
 */
public class BaseDemoActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private ProgressDialog mProgressDialog;
    private static final String TAG = "BaseDriveActivity";
    private int notificationId = 1;
    private DriveId mFolderDriveId;
    int count = 0;
    List<Record> items = new ArrayList<>();
    int uploadCount = 0;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private GoogleApiClient mGoogleApiClient;


    protected void connectClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    protected void createFolder() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(getString(R.string.app_name).concat("_").concat(timeStamp)).build();
        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(
                mGoogleApiClient, changeSet).setResultCallback(callback);
    }

    final ResultCallback<DriveFolder.DriveFolderResult> callback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage(getString(R.string.error_file));
                return;
            }
            mFolderDriveId = result.getDriveFolder().getDriveId();
            Cursor cursor = getContentResolver().query(RecordContract.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                try {
                    int columne_one = cursor.getColumnIndex(RecordContract.NAME);
                    int columne_two = cursor.getColumnIndex(RecordContract.AUDIO_PATH);
                    while (cursor.moveToNext()) {
                        Record record = new Record(cursor.getString(columne_one), cursor.getString(columne_two));
                        items.add(record);


                    }
                } finally {
                    cursor.close();
                    Log.d(getClass().getSimpleName(), "cursor closed");
                }
            }
            new EditContentsAsyncTask().execute();

        }
    };

    public class EditContentsAsyncTask extends AsyncTask<Void, Void, Boolean> {

        public EditContentsAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            sendNotification();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            int count = 0;
            for (final Record record : items) {

                DriveApi.DriveContentsResult driveContentsResult = Drive.DriveApi.newDriveContents(mGoogleApiClient).await();
                if (!driveContentsResult.getStatus().isSuccess()) {
                    hideLoadingDialog();
                    return false;
                }
                DriveContents driveContents = driveContentsResult.getDriveContents();
                DriveFolder folder = mFolderDriveId.asDriveFolder();
                boolean response = uploadRecord(folder, driveContents, record);

                if (response) {
                    count++;
                }
            }
            if (count == items.size()) {
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText(getString(R.string.success));
            notificationManager.notify(notificationId, notificationBuilder.build());
            if (!result) {
                showMessage(getString(R.string.error));
                return;
            }
            if (items.size() == 0) {
                showMessage(getString(R.string.success_nothin));
            } else {
                showMessage(getString(R.string.success));
            }

            mGoogleApiClient.disconnect();
        }
    }


    private boolean uploadRecord(DriveFolder folder, DriveContents driveContents, Record record) {
        boolean response = true;
        OutputStream outputStream = driveContents.getOutputStream();
        try {
            Uri uri = Uri.parse(record.getAudioPath());
            InputStream inputStream = getContentResolver().openInputStream(uri);

            if (inputStream != null) {
                byte[] data = new byte[1024];
                while (inputStream.read(data) != -1) {
                    outputStream.write(data);
                }
                inputStream.close();
            }

            outputStream.close();
        } catch (IOException e) {
            Log.e("EX", e.getMessage());
        }
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(record.getName() + ".3gp")
                .setMimeType("audio/3gpp")
                .setStarred(true).build();

        DriveFolder.DriveFileResult result = folder.createFile(mGoogleApiClient, changeSet, driveContents).await();
        if (!result.getStatus().isSuccess()) {

            response = false;
        }
        return response;
    }

    private void sendNotification() {

        notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.uploading))
                .setContentText(getString(R.string.uploading_progress))
                .setSmallIcon(android.R.drawable.stat_notify_sync);

        notificationBuilder.setProgress(0, 0, true);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideLoadingDialog();
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    protected void disConnectClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        hideLoadingDialog();
        Log.i(TAG, "GoogleApiClient connected");
        createFolder();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        hideLoadingDialog();
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        hideLoadingDialog();
        //Toast.makeText(this,result.toString(),Toast.LENGTH_LONG).show();
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            hideLoadingDialog();
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();

            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            hideLoadingDialog();
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    public synchronized void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this) {
                @Override
                public void onDetachedFromWindow() {
                    mProgressDialog = null;
                }
            };
            mProgressDialog.setMessage(getString(R.string.upload));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public synchronized void hideLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }
}
