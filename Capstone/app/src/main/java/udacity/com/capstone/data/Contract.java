package udacity.com.capstone.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String AUTHORITY = "udacity.com.capstone";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_RECORD = "records";

    public static final class Record implements BaseColumns {
//public static final Uri CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY)
// .appendPath(CONTENT_URI_PATH).build();

        public static final Uri uri = BASE_URI.buildUpon().appendPath(PATH_RECORD).build();

        public static final String TABLE_NAME = "records";


        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PATH = "audiopath";
        public static final int POSITION_ID = 0;
        public static final int POSITION_NAME = 1;
        public static final int POSITION_PATH = 2;


        public static final String[] RECORD_COLUMNS = {
                _ID,
                COLUMN_NAME,
                COLUMN_PATH
        };

        public static Uri makeUriForRecord(String name) {
            return uri.buildUpon().appendPath(name).build();
        }

        public static String getRecordFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }


    }

}
