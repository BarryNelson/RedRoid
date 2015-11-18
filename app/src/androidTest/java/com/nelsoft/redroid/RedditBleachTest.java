package com.nelsoft.redroid;



import junit.framework.TestCase;

/**
 * Created by barry on 11/17/15.
 */
public class RedditBleachTest extends TestCase{

    public void testCheckUrl(){
        String res = RedditBleach.getInstance().findGraphicObject("http://i.imgur.com/meMUn3R");
        assertEquals("http://i.imgur.com/meMUn3R.gif",res);
    }
}
