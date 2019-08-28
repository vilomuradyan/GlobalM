package com.globalm.platform.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.globalm.platform.models.gopro.hero4.GoProVideoDataContainer;
import com.globalm.platform.models.gopro.hero4.MediaResponse;
import com.globalm.platform.network.GoProAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public abstract class GoProUtil {
    private static final String GO_PRO_FILE_NAME = "DownloadedVideoFile.mp4";
    private static final String VIDEO_PATH = "videos/DCIM/";
    private static final String VIDEO_THUMBNAIL_FILE_PATH = "gp/gpMediaMetadata?p=";

    public static String buildVideoDownloadUrl(String folder, String fileName) {
        return buildUrl(folder, VIDEO_PATH, fileName);
    }

    public static List<GoProVideoDataContainer> buildVideoDataContainers(MediaResponse mediaResponse) {
        List<MediaResponse.Media> filteredMedia = filterMedia(mediaResponse.getMedia());
        List<GoProVideoDataContainer> urls = new ArrayList<>();
        for (MediaResponse.Media media : filteredMedia) {
            for (MediaResponse.Media.FS fs : media.getFs()) {
                urls.add(new GoProVideoDataContainer(buildThumbnailUrl(media.getD(),
                        fs.getN()),
                        media.getD(),
                        fs.getN())
                );
            }
        }

        return urls;
    }

    @Nullable
    public static File saveFile(Context context, ResponseBody responseBody) {
        File file = new File(
                context.getFilesDir().getAbsolutePath() +
                        File.separator +
                        GO_PRO_FILE_NAME);
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            byte[] fileReader = new byte[4096];
            inputStream = responseBody.byteStream();
            outputStream = new FileOutputStream(file);

            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);
            }

            outputStream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            file = null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return file;
        }
    }

    private static String buildThumbnailUrl(String folder, String fileName) {
        return buildUrl(folder, VIDEO_THUMBNAIL_FILE_PATH, fileName);
    }

    private static List<MediaResponse.Media> filterMedia(List<MediaResponse.Media> mediaList) {
        List<MediaResponse.Media> filteredMedia = new ArrayList<>();

        for (MediaResponse.Media media : mediaList) {
            List<MediaResponse.Media.FS> fsList = new ArrayList<>();
            for (MediaResponse.Media.FS fs : media.getFs()) {
                if (fs.getLs() != null) {
                    fsList.add(fs);
                }
            }
            filteredMedia.add(new MediaResponse.Media(media.getD(), fsList));
        }

        return filteredMedia;
    }

    private static String buildUrl(String folder, String path, String fileName) {
        return String.format("%s%s%s/%s",
                GoProAPI.GO_PRO_HERO4_BASE_URL,
                path,
                folder,
                fileName);
    }
}
