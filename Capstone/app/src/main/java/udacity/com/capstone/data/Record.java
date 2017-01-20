/*
 * This file is part of the Android-OrmLiteContentProvider package.
 *
 * Copyright (c) 2012, Android-OrmLiteContentProvider Team.
 *                     Jaken Jarvis (jaken.jarvis@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted via
 * https://github.com/jakenjarvis/Android-OrmLiteContentProvider
 */
package udacity.com.capstone.data;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

import udacity.com.capstone.data.provider.RecordContract;

@AdditionalAnnotation.Contract()
@DatabaseTable(tableName = RecordContract.TABLE_NAME)
@AdditionalAnnotation.DefaultContentUri(authority = RecordContract.AUTHORITY, path = RecordContract.CONTENT_URI_PATH)
@AdditionalAnnotation.DefaultContentMimeTypeVnd(name = RecordContract.MIMETYPE_NAME, type = RecordContract.MIMETYPE_TYPE)
public class Record {

    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    @AdditionalAnnotation.DefaultSortOrder
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String audioPath;

    public Record() {
        // ORMLite needs a no-arg constructor
    }

    public Record(String name, String audioPath) {
        this.id = 0;
        this.name = name;
        this.audioPath = audioPath;
    }

    public int getId() {
        return id;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public String getName() {
        return name;
    }

}
