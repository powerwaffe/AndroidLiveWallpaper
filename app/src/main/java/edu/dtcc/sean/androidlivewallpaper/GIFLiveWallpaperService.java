package edu.dtcc.sean.androidlivewallpaper;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.IOException;

/** When ran this program creates a live wallpaper from a gif file. The Live wallpaper should
 * be placed within the wallpaper directory under live wallpapers.
 * The live wallpaper should have a custom icon and named created with it upon launch.
 * NOTE: This program must be ran with launch options set to "nothing". */

public class GIFLiveWallpaperService extends WallpaperService
{
    @Override
    public WallpaperService.Engine onCreateEngine()
    {
        try
        {
            Movie movie = Movie.decodeStream(
                    getResources().getAssets().open("colors.gif"));

            return new LiveWallpaperEngine(movie);
        }
        catch(IOException e)
        {
            // Display error
            Log.d("GIF", "Could not GIF");
            return null;
        }
    }

    private class LiveWallpaperEngine extends WallpaperService.Engine
    {
        private final int frameDuration = 20; // Represents the delay between re-draw operations

        private SurfaceHolder surfaceHolder;
        private Movie movie;    // Animated GIF must be created as a Movie object
        private boolean isVisible; // Variable to check if the live wallpaper is visible
        private Handler handler; // Handler object is responsible for drawing the wallpaper

        public LiveWallpaperEngine(Movie movie)
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

                // Draw live wallpaper
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
