package com.backupmanager.app;

import android.content.Context;
import android.os.Environment;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.backupmanager.data.LocalFiles;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.backupmanager.app", appContext.getPackageName());
        stringTest();
    }

    public void stringTest(){
        String s = "/storage/emulated/0/Notifications/Messenger/";

        s = s.substring(0, s.lastIndexOf("/"));
        s = s.substring(0, s.lastIndexOf("/"));
        System.out.println(s);
    }


}