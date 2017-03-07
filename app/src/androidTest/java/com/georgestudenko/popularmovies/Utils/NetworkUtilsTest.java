package com.georgestudenko.popularmovies.Utils;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Created by george on 06/03/2017.
 */
@RunWith(AndroidJUnit4.class)
public class NetworkUtilsTest {
    @Test
    public void buildResourcesURLTest() throws Exception {
        String expected = "https://api.themoviedb.org/3/movie/330459/videos?api_key=9a90b15711fbb035ca8525853f05faab";
        String result = NetworkUtils.buildResourcesURL("330459","videos");
        assertEquals(expected,result);
    }
}