package nl.sjtek.control.data.actions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by wouter on 1-12-16.
 */
public class ArgumentsTest {
    private static final String TEST_URL = "https://open.spotify.com/track/7tQK6Kw2ki78tNfIFCB3CY";
    private static final String TEST_URL_ENCODED = "https%3A%2F%2Fopen.spotify.com%2Ftrack%2F7tQK6Kw2ki78tNfIFCB3CY";
    private static final String EMPTY = "?user=default";

    @Test
    public void empty() throws Exception {
        assertEquals(Arguments.empty().build(), EMPTY);
    }

    @Test
    public void isUseVoice() throws Exception {
        Arguments withVoice = new Arguments().setUseVoice(true);
        Arguments withoutVoice = new Arguments().setUseVoice(false);
        assertEquals(withVoice.isUseVoice(), true);
        assertEquals(withoutVoice.isUseVoice(), false);
    }

    @Test
    public void setUseVoice() throws Exception {
        assertEquals(new Arguments().setUseVoice(true).build(), "?voice&user=default");
        assertEquals(new Arguments().setUseVoice(false).build(), EMPTY);
    }

    @Test
    public void getUser() throws Exception {
        assertEquals(new Arguments().setUser("wouter").getUser(), "wouter");
        assertEquals(new Arguments().setUser("").getUser(), "");
        assertEquals(new Arguments().setUser(null).getUser(), "");
    }

    @Test
    public void setUser() throws Exception {
        assertEquals(new Arguments().setUser("wouter").build(), "?user=wouter");
        assertEquals(new Arguments().setUser("").build(), EMPTY);
    }

    @Test
    public void getUrl() throws Exception {
        assertEquals(new Arguments().setUrl(TEST_URL).getUrl(), TEST_URL);
        assertEquals(new Arguments().setUrl("").getUrl(), "");
        assertEquals(new Arguments().setUrl(null).getUrl(), "");
    }

    @Test
    public void setUrl() throws Exception {
        assertEquals(new Arguments().setUrl(TEST_URL).build(), "?url=" + TEST_URL_ENCODED + "&user=default");
        assertEquals(new Arguments().setUrl("").build(), EMPTY);
        assertEquals(new Arguments().setUrl(null).build(), EMPTY);
    }

    @Test
    public void build() throws Exception {
        assertEquals(new Arguments()
                        .setUrl(TEST_URL)
                        .setUseVoice(true)
                        .setUser("wouter")
                        .build(),
                "?voice&url=" + TEST_URL_ENCODED + "&user=wouter");
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(new Arguments()
                        .setUrl(TEST_URL)
                        .setUseVoice(true)
                        .setUser("wouter")
                        .toString(),
                "?voice&url=" + TEST_URL_ENCODED + "&user=wouter");
    }
}