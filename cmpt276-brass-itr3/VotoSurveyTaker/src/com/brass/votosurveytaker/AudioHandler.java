package com.brass.votosurveytaker;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

/**
 * media class for audio play back
 * 
 * help to setup audio, play, stop, and continue
 * 
 */
public class AudioHandler {
	private static MediaPlayer player;

	/**
	 * Download the song (if necessary) and begin playback. If paused, resume
	 * playing.
	 * 
	 * @param songURL
	 */
	public static void startMusic(final TextView text, String songURL) {
		// Not yet created:
		if (player == null) {
			player = new MediaPlayer();
			text.setText(R.string.preparing);

			// Handle media player errors (such as file not found...)
			player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					stopMusic(text);
					text.setText(R.string.error_playing_media_contact_administrator_or_submit_a_bug_report_);
					switch (extra) {
					case MediaPlayer.MEDIA_ERROR_IO:
						Log.e("MediaDemo", "Media IO error.");
						break;
					case MediaPlayer.MEDIA_ERROR_MALFORMED:
						Log.e("MediaDemo", "Media malformed error.");
						break;
					default:
						Log.e("MediaDemo", "Media error: " + extra);
					}
					return true;
				}
			});

			// Load music in background so it does not hold up UI thread.
			try {
				player.setDataSource(songURL);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			player.prepareAsync();

			// Play music once it is prepared.
			player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					text.setText(R.string.playing);

					// Play
					player.start();
				}
			});

			// When done, free resources.
			player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					stopMusic(text);
				}
			});
		} else {
			// Already have a player, so it must be paused. Just play.
			text.setText(R.string.playing);
			player.start();
		}
	}

	/**
	 * Stop music and free resources
	 */
	public static void stopMusic(TextView text) {
		if (player != null) {
			text.setText(R.string.stopped);
			if (player.isPlaying()) {
				player.stop();
			}
			player.release();
			player = null;
		}
	}

	/**
	 * Pause playback; allow it to be restarted.
	 */
	public static void pauseMusic(TextView text) {
		if (player != null && player.isPlaying()) {
			text.setText(R.string.paused);
			player.pause();
		}
	}
}
