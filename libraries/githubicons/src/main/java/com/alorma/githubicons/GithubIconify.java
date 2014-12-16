/**
 * Copyright 2013 Joan Zapata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * It uses FontAwesome font, licensed under OFL 1.1, which is compatible
 * with this library's license.
 *
 *     http://scripts.sil.org/cms/scripts/render_download.php?format=file&media_id=OFL_plaintext&filename=OFL.txt
 */
package com.alorma.githubicons;

import android.content.Context;
import android.graphics.Typeface;

public final class GithubIconify {

	private static final String TTF_FILE = "octicons-local-2.1.2.ttf";

	private static Typeface typeface = null;

	private GithubIconify() {
		// Prevent instantiation
	}

	/**
	 * The typeface that contains FontAwesome icons.
	 *
	 * @return the typeface, or null if something goes wrong.
	 */
	public static final Typeface getTypeface(Context context) {
		if (typeface == null) {
			typeface = Typeface.createFromAsset(context.getAssets(), TTF_FILE);
		}
		return typeface;
	}

	public static enum IconValue {

		octicon_git_pull_request('\uf009'),
		octicon_issue_opened('\uf026'),
		octicon_issue_closed('\uf028'),
		octicon_x('\uf081'),
		octicon_file_code('\uf010'),
		octicon_file_text('\uf011'),
		octicon_file_directory('\uf016'),
		octicon_file_submodule('\uf017'),
		octicon_file_symlink_directory('\uf0b1'),
		octicon_arrow_left('\uf040'),
		octicon_clock('\uf046'),
		octicon_repo('\uf001'),
		octicon_plus('\uf05d'),
		octicon_comment_discussion('\uf04f'),
		octicon_repo_push('\uf005'),
		octicon_organization('\uf037'),
		octicon_person('\uf018'),
		octicon_gist('\uf00e'),
		octicon_location('\uf060'),
		octicon_mail('\uf03b'),
		octicon_squirrel('\uf0b2'),
		octicon_bug('\uf091'),
		octicon_repo_forked('\uf002'),
		octicon_eye('\uf04e');

		char character;

		IconValue(char character) {
			this.character = character;
		}
	}
}
