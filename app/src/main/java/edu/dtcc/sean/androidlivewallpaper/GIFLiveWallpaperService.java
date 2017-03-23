package edu.dtcc.sean.androidlivewallpaper;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by Sean on 3/23/2017.
 */

public class GIFLiveWallpaperService extends WallpaperService
{
    @Override
    public WallpaperService.Engine onCreateEngine()
    {
        try
        {
            Movie movie = Movie.decodeStream(
                    getResources().getAssets().open("colors.gif"));

            return new GIFWallpaperEngine(movie);
        }
        catch(IOException e)
        {
            Log.d("GIF", "Could not load asset");
            return null;
        }
    }

    private class GIFWallpaperEngine extends WallpaperService.Engine
    {
        // Represents the delay between re-draw operations
        private final int frameDuration = 10;

        private SurfaceHolder surfaceHolder;
        private Movie movie;
        private boolean isVisible;
        private Handler handler;

        public GIFWallpaperEngine(Movie movie)
        {
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
            super.onCreate(surfaceHolder);
            this.surfaceHolder = surfaceHolder;
        }

        private Runnable drawGIF = new Runnable()
        {
            public void run() {
                draw();
            }
        };

        private void draw()
        {
            if (isVisible)
            {
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.save();

                // Set size and position of image
                canvas.scale(5f, 4f);
                movie.draw(canvas, -100, 0);
                canvas.restore();
                surfaceHolder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, frameDuration);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            this.isVisible = visible;
            if (visible)
            {
                handler.post(drawGIF);
            } else
            {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
        }
    }
}
