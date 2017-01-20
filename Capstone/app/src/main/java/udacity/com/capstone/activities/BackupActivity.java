package udacity.com.capstone.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import udacity.com.capstone.R;
import udacity.com.capstone.data.Record;
import udacity.com.capstone.data.provider.RecordContract;
import udacity.com.capstone.utils.ApiClientAsyncTask;

public class BackupActivity extends BaseDemoActivity {
    private int notificationId = 1;
    private DriveId mFolderDriveId;
    int count = 0;
    List<Record> items = new ArrayList<>();
    int uploadCount = 0;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        showMessage("connected");


    }

//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_backup);
//        notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        findViewById(R.id.btn_backup).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // createFolder();
//
//            }
//        });
//    }
//
//    final ResultCallback<DriveFolder.DriveFolderResult> callback = new ResultCallback<DriveFolder.DriveFolderResult>() {
//        @Override
//        public void onResult(DriveFolder.DriveFolderResult result) {
//            if (!result.getStatus().isSuccess()) {
//                showMessage("Error while trying to create the folder");
//                return;
//            }
//            showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
//            mFolderDriveId = result.getDriveFolder().getDriveId();
//            Cursor cursor = getContentResolver().query(RecordContract.CONTENT_URI, null, null, null, null);
//            if (cursor != null) {
//                try {
//                    int columne_one = cursor.getColumnIndex(RecordContract.NAME);
//                    int columne_two = cursor.getColumnIndex(RecordContract.AUDIO_PATH);
//                    while (cursor.moveToNext()) {
//                        Record record = new Record(cursor.getString(columne_one), cursor.getString(columne_two));
//                        items.add(record);
//
//
//                    }
//                } finally {
//                    cursor.close();
//                    Log.d(getClass().getSimpleName(), "cursor closed");
//                }
//            }
//            new EditContentsAsyncTask().execute();
//
//        }
//    };
//
//    public class EditContentsAsyncTask extends AsyncTask<Void, Void, Boolean> {
//
//        public EditContentsAsyncTask() {
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            sendNotification();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... args) {
//            int count = 0;
//            for (final Record record : items) {
//
//                DriveApi.DriveContentsResult driveContentsResult = Drive.DriveApi.newDriveContents(getGoogleApiClient()).await();
//                if (!driveContentsResult.getStatus().isSuccess()) {
//                    return false;
//                }
//                DriveContents driveContents = driveContentsResult.getDriveContents();
//                DriveFolder folder = mFolderDriveId.asDriveFolder();
//                boolean response = uploadRecord(folder, driveContents, record);
//
//                if (response) {
//                    count++;
//                }
////                Drive.DriveApi.newDriveContents(getGoogleApiClient())
////                        .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
////                            @Override
////                            public void onResult(DriveApi.DriveContentsResult result) {
////                                if (!result.getStatus().isSuccess()) {
////                                    showMessage("Error while trying to create new file contents");
////                                    return;
////                                }
////
////                                DriveFolder folder = mFolderDriveId.asDriveFolder();
////                                uploadRecord(folder, result.getDriveContents(), record);
////                            }
////                        });
//            }
//            if (count == items.size()) {
//                return true;
//            }
//
//            return false;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            notificationBuilder.setProgress(0, 0, false);
//            notificationBuilder.setContentText("upload done");
//            notificationManager.notify(notificationId, notificationBuilder.build());
//            if (!result) {
//                showMessage("Error while editing contents");
//                return;
//            }
//            showMessage("Successfully uploaded contents");
//        }
//    }
//
//
//    private boolean uploadRecord(DriveFolder folder, DriveContents driveContents, Record record) {
//        boolean response = true;
//        OutputStream outputStream = driveContents.getOutputStream();
//        try {
//            Uri uri = Uri.parse(record.getAudioPath());
//            InputStream inputStream = getContentResolver().openInputStream(uri);
//
//            if (inputStream != null) {
//                byte[] data = new byte[1024];
//                while (inputStream.read(data) != -1) {
//                    outputStream.write(data);
//                }
//                inputStream.close();
//            }
//
//            outputStream.close();
//        } catch (IOException e) {
//            Log.e("EX", e.getMessage());
//        }
//        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                .setTitle(record.getName() + ".3gp")
//                .setMimeType("audio/3gpp")
//                .setStarred(true).build();
//
//
//        // create a file on root folder
//        DriveFolder.DriveFileResult result = folder.createFile(getGoogleApiClient(), changeSet, driveContents).await();
//        if (!result.getStatus().isSuccess()) {
//            //  showMessage("Error while trying to create the file");
//            Log.e("wer ", result.toString());
//            response = false;
//        }
//        return response;
//    }
//
//    private void sendNotification() {
//
//        notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder.setContentTitle("Uploading files")
//                .setContentText("upload in progress")
//                .setSmallIcon(android.R.drawable.stat_notify_sync);
//
//        notificationBuilder.setProgress(0, 0, true);
//        notificationManager.notify(notificationId, notificationBuilder.build());
//    }


}
