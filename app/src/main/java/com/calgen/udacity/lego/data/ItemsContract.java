/*
 * Copyright 2016 Gurupad Mamadapur
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.calgen.udacity.lego.data;

import android.net.Uri;

public class ItemsContract {
	public static final String CONTENT_AUTHORITY = "com.calgen.udacity.lego";
	public static final Uri BASE_URI = Uri.parse("content://com.calgen.udacity.lego");

	interface ItemsColumns {
		/** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
		String _ID = "_id";
		/** Type: TEXT */
		String SERVER_ID = "server_id";
		/** Type: TEXT NOT NULL */
		String TITLE = "title";
		/** Type: TEXT NOT NULL */
		String AUTHOR = "author";
		/** Type: TEXT NOT NULL */
		String BODY = "body";
        /** Type: TEXT NOT NULL */
        String THUMB_URL = "thumb_url";
		/** Type: TEXT NOT NULL */
		String PHOTO_URL = "photo_url";
		/** Type: REAL NOT NULL DEFAULT 1.5 */
		String ASPECT_RATIO = "aspect_ratio";
		/** Type: INTEGER NOT NULL DEFAULT 0 */
		String PUBLISHED_DATE = "published_date";
        /** Type: INTEGER NOT NULL DEFAULT 4342338 - (#424242) */
        String DARK_MUTED_COLOR = "dark_muted_color";
        /** Type: BOOLEAN NOT NULL DEFAULT FALSE*/
        String IS_DARK = "is_dark";
	}

	public static class Items implements ItemsColumns {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.calgen.udacity.lego.items";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.calgen.udacity.lego.items";

        public static final String DEFAULT_SORT = PUBLISHED_DATE + " DESC";

		/** Matches: /items/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath("items").build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildItemUri(long _id) {
			return BASE_URI.buildUpon().appendPath("items").appendPath(Long.toString(_id)).build();
		}

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
	}

	private ItemsContract() {
	}
}
