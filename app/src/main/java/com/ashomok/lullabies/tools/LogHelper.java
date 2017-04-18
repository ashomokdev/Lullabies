/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashomok.lullabies.tools;

import android.media.MediaMetadataRetriever;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.ashomok.lullabies.BuildConfig;

import java.util.List;


public class LogHelper {

    private static final String LOG_PREFIX = "mLog";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }


    //todo remove next 3 methods
    public static void printList1(String TAG, List<MediaSessionCompat.QueueItem> items)
    {
        for (MediaSessionCompat.QueueItem item: items) {
            Log.d(TAG, item.getDescription().getTitle().toString() );
        }
    }

    public static void printList(String TAG, List<MediaBrowserCompat.MediaItem> items)
    {
        for (MediaBrowserCompat.MediaItem item: items) {
            Log.d(TAG, item.getDescription().getTitle().toString() + "mediaID" + item.getMediaId() );
        }
    }


    public static void printList2(String TAG, List<MediaMetadataCompat> items)
    {
        for (MediaMetadataCompat item: items) {
            Log.d(TAG, item.getDescription().getTitle().toString() );
        }
    }
    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }
}
