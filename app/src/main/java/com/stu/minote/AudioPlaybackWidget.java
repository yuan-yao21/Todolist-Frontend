package com.stu.minote;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.stu.minote.databinding.WidgetAudioPlaybackBinding;

import java.io.File;
import java.io.IOException;

public class AudioPlaybackWidget extends LinearLayout {
    private LinearLayout _parent;
    private ExoPlayer player;
    private MediaItem mediaItem;
    private File _audioFile;
    private Uri audioFileUri;
    private WidgetAudioPlaybackBinding binding;

    public View getView() {
        return binding.getRoot();
    }

    @OptIn(markerClass = UnstableApi.class) private void init(LinearLayout parent, Context context, File audioFile) throws IOException {
        _parent = parent;
        _audioFile = audioFile;

        // 填充布局
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            binding = WidgetAudioPlaybackBinding.inflate(inflater);
        }

        // 初始化播放器
        player = new ExoPlayer.Builder(context).build();
        audioFileUri = Uri.fromFile(audioFile);
        mediaItem = MediaItem.fromUri(audioFileUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPauseAtEndOfMediaItems(true);
        // player.play();
        binding.playerView.setPlayer(player);
        binding.deleteButton.setOnClickListener(v -> {
            player.release();
            audioFile.delete();
            _parent.removeView(this.getView());
        });
    }
    public AudioPlaybackWidget(LinearLayout parent, Context context, File audioFile) throws IOException {
        super(context);
        init(parent, context, audioFile);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        player.release();
        _audioFile.delete();
    }

}
